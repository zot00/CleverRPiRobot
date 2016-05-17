package org.jointheleague.ecolban.cleverrobot;

import java.io.IOException;

import org.jointheleague.ecolban.rpirobot.IRobotAdapter;
import org.jointheleague.ecolban.rpirobot.IRobotInterface;
import org.jointheleague.ecolban.rpirobot.SimpleIRobot;

public class CleverRobot extends IRobotAdapter {

    // The following measurements are taken from the interface specification
    private static final double WHEEL_DISTANCE = 235.0; // in mm
    private static final double WHEEL_DIAMETER = 72.0; // in mm
    private static final double ENCODER_COUNTS_PER_REVOLUTION = 508.8;

    private static final int STRAIGHT_SPEED = 200;
    private static final int TURN_SPEED = 100;

    private final boolean debug = true; // Set to true to get debug messages.
    private volatile boolean running;

    public CleverRobot(IRobotInterface iRobot) {
        super(iRobot);
        System.out.println("Hello. I'm CleverRobot");
    }

    public static void main(String[] args) throws IOException {
        CleverRobot rob = null;
        try {
            rob = new CleverRobot(new SimpleIRobot());
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
        rob.initialize();
        while (rob.running) {
            rob.loop();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    private int startLeft;
    private int startRight;
    private int countsToGoWheelLeft;
    private int countsToGoWheelRight;
    private int directionLeft;
    private int directionRight;

    private int currentCommand = 0;

    /* This method is executed when the robot first starts up. */
    public void initialize() throws IOException {
        // what would you like me to do, Clever Human?
        System.out.println("Initializing...");
        currentCommand = 0;
        running = true;
        nextCommand();

    }

    /* This method is called repeatedly. */
    public void loop() throws IOException {
        if (checkDone()) {
            nextCommand();
        }
    }

    /**
     * This method determines where to go next. This is a very simple
     * Tortoise-like implementation, but a more advanced implementation could
     * take into account sensory input, maze mapping, and other.
     *
     * @throws ConnectionLostException
     */
    private void nextCommand() throws IOException {
        System.out.println("currentCommand = " + currentCommand);
        switch (currentCommand) {
        case 0:
            goStraight(1000);
            break;
        case 1:
            turnLeft(180);
            break;
        case 2:
            goStraight(1000);
            break;
        case 3:
            turnRight(180);
            break;
        case 4:
            shutDown();
            break;
        default:
        }
        currentCommand++;
    }


    /**
     * Moves the robot in a straight line. Note: Unexpected behavior may occur
     * if distance is larger than 14567mm.
     *
     * @param distance
     *            the distance to go in mm. Must be &le; 14567.
     */
    private void goStraight(int distance) throws IOException {
        countsToGoWheelLeft = (int) (distance * ENCODER_COUNTS_PER_REVOLUTION / (Math.PI * WHEEL_DIAMETER));
        countsToGoWheelRight = countsToGoWheelLeft;
        if (debug) {
            String msg = String.format("Going straight  L: %d  R: %d", countsToGoWheelLeft, countsToGoWheelRight);
            System.out.println(msg);
        }
        directionLeft = 1;
        directionRight = 1;
        recordEncodersAndDrive(directionLeft * STRAIGHT_SPEED, directionRight * STRAIGHT_SPEED);
    }

    /**
     * Turns in place rightwards. Note: Unexpected behavior may occur if degrees
     * is larger than 7103 degrees (a little less than 20 revolutions).
     *
     * @param degrees
     *            the number of degrees to turn. Must be &le; 7103.
     */
    private void turnRight(int degrees) throws IOException {
        countsToGoWheelRight = (int) (degrees * WHEEL_DISTANCE * ENCODER_COUNTS_PER_REVOLUTION
                / (360.0 * WHEEL_DIAMETER));
        countsToGoWheelLeft = countsToGoWheelRight;
        directionLeft = 1;
        directionRight = -1;
        recordEncodersAndDrive(directionLeft * TURN_SPEED, directionRight * TURN_SPEED);
        if (debug) {
            String msg = String.format("Turning right  L: %d  R: %d", countsToGoWheelLeft, countsToGoWheelRight);
            System.out.println(msg);
        }
    }

    /**
     * Turns in place leftwards. Note: Unexpected behavior may occur if degrees
     * is larger than 7103 degrees (a little less than 20 revolutions).
     *
     * @param degrees
     *            the number of degrees to turn. Must be &le; 7103.
     */
    private void turnLeft(int degrees) throws IOException {
        countsToGoWheelRight = (int) (degrees * WHEEL_DISTANCE * ENCODER_COUNTS_PER_REVOLUTION
                / (360.0 * WHEEL_DIAMETER));
        countsToGoWheelLeft = countsToGoWheelRight;
        if (debug) {
            String msg = String.format("Turning left  L: %d  R: %d", countsToGoWheelLeft, countsToGoWheelRight);
            System.out.println(msg);
        }
        directionLeft = -1;
        directionRight = 1;
        recordEncodersAndDrive(directionLeft * TURN_SPEED, directionRight * TURN_SPEED);
    }

    private void recordEncodersAndDrive(int leftVelocity, int rightVelocity) throws IOException {
        readSensors(SENSORS_GROUP_ID101);
        startLeft = getEncoderCountLeft();
        startRight = getEncoderCountRight();
        driveDirect(leftVelocity, rightVelocity);
    }

    /**
     * Checks if the last command has been completed.
     *
     * @return true if the last command has been completed
     * @throws ConnectionLostException
     */
    private boolean checkDone() throws IOException {
        readSensors(SENSORS_GROUP_ID101);
        int countLeft = getEncoderCountLeft();
        int countRight = getEncoderCountRight();
        boolean done = false;
        int doneLeft = (directionLeft * (countLeft - startLeft)) & 0xFFFF;
        int doneRight = (directionRight * (countRight - startRight)) & 0xFFFF;
        if (debug) {
            String msg = String.format("L: %d  R: %d", doneLeft, doneRight);
            System.out.println(msg);
        }
        if (countsToGoWheelLeft <= doneLeft && doneLeft < 0x7FFF
                || countsToGoWheelRight <= doneRight && doneRight < 0x7FFF) {
            driveDirect(0, 0);
            waitForCompleteStop();
            done = true;
        }
        return done;
    }

    private void waitForCompleteStop() throws IOException {
        boolean done = false;
        int prevCountLeft = -1;
        int prevCountRight = -1;
        while (!done) {
            readSensors(SENSORS_GROUP_ID101);
            int countLeft = getEncoderCountLeft();
            int countRight = getEncoderCountRight();
            if (debug) {
                String msg = String.format("Stopping  L: %d  R: %d", countLeft, countRight);
                System.out.println(msg);
            }
            if (prevCountLeft == countLeft && prevCountRight == countRight) {
                done = true;
            } else {
                prevCountLeft = countLeft;
                prevCountRight = countRight;
            }
        }
    }

    private void shutDown() throws IOException {
        running = false;
        System.out.println("Shutting down... Bye!");
        stop();
        closeConnection();
    }

}
