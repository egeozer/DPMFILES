package wallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	private int distError;
	int positiveErrorDist;
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
		
		
	}
	
	@Override
	public void processUSData(int distance) {

		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		// (n.b. this was not included in the Bang-bang controller, but easily
		// could have).
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}
		
		
		

		// TODO: process a movement based on the us distance passed in (P style)
		
		// Controller

		

		distError= bandCenter-distance;			// Compute error

		

		if (Math.abs(distError) <= bandwidth) {	// Within limits, same speed

			leftMotor.setSpeed(motorStraight);		// Start moving forward

			rightMotor.setSpeed(motorStraight);

			leftMotor.forward();

			rightMotor.forward();				

		}

		

		else if (distError > 0) {				// Too close to the wall
			//System.out.println(distError);
			

			
				leftMotor.setSpeed(motorStraight+15*distError);
			

			rightMotor.setSpeed(motorStraight+5*distError);

			leftMotor.forward();

			rightMotor.backward();		
			}

		

		

		else if (distError < 0) {				// Too far from the wall
			positiveErrorDist = -distError;
			if(positiveErrorDist >=200)
				positiveErrorDist = 200;
			//System.out.println(positiveErrorDist);
			
			
				leftMotor.setSpeed(motorStraight+2*positiveErrorDist);

				rightMotor.setSpeed(motorStraight+5*positiveErrorDist);
				
			leftMotor.forward();

			rightMotor.forward();	
			

		}			

	}

	

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
