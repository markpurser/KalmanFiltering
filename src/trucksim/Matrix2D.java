package trucksim;

import processing.core.PVector;

public class Matrix2D
{
	public float m00 = 0.0f, m01 = 0.0f;
	public float m10 = 0.0f, m11 = 0.0f;

	public Matrix2D()
	{
	}

	public Matrix2D( Matrix2D src )
	{
		m00 = src.m00;
		m01 = src.m01;
		m10 = src.m10;
		m11 = src.m11;
	}
	
	static public Matrix2D identity()
	{
		Matrix2D ret = new Matrix2D();
		ret.m00 = 1.0f;
		ret.m01 = 0.0f;
		ret.m10 = 0.0f;
		ret.m11 = 1.0f;
		return ret;
	}
	
	static public Matrix2D mult( Matrix2D left, Matrix2D right )
	{
		Matrix2D ret = new Matrix2D();
		ret.m00 = left.m00 * right.m00 + left.m01 * right.m10;
		ret.m01 = left.m00 * right.m01 + left.m01 * right.m11;
		ret.m10 = left.m10 * right.m00 + left.m11 * right.m10;
		ret.m11 = left.m10 * right.m01 + left.m11 * right.m11;
		return ret;
	}
	
	static public PVector mult( Matrix2D left, PVector right )
	{
		PVector ret = new PVector();
		ret.x = left.m00 * right.x + left.m01 * right.y;
		ret.y = left.m10 * right.x + left.m11 * right.y;
		return ret;
	}
	
	static public Matrix2D add( Matrix2D left, Matrix2D right )
	{
		Matrix2D ret = new Matrix2D();
		ret.m00 = left.m00 + right.m00;
		ret.m01 = left.m01 + right.m01;
		ret.m10 = left.m10 + right.m10;
		ret.m11 = left.m11 + right.m11;
		return ret;
	}

	static public Matrix2D subtract( Matrix2D left, Matrix2D right )
	{
		Matrix2D ret = new Matrix2D();
		ret.m00 = left.m00 - right.m00;
		ret.m01 = left.m01 - right.m01;
		ret.m10 = left.m10 - right.m10;
		ret.m11 = left.m11 - right.m11;
		return ret;
	}

	public Matrix2D transpose()
	{
		Matrix2D ret = new Matrix2D();
		ret.m00 = m00;
		ret.m01 = m10;
		ret.m10 = m01;
		ret.m11 = m11;
		return ret;
	}
}
