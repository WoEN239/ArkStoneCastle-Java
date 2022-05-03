package org.woen.brick2;

import static lejos.nxt.SensorPort.*;
import org.woen.AbstractBrick;
import org.woen.Subsystem;
import org.woen.fixedtetrix.FixedTetrixControllerFactory;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.CruizcoreGyro;

public class Brick2 extends AbstractBrick {

	private static Drivetrain drivetrain;
	private static RS485Receiver receiver;

	@Override
	public void initHardware() {

		NXTMotor intakeL = new NXTMotor(MotorPort.A);
		NXTMotor intakeC = new NXTMotor(MotorPort.B);
		NXTMotor intakeR = new NXTMotor(MotorPort.C);
		FixedTetrixControllerFactory controllerFactory = new FixedTetrixControllerFactory(S1);
		UltrasonicSensor ultrasonic = new UltrasonicSensor(S2);
		CruizcoreGyro gyro = new CruizcoreGyro(S3);

		receiver = new RS485Receiver();

		Intake intake = new Intake(intakeL, intakeC, intakeR);
		Barrier barrier = new Barrier(receiver, controllerFactory);
		WallSensor wallSensor = new WallSensor(ultrasonic);
		drivetrain = new Drivetrain(controllerFactory, gyro, wallSensor);

		allSystems = new Subsystem[] { receiver, barrier, wallSensor, drivetrain, intake };
	}

	public static void main(String[] args) {
		StrategyThread thread = new StrategyThread();
		thread.start();
		new Brick2().run("Castle");
		thread.interrupt();
	}

	public static void waitCompletion() {
		while (drivetrain.getStatus() == Drivetrain.STATUS_MOVING && !Thread.currentThread().isInterrupted())
			Thread.yield();
	}

	public static void moveToWall() {
		drivetrain.moveToWall();
		waitCompletion();
	}

	public static void move(float distanceCm) {
		drivetrain.move(distanceCm);
		waitCompletion();
	}

	public static void rotate(float angleDegrees) {
		drivetrain.rotate(angleDegrees);
		waitCompletion();
	}

	public static void rotateAbsolute(float angleDegrees) {
		drivetrain.rotateAbsolute(angleDegrees);
		waitCompletion();
	}

	public static class StrategyThread extends Thread {

		@Override
		public void run() {
			while (!started) {
				if (Thread.interrupted())
					return;
				Thread.yield();
			}
			rotateAbsolute(45); // Big triangle
			moveToWall();
			rotateAbsolute(180);
			moveToWall();
			rotateAbsolute(-90);
			moveToWall();

			rotateAbsolute(-5); // Big square
			moveToWall();
			rotate(90);
			moveToWall();
			rotate(90);
			moveToWall();
			rotate(90);
			moveToWall();

			while (!Thread.interrupted()) {
				// Random angle from +- 60 to +- 180
				rotate((Math.random() > 0.5 ? -1f : 1f) * (60f + (float) Math.random() * 120f));
				moveToWall();
				if (receiver.getTimeSinceLastRobotOnBaseMillis() > 50 * 1000) {
					if (Math.random() > 0.5) {
						rotateAbsolute(180); // Try return to base
						moveToWall();
						rotateAbsolute(-90);
						moveToWall();
					} else {
						rotateAbsolute(-90); // Try return to base (other method)
						moveToWall();
						rotateAbsolute(180);
						moveToWall();
					}
				}
			}
			stopRequested = true;
		}
	}
}
