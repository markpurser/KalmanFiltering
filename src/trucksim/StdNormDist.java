package trucksim;

import processing.core.PApplet;

public class StdNormDist
{
	static float[] m_randn;
	static int m_i = 0;

	static public void Initialise( PApplet _p )
	{
		String lines[] = _p.loadStrings( "norm_dist.txt" );
//		PApplet.println("there are " + lines.length + " lines");
		m_randn = new float[lines.length];
		for (int i =0 ; i < lines.length; i++)
		{
			m_randn[i] = Float.valueOf( lines[i] );
//			PApplet.println( lines[i] + "   " + m_randn[i] );
		}
	}
	
	static public float randn()
	{
		if( m_i >= m_randn.length ) m_i = 0;
		return m_randn[m_i++];
	}

}
