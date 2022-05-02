package org.woen;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import lejos.nxt.LCD;

public class DisplayUtils {
	
	private final Graphics graphics;
	
	public DisplayUtils(Graphics graphics) {
		this.graphics = graphics;
		graphics.setFont(Font.getLargeFont());
	}

	public void displayLogo() {
		graphics.drawString("WoEN 239", LCD.SCREEN_WIDTH/2, LCD.SCREEN_HEIGHT/2 - graphics.getFont().getHeight()/2, Graphics.HCENTER | Graphics.VCENTER);
		try {
			Image image = Image.createImage(new FileInputStream(new File ("WoEN Logo NXT.ini")));
			graphics.drawImage(image, 0, 0, 0);
		} catch (IOException ignored) {}
	}
	
	public void clear() {
		graphics.clear();
	}
}
