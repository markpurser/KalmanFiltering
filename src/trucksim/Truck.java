package trucksim;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

//----------------------------------------------------------------------------------------------------
//Class: Truck
//----------------------------------------------------------------------------------------------------
public class Truck
{
	private PApplet p;
	
	private final float s_Dt = .03333333f;
	private float s_sigma_a = 20.0f;
	private float s_sigma_z = 40.0f;

	private PVector m_state = new PVector( 0.0f, 20.0f );
	private PVector m_measurement = new PVector( 0.0f, 0.0f );
	
	private PVector m_stateEstimate = new PVector( 0.0f, 20.0f );
	private Matrix2D m_estimateCovariance = new Matrix2D();
	
	private Matrix2D m_F = new Matrix2D();
	private PVector m_G = new PVector( s_Dt*s_Dt*0.5f, s_Dt );
	private Matrix2D m_Q = new Matrix2D();
	private float m_R;

	private final int iir_n = 20;
	private int iir_k = 0;
	private float iirFilter[] = new float[iir_n];

	public Truck( PApplet _p )
	{
		p = _p;

		m_estimateCovariance.m00 = 0.0f;   m_estimateCovariance.m01 = 0.0f;
		m_estimateCovariance.m10 = 0.0f;   m_estimateCovariance.m11 = 0.0f;

		m_F.m00 = 1.0f;   m_F.m01 = s_Dt;
		m_F.m10 = 0.0f;   m_F.m11 = 1.0f;

		// Q = G * GT * sigma_a^2
		m_Q.m00 = m_G.x * m_G.x * s_sigma_a * s_sigma_a;
		m_Q.m01 = m_G.x * m_G.y * s_sigma_a * s_sigma_a;
		m_Q.m10 = m_G.y * m_G.x * s_sigma_a * s_sigma_a;
		m_Q.m11 = m_G.y * m_G.y * s_sigma_a * s_sigma_a;
		
		// R is a scalar value, because H is [1,0] in this instance
		m_R = s_sigma_z * s_sigma_z;
		
		for( int i = 0; i < iir_n; i++ )
		{
			iirFilter[i] = 0.0f;
		}
	}
	
	public float GetStateX()
	{
		return m_state.x;
	}
	
	public float GetMeasurementX()
	{
		return m_measurement.x;
	}

	public float GetStateEstimateX()
	{
		return m_stateEstimate.x;
	}

	public float GetIIRFilterX()
	{
		float x = 0.0f;
		for( int i = 0; i < iir_n; i++ )
		{
			x += iirFilter[i];
		}
		return x / iir_n;
	}

	private void KalmanFilter()
	{
		// phase 1 Predict
		PVector x_km1km1 = m_stateEstimate;    // state estimate at k-1 given observations up to k-1

		PVector x_kkm1;    // state estimate at k given observations up to k-1
		x_kkm1 = Matrix2D.mult( m_F, x_km1km1 );
		
		Matrix2D FT = m_F.transpose();
		
		Matrix2D P_km1km1 = m_estimateCovariance;
		
		Matrix2D FmultPmultFT = Matrix2D.mult( m_F, P_km1km1 );
		FmultPmultFT = Matrix2D.mult( FmultPmultFT, FT );
		
		Matrix2D P_kkm1 = Matrix2D.add( FmultPmultFT, m_Q );
		
		// phase 2 Update
		float yk = m_measurement.x - x_kkm1.x;
		float Sk = P_kkm1.m00 + m_R;
		
		// optimal gain computation
		PVector Kk = new PVector();
		Kk.x = P_kkm1.m00 * ( 1.0f / Sk );
		Kk.y = P_kkm1.m10 * ( 1.0f / Sk );
		Matrix2D KmultH = new Matrix2D();
		KmultH.m00 = Kk.x;    KmultH.m01 = 0.0f;
		KmultH.m10 = Kk.y;    KmultH.m11 = 0.0f;
		m_estimateCovariance = Matrix2D.mult( Matrix2D.subtract( Matrix2D.identity(), KmultH ), P_kkm1 );

		// manual gain ( 1 = full weight on measurement, 0 = full weight on state estimate )
/*		PVector Kk = new PVector( 0.5f, 0.5f );
		// 'Joseph form'
		Matrix2D KmultH = new Matrix2D();
		KmultH.m00 = Kk.x;    KmultH.m01 = 0.0f;
		KmultH.m10 = Kk.y;    KmultH.m11 = 0.0f;
		Matrix2D part1 = Matrix2D.mult( Matrix2D.subtract( Matrix2D.identity(), KmultH ), P_kkm1 );
		Matrix2D part2 = Matrix2D.subtract( Matrix2D.identity(), KmultH ).transpose();
		Matrix2D part3 = new Matrix2D();
		part3.m00 = Kk.x*Kk.x*m_R;    part3.m01 = Kk.x*Kk.y*m_R;
		part3.m10 = Kk.y*Kk.x*m_R;    part3.m11 = Kk.y*Kk.y*m_R;
		m_estimateCovariance = Matrix2D.add( Matrix2D.mult( part1, part2 ), part3 );
*/
		m_stateEstimate = PVector.add( x_kkm1, PVector.mult( Kk, yk ) );
	}
	
	public void Update()
	{
		// taken from http://en.wikipedia.org/wiki/Kalman_filter
		
		// update state - true position of the truck
		PVector x_km1 = m_state;    // state at last step k-1

		PVector Fmultx_km1 = Matrix2D.mult( m_F, x_km1 );

		float ak = s_sigma_a * StdNormDist.randn();

		PVector wk = PVector.mult( m_G, ak );

		PVector xk = PVector.add( Fmultx_km1, wk );

		m_state = xk;
		
		// noisy measurement
		float vk = s_sigma_z * StdNormDist.randn();
		float zk = xk.x + vk;
		m_measurement.x = zk;

		KalmanFilter();
		
		iirFilter[iir_k++] = m_measurement.x;
		if( iir_k >= iir_n ) iir_k = 0;
	}
}
