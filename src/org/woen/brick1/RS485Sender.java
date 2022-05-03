package org.woen.brick1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.woen.Subsystem;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;

public class RS485Sender implements Subsystem {

	private NXTConnection connection = null;
	// private DataInputStream brick2InputStream = null;
	private DataOutputStream brick2OutputStream = null;

	private boolean exceptionHappened = false;

	private boolean valueToTransmit = false;
	private boolean lastTransmittedValue = false;
	private long lastTransmissionTime = 0;

	private static final long TRANSMISSION_PERIOD_MS = 250;

	@Override
	public void initialize() {
		while (connection == null) {
			Sound.beep();
			System.out.println("RS485 Connecting");
			connection = RS485.getConnector().waitForConnection(0, NXTConnection.RAW);

			if (connection != null) {
				System.out.println("Connected");

				// brick2InputStream = connection.openDataInputStream();
				brick2OutputStream = connection.openDataOutputStream();
			} else {
				Sound.buzz();
				System.out.println("E: No NXT conn");
				System.out.println("Enter to retry");
				System.out.println("Esc to skip");
				int button = Button.waitForAnyPress();
				if (button == Button.ID_ENTER)
					System.out.println("Retrying...");
				else if (button == Button.ID_ESCAPE)
					return;
			}
		}
	}

	@Override
	public void update() {
		if (brick2OutputStream != null)
			try {
				if (valueToTransmit != lastTransmittedValue
						|| System.currentTimeMillis() - lastTransmissionTime > TRANSMISSION_PERIOD_MS) {
					brick2OutputStream.writeBoolean(valueToTransmit);
					brick2OutputStream.flush();
					lastTransmissionTime = System.currentTimeMillis();
					lastTransmittedValue = valueToTransmit;
					exceptionHappened = false;
				}
			} catch (IOException ignored) {
				if (!exceptionHappened) {
					exceptionHappened = true;
					Sound.buzz();
					System.err.println("RS485 Err");
				}
			}
	}

	@Override
	public void stop() {
		if (connection != null) {
			if (brick2OutputStream != null) {
				try {
					brick2OutputStream.close();
				} catch (IOException ignored) {
				}
			}
			connection.close();
		}

	}

	public void setValueToTransmit(boolean valueToTransmit) {
		this.valueToTransmit = valueToTransmit;
	}

}
