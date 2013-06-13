package trucksim;

import processing.core.*;

//import java.util.*;

public class TruckSim extends PApplet
{
	private static final long serialVersionUID = 1L;

	final int numTrucks = 1;
	Truck Truck[] = new Truck[numTrucks];

	static final int windowWidth = 960;
	static final int windowHeight = 640;

	float lastTimeMillis = 0.0f;
	
	short windDirection = -10000;

	public void setup()
	{
		size( 960, 640 );
		smooth();

		for( int i = 0; i < numTrucks; i++ )
		{
			Truck[i] = new Truck( this );
		}
		// make one Truck drift off somewhere
		//Truck[6].m_dadt = 0;
		
		StdNormDist.Initialise( this );
	}
	
	void process( float Dt )
	{
		// Truck UPDATE
		for( int i = 0; i < numTrucks; i++ )
		{
			Truck[i].Update();
		}
	}

	public void DisplayTrucks()
	{
		for( int i = 0; i < numTrucks; i++ )
		{
			scale( 2.0f );

			fill( color( 255, 255, 255 ) );
			// body
			rect( Truck[i].GetStateX() - 10, 200 - 4, 20, 8 );
			triangle( Truck[i].GetStateX() + 10, 200 - 4, Truck[i].GetStateX() + 14, 200, Truck[i].GetStateX() + 10, 200 + 4 );

			fill( color( 255, 0, 0 ) );
			// body
			rect( Truck[i].GetMeasurementX() - 10, 200 - 4, 20, 8 );
			triangle( Truck[i].GetMeasurementX() + 10, 200 - 4, Truck[i].GetMeasurementX() + 14, 200, Truck[i].GetMeasurementX() + 10, 200 + 4 );

			fill( color( 0, 0, 255 ) );
			// body
			rect( Truck[i].GetStateEstimateX() - 10, 200 - 4, 20, 8 );
			triangle( Truck[i].GetStateEstimateX() + 10, 200 - 4, Truck[i].GetStateEstimateX() + 14, 200, Truck[i].GetStateEstimateX() + 10, 200 + 4 );

			fill( color( 0, 255, 0 ) );
			// body
			rect( Truck[i].GetIIRFilterX() - 10, 200 - 4, 20, 8 );
			triangle( Truck[i].GetIIRFilterX() + 10, 200 - 4, Truck[i].GetIIRFilterX() + 14, 200, Truck[i].GetIIRFilterX() + 10, 200 + 4 );

			resetMatrix();
		}
	}

	public void draw()
	{
//		float Dt = ( millis() - lastTimeMillis ) / 1000.0f;
//		lastTimeMillis = millis();

		process( 0.0666f );

		background( 200 );

		// display Truck
		DisplayTrucks();
	}
}
