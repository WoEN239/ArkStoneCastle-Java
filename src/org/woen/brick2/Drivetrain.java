package org.woen.brick2;

import static lejos.nxt.addon.tetrix.TetrixMotorController.MOTOR_1;
import static lejos.nxt.addon.tetrix.TetrixMotorController.MOTOR_2;
import static lejos.util.Delay.msDelay;

import org.woen.ExceptionUtils;
import org.woen.Subsystem;
import org.woen.fixedtetrix.FixedTetrixControllerFactory;
import org.woen.fixedtetrix.FixedTetrixEncoderMotor;
import org.woen.fixedtetrix.FixedTetrixMotorController;

import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.CruizcoreGyro;

public class Drivetrain implements Subsystem {

	private final FixedTetrixMotorController controller;
	private final FixedTetrixEncoderMotor motorLeft;
	private final FixedTetrixEncoderMotor motorRight;
	private final CruizcoreGyro gyro;
	private final WallSensor wallSensor;

	private static final long TIMEOUT_MILLIS = 4000;

	private static final float ANGLE_THRESHOLD = 5f;
	private static final float DISTANCE_THRESHOLD = 10f;

	private static final float GYRO_ANGLE_SCALE = 100.0f;

	private static final float WHEEL_DIAMETER_CM = 10.90532f;
	private static final int ENCODER_RESOLUTION = 24;
	private static final float GEARBOX_RATIO = 40.0f;
	private static final float ENCODER_TICKS_TO_CM_RATIO = (float) ((WHEEL_DIAMETER_CM * Math.PI)
			/ (ENCODER_RESOLUTION * GEARBOX_RATIO));

	public Drivetrain(FixedTetrixControllerFactory factory, CruizcoreGyro gyro, WallSensor wallSensor) {
		FixedTetrixMotorController controller = null;
		FixedTetrixEncoderMotor motorLeft = null;
		FixedTetrixEncoderMotor motorRight = null;
		try {
			controller = factory.newMotorController();
			motorLeft = controller.getEncoderMotor(MOTOR_1);
			motorRight = controller.getEncoderMotor(MOTOR_2);
		} catch (IllegalStateException e) {
			ExceptionUtils.fatalException("No motor controller");
		}
		this.motorLeft = motorLeft;
		this.motorRight = motorRight;
		this.controller = controller;
		this.gyro = gyro;
		this.wallSensor = wallSensor;
		if (!gyro.readAllData())
			ExceptionUtils.fatalException("Gyro error");
	}

	public static final int STATUS_UNKNOWN = -1;
	public static final int STATUS_MOVING = 0;
	public static final int STATUS_TIMEOUT_EXCEEDED = 1;
	public static final int STATUS_AT_TARGET = 2;

	private int status = STATUS_UNKNOWN;
	private long targetUpdateMillis = 0;

	public int getStatus() {
		return status;
	}

	@Override
	public void initialize() {
		{
			gyro.reset();
			gyro.setAccScale2G();
		}
		{
			motorLeft.setReverse(false);
			motorRight.setReverse(true);
			motorLeft.forward();
			motorRight.forward();
			motorLeft.setPower(-3);
			motorRight.setPower(-3);
			msDelay(25);
			controller.sendData(0x44, (byte) 0x0);
			controller.sendData(0x47, (byte) 0x0);
			motorLeft.setPower(0);
			motorRight.setPower(0);
		}
		{
			rotateAbsolute(45);
			while (status == STATUS_MOVING) {
				update();
			}
			motorLeft.setPower(0);
			motorRight.setPower(0);
			if (status != STATUS_AT_TARGET) {
				System.err.println("E: Drivetrain can't");
				System.err.println("reach position");
			}
		}
	}

	float angleTarget = 0;

	public int cmToEncoderTicks(float centimeters) {
		return (int) (centimeters / ENCODER_TICKS_TO_CM_RATIO);
	}

	public float encoderTicksToCm(float ticks) {
		return ticks * ENCODER_TICKS_TO_CM_RATIO;
	}

	private void setAngleTarget(float target) {
		angleTarget = target;
	}

	private float getAngleError() {
		float error = angleTarget - gyro.getAngle() / GYRO_ANGLE_SCALE;
		if (error > 180.0f)
			do
				error -= 360.0f;
			while (error > 180.0f);
		else if (error < -180.0f)
			do
				error += 360.0f;
			while (error < -180.0f);
		return error;
	}

	int leftEncoderTarget = 0;
	int rightEncoderTarget = 0;

	private void setDistanceTarget(float distance) {
		int distanceIncrement = cmToEncoderTicks(distance);
		leftEncoderTarget = motorLeft.getTachoCount() + distanceIncrement;
		rightEncoderTarget = motorRight.getTachoCount() + distanceIncrement;
	}

	private float getDistanceError() {
		return encoderTicksToCm(
				(motorLeft.getTachoCount() - leftEncoderTarget + motorRight.getTachoCount() - rightEncoderTarget)
						* .5f);
	}

	boolean stopAtWall = false;

	public void moveToWall() {
		stopAtWall = true;
		setDistanceTarget(300);
		status = STATUS_MOVING;
		targetUpdateMillis = System.currentTimeMillis();
	}

	public void move(float distanceCm) {
		stopAtWall = false;
		setDistanceTarget(distanceCm);
		status = STATUS_MOVING;
		targetUpdateMillis = System.currentTimeMillis();
	}

	public void rotate(float angleDegrees) {
		rotateAbsolute(angleTarget + angleDegrees);
	}

	public void rotateAbsolute(float angleDegrees) {
		stopAtWall = false;
		setAngleTarget(angleDegrees);
		status = STATUS_MOVING;
		targetUpdateMillis = System.currentTimeMillis();
	}

	float angle_kP = 1.5f;
	float distance_kP = 3f;

	@Override
	public void update() {

		float angleError = getAngleError();

		boolean doStopAtWall = stopAtWall && wallSensor.isRobotNearWall();

		float distanceError = doStopAtWall ? 0 : getDistanceError();

		float angleSignal = angleError * angle_kP;
		float distanceSignal = distanceError * distance_kP;

		if (Math.abs(angleError) < ANGLE_THRESHOLD && Math.abs(distanceError) < DISTANCE_THRESHOLD || doStopAtWall)
			status = STATUS_AT_TARGET;

		if (status != STATUS_AT_TARGET && System.currentTimeMillis() - targetUpdateMillis > TIMEOUT_MILLIS)
			status = STATUS_TIMEOUT_EXCEEDED;

		motorLeft.setPower((int) (distanceSignal + angleSignal));
		motorRight.setPower((int) (distanceSignal - angleSignal));

	}

	private void stopMotors() {
		motorLeft.flt();
		motorRight.flt();
	}

	@Override
	public void stop() {
		stopMotors();
	}

}
