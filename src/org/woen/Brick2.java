package org.woen;

import static lejos.nxt.SensorPort.S1;
import static lejos.nxt.addon.tetrix.TetrixMotorController.MOTOR_1;
import static lejos.nxt.addon.tetrix.TetrixMotorController.MOTOR_2;
import static lejos.nxt.addon.tetrix.TetrixServoController.SERVO_1;
import static lejos.util.Delay.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.woen.DisplayUtils;
import org.woen.fixedtetrix.FixedTetrixControllerFactory;
import org.woen.fixedtetrix.FixedTetrixEncoderMotor;
import org.woen.fixedtetrix.FixedTetrixMotorController;
import org.woen.fixedtetrix.FixedTetrixServo;
import org.woen.fixedtetrix.FixedTetrixServoController;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.addon.tetrix.TetrixControllerFactory;
import lejos.nxt.addon.tetrix.TetrixEncoderMotor;
import lejos.nxt.addon.tetrix.TetrixMotor;
import lejos.nxt.addon.tetrix.TetrixMotorController;
import lejos.nxt.addon.tetrix.TetrixRegulatedMotor;
import lejos.nxt.addon.tetrix.TetrixServo;
import lejos.nxt.addon.tetrix.TetrixServoController;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;

public class Brick2 {

	public static void main(String[] args) {
		DisplayUtils display = new DisplayUtils(new Graphics());
		display.displayLogo();
		msDelay(1000);
		display.clear();

		NXTConnection connection = null;
		Sound.beep();
		System.out.println("RS485 Connecting");
		connection = RS485.getConnector().connect("Brick1", NXTConnection.RAW);

		DataInputStream brick1InputStream;
		DataOutputStream brick1OutputStream;

		if (connection != null) {
			Sound.beepSequenceUp();
			System.out.println("Connected");

			brick1InputStream = connection.openDataInputStream();
			brick1OutputStream = connection.openDataOutputStream();
		} else {
			Sound.buzz();
			System.out.println("No NXT conn");
			Button.waitForAnyPress(3000);
			// return;
		}

		FixedTetrixEncoderMotor motorLeft;
		FixedTetrixEncoderMotor motorRight;
		FixedTetrixMotorController tetrixMotorController;
		FixedTetrixServo barrierServo;

		try {
			FixedTetrixControllerFactory tetrixControllerFactory = new FixedTetrixControllerFactory(S1);
			tetrixMotorController = tetrixControllerFactory.newMotorController();
			FixedTetrixServoController tetrixServoController = tetrixControllerFactory.newServoController();
			motorLeft = tetrixMotorController.getEncoderMotor(MOTOR_1);
			motorRight = tetrixMotorController.getEncoderMotor(MOTOR_2);
			motorLeft.forward();
			motorRight.forward();
			motorLeft.setReverse(false);
			motorRight.setReverse(true);
			motorLeft.setPower(-3);
			motorRight.setPower(-3);
			msDelay(25);
			tetrixMotorController.sendData(0x44,(byte)0x0);
			tetrixMotorController.sendData(0x47,(byte)0x0);
			motorLeft.setPower(0);
			motorRight.setPower(0);
			barrierServo = tetrixServoController.getServo(SERVO_1);
		} catch (IllegalStateException e) {
			Sound.buzz();
			System.out.println("No TETRIX Ctrlr");
			Button.waitForAnyPress(3000);
			return;
		}

		Button.waitForAnyPress(1000 * 30);
	}

}
