package org.jointheleague.ecolban.cleverrobot;

import java.io.IOException;

import org.jointheleague.ecolban.rpirobot.IRobotAdapter;
import org.jointheleague.ecolban.rpirobot.IRobotInterface;
import org.jointheleague.ecolban.rpirobot.SimpleIRobot;

public class CleverRobot extends IRobotAdapter implements Runnable {

	// The following measurements are taken from the interface specification

	private boolean running;
	private final boolean debug = true; // Set to true to get debug messages.

	public CleverRobot(IRobotInterface iRobot) {
		super(iRobot);
		if (debug) {
			System.out.println("Hello. I'm CleverRobot");
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			IRobotInterface base = new SimpleIRobot();
			CleverRobot rob = new CleverRobot(base);
			rob.initialize();
			rob.run();
		} catch (InterruptedException | IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/* This method is executed when the robot first starts up. */
	private void initialize() throws IOException {
		// what would you like me to do, Clever Human?
		if(debug){
			System.out.println("Initializing...");
		}
	}

	public void run() {
		int leftSpeed = 500;
		int rightSpeed = 500;
		running = true;
		while (running) {
			try {
				driveDirect(leftSpeed, rightSpeed);
				Thread.sleep(20);
				readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);
				if (isBumpLeft() && isBumpRight()) {
					// adjust the wheel speeds
				}
				if (isBumpLeft()) {
					// adjust the wheel speeds
				}
				if (isBumpRight()) {
					// adjust the wheel speeds
				}
				if (debug) {
					System.out.println(String.format(
							"Left bumber = %s, Right bumber = %s",
							isBumpLeft(), isBumpRight()));
					System.out.println(String.format(
							"Left wheel speed = %d, Right wheel speed = %d",
							leftSpeed, rightSpeed));
				}
				// ...
				// if (/* goal reached */){
				// running = false;
				// }
				
				

			} catch (IOException | InterruptedException e) {
				running = false;
			}
		}

		try {
			shutDown();
		} catch (IOException e) {
		}

	}

	private void shutDown() throws IOException {
		stop();
		closeConnection();
	}

}
