package org.woen.brick2;

import org.woen.Subsystem;
import org.woen.fixedtetrix.FixedTetrixControllerFactory;
import org.woen.fixedtetrix.FixedTetrixServo;
import org.woen.fixedtetrix.FixedTetrixServoController;
import static org.woen.fixedtetrix.FixedTetrixServoController.SERVO_1;

import org.woen.ExceptionUtils;;

public class Barrier implements Subsystem {

	private final FixedTetrixServoController controller;
	private final FixedTetrixServo servo;
	private final RS485Receiver receiver;

	private static final int PWM_OPEN = 1500;
	private static final int PWM_CLOSE = 1500;

	public Barrier(RS485Receiver receiver, FixedTetrixControllerFactory factory) {
		this.receiver = receiver;
		FixedTetrixServoController controller = null;
		FixedTetrixServo servo = null;
		try {
			controller = factory.newServoController();
			servo = controller.getServo(SERVO_1);
		} catch (IllegalStateException e) {
			ExceptionUtils.fatalException("No servo controller");
		}
		this.controller = controller;
		this.servo = servo;
	}

	@Override
	public void initialize() {
		servo.setpulseWidth(PWM_CLOSE);
	}

	@Override
	public void update() {
		servo.setpulseWidth(receiver.isRobotOnBase() ? PWM_OPEN : PWM_CLOSE);

	}

	@Override
	public void stop() {
		controller.flt();
	}

}
