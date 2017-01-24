/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private EV3ColorSensor sensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
	private SampleProvider sampleProvider;
	int dataSize = sensor.sampleSize();
	public double theta;
	public double x,y;
	public int beepCounter;
	public double gridSize = 30.48;
	public double tempX, tempY;
	
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		sensor.setFloodlight(true);
	
		Sound.setVolume(50);
	}

	// run method (required for Thread)
	public void run() {
		
		long correctionStart, correctionEnd;
		
		
		while (true) {
			float sensorData[] = new float[dataSize];
			correctionStart = System.currentTimeMillis();

			sampleProvider=sensor.getRedMode();
			sampleProvider.fetchSample(sensorData, 0);
			
			//create array to store sensor data then retrieve data	
	
			if(sensorData[0] < .3 ) {	//the robot will beep once it reads a color darker than specified
				
				Sound.beep();	
				beepCounter++;
		
				if(beepCounter == 1 || beepCounter == 4 || beepCounter == 7 || beepCounter == 10){		// when the robot exits the 																// corner squares
			
					x = odometer.getX();																
					y = odometer.getY();
				}
		
				if(beepCounter > 1 && beepCounter < 4){			// as the robot travels up along the first grid side (+y)
		
					tempY = y;
					odometer.setY(tempY + gridSize);
					y = odometer.getY();
				}
		
				if(beepCounter > 4 && beepCounter < 7){			//as the robot travels right along the second grid side (+x)
		
					tempX = x;
					odometer.setX(tempX + gridSize);		
					x = odometer.getX();
				}
		
				if(beepCounter > 7 && beepCounter < 10){		// as the robot travels down along the third grid side (-y)
			
					tempY = y;			
					odometer.setY(tempY - gridSize);		
					y = odometer.getY();	
				}	
		
				if(beepCounter > 10 && beepCounter < 13){		// as the robot travels left along the fourth grid side (-x)			
					
					tempX = x;
					odometer.setX(tempX - gridSize);
					x = odometer.getX();
				}
			}
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}