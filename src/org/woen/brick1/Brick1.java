package org.woen.brick1;

import static lejos.nxt.SensorPort.*;

import org.woen.AbstractBrick;
import org.woen.Subsystem;

public class Brick1 extends AbstractBrick {

	@Override
	public void initHardware() {

		RS485Sender sender = new RS485Sender();

		allSystems = new Subsystem[] { sender };
	}

	public static void main(String[] args) {
		new Brick1().run("ArkStone");
	}
}
