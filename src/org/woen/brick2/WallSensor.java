package org.woen.brick2;

import org.woen.Subsystem;

import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class WallSensor implements Subsystem {
	
	private final UltrasonicSensor ultrasonic;
	private long lastCaptureTimeMillis = 0;
	private boolean robotNearWall = false;
	private int distance = 255;
	private static final int DISTANCE_TO_WALL_THRESHOLD = 15;
	private static final long CAPTURE_DELAY_MS = 30;

	public WallSensor(UltrasonicSensor ultrasonic) {
		this.ultrasonic = ultrasonic;
		if(ultrasonic.reset() < 0) {
			Sound.buzz();
			System.err.println("E: No ultrasonic");
		}
	}

	@Override
	public void initialize() {
		ultrasonic.reset();
		ultrasonic.continuous();
	}

	@Override
	public void update() {
		if(System.currentTimeMillis() - lastCaptureTimeMillis> CAPTURE_DELAY_MS) {
			distance = ultrasonic.getDistance();
			robotNearWall = distance < DISTANCE_TO_WALL_THRESHOLD;
			lastCaptureTimeMillis = System.currentTimeMillis();
		}
	}

	@Override
	public void stop() {
		ultrasonic.off();
		
	}

	public boolean isRobotNearWall() {
		return robotNearWall;
	}
	
	public int getDistance() {
		return distance;
	}

}
