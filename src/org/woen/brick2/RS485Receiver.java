package org.woen.brick2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.woen.Subsystem;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;

public class RS485Receiver implements Subsystem {

	private boolean robotOnBase = false;

	private long lastRobotOnBaseMillis = System.currentTimeMillis();

	private NXTConnection connection = null;
	private DataInputStream brick1InputStream = null;
	// DataOutputStream brick1OutputStream = null;

	@Override
	public void initialize() {
		while (connection == null) {
			Sound.beep();
			System.out.println("RS485 Connecting");
			connection = RS485.getConnector().connect("Brick1", NXTConnection.RAW);

			if (connection != null) {
				System.out.println("Connected");

				brick1InputStream = connection.openDataInputStream();
				// brick1OutputStream = connection.openDataOutputStream();
			} else {
				Sound.buzz();
				System.out.println("E: No NXT conn");
				System.out.println("Retry in 5s");
				System.out.println("Enter to skip");
				if (Button.waitForAnyPress(5000) == Button.ID_ENTER)
					return;
			}
		}
	}

	@Override
	public void update() {
		if (brick1InputStream != null)
			try {
				while (brick1InputStream.available() > 0)
					robotOnBase = brick1InputStream.readBoolean();
			} catch (IOException e) {
				robotOnBase = false;
			}
		if (robotOnBase)
			lastRobotOnBaseMillis = System.currentTimeMillis();
	}

	@Override
	public void stop() {
		if (connection != null) {
			if (brick1InputStream != null) {
				try {
					brick1InputStream.close();
				} catch (IOException ignored) {
				}
			}
			connection.close();
		}

	}

	public long getTimeSinceLastRobotOnBaseMillis() {
		return System.currentTimeMillis() - lastRobotOnBaseMillis;
	}

	public boolean isRobotOnBase() {
		return robotOnBase;
	}

}
