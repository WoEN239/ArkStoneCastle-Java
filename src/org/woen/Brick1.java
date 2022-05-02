package org.woen;

import static lejos.util.Delay.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.woen.DisplayUtils;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;

public class Brick1 {

	public static void main(String[] args) {
		DisplayUtils display = new DisplayUtils(new Graphics());
		display.displayLogo();
		msDelay(1000);
		display.clear();

		NXTConnection connection = null;
		Sound.beep();
		System.out.println("RS485 Connecting");
		connection = RS485.getConnector().waitForConnection(0, NXTConnection.RAW);
		
		DataInputStream brick2InputStream;
		DataOutputStream brick2OutputStream;
		
		if (connection != null) {
			Sound.beepSequenceUp();
			System.out.println("Connected");

			brick2InputStream = connection.openDataInputStream();
			brick2OutputStream = connection.openDataOutputStream();
		} else {
			Sound.buzz();
			System.out.println("No RS485 connection");
			Button.waitForAnyPress();
			return;
		}
		
		Button.waitForAnyPress();
	}

}
