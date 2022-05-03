package org.woen;

import lejos.nxt.Button;
import lejos.nxt.Sound;

public class ExceptionUtils {

	public static void fatalException(String message) {
		Sound.buzz();
		System.err.println(message);
		Button.waitForAnyPress(15000);
		System.exit(1);
	}
}
