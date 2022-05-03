package org.woen.brick2;

import org.woen.Subsystem;

import lejos.nxt.NXTMotor;

public class Intake implements Subsystem {

	private final NXTMotor motorL;
	private final NXTMotor motorC;
	private final NXTMotor motorR;

	private static final int MOTORL_DIRECTION = 1;
	private static final int MOTORC_DIRECTION = 1;
	private static final int MOTORR_DIRECTION = 1;

	private static final int INIT_POWER = 33;
	private static final int RUN_POWER = 100;

	private boolean started = false;

	public Intake(NXTMotor motorL, NXTMotor motorC, NXTMotor motorR) {
		this.motorL = motorL;
		this.motorC = motorC;
		this.motorR = motorR;
	}

	@Override
	public void initialize() {
		motorL.setPower(MOTORL_DIRECTION * INIT_POWER);
		motorC.setPower(MOTORC_DIRECTION * INIT_POWER);
		motorR.setPower(MOTORR_DIRECTION * INIT_POWER);
	}

	@Override
	public void update() {
		if (!started) {
			motorL.setPower(MOTORL_DIRECTION * RUN_POWER);
			motorC.setPower(MOTORC_DIRECTION * RUN_POWER);
			motorR.setPower(MOTORR_DIRECTION * RUN_POWER);
			started = true;
		}
	}

	@Override
	public void stop() {
		motorL.flt();
		motorC.flt();
		motorR.flt();
	}

}
