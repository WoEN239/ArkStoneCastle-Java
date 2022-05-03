package org.woen;

import static lejos.util.Delay.msDelay;

import javax.microedition.lcdui.Graphics;

import lejos.nxt.Button;
import lejos.nxt.Sound;

public abstract class AbstractBrick {

	protected Subsystem[] allSystems = new Subsystem[] {};

	protected static boolean started = false;

	protected static boolean stopRequested = false;

	public abstract void initHardware();

	public void run(String msg) {
		DisplayUtils display = new DisplayUtils(new Graphics());
		display.displayLogo();
		msDelay(1000);
		display.clear();

		System.out.println("Init hardware...");

		initHardware();

		Sound.beepSequenceUp();
		System.out.println("Press Enter to");
		System.out.println("init subsystems");

		{
			int button = Button.waitForAnyPress(30 * 1000);
			if (button == Button.ID_ESCAPE || button == 0)
				return;
		}

		for (Subsystem system : allSystems)
			system.initialize();

		Sound.twoBeeps();
		System.out.println("Press Enter to");
		System.out.println("Start robot");

		if (Button.waitForAnyPress() == Button.ID_ESCAPE)
			for (Subsystem system : allSystems)
				system.stop();

		display.clear();
		display.displayString(msg);

		started = true;

		int updateTimes = 0;
		long measureTime = System.currentTimeMillis();

		while (Button.ESCAPE.isUp() && !stopRequested) {
			updateTimes++;
			if (System.currentTimeMillis() - measureTime > 10 * 1000) {
				measureTime = System.currentTimeMillis();
				System.out.println(updateTimes / 10.0f);
				updateTimes = 0;
			}
			for (Subsystem system : allSystems)
				// Thread.yield();
				system.update();
		}

		for (Subsystem system : allSystems)
			system.stop();
	}
}
