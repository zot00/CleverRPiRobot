package org.jointheleague.ecolban.cleverrobot;
/*********************************************************************************************
 * Vic's ultrasonic sensor running with Erik's Clever Robot for Pi
 * version 0.9, 170227
 **********************************************************************************************/
import java.io.IOException;

import org.jointheleague.ecolban.rpirobot.IRobotAdapter;
import org.jointheleague.ecolban.rpirobot.IRobotInterface;
import org.jointheleague.ecolban.rpirobot.SimpleIRobot;

public class CleverRobot extends IRobotAdapter
{

    public CleverRobot(IRobotInterface iRobot)
    {
	super(iRobot);
    }

    public static void main(String[] args) throws Exception
    {
	System.out.println("Try event listner, rev Monday 2030");
	IRobotInterface base = new SimpleIRobot();
	CleverRobot rob = new CleverRobot(base);
	rob.getGoing();
    }

    private void getGoing()
    {
	Sonar sonar = new Sonar();
	System.out.println("Try event listner, rev Monday 2030");
	for (int i = 0; i < 100000; i++)
	    {
		    try
			{
			    System.out.print(sonar.readSonar("left") + "     ");
			    Thread.sleep(300);
			    System.out.print(sonar.readSonar("center") + "     ");
			    Thread.sleep(300);
			    System.out.print(sonar.readSonar("right") + "\n");
			    Thread.sleep(300);
			} catch (Exception e)
			{
			    System.out.println("readSonar exception");
			}
	    }
	try
	    {
		shutDown();
	    } catch (IOException e)
	    {
	    }
    }

    private void shutDown() throws IOException
    {
	reset();
	stop();
	closeConnection();
    }
}
