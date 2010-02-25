package raisin.android.engine.math;

public class SinCosTable {

	/**
	 * set table precision to 0.25 degrees
	 */
	public static final double SC_PRECISION = 0.25d;

	/**
	 * calculate reciprocal for conversions
	 */
	public static final double SC_INV_PREC = 1.0d / SC_PRECISION;

	/**
	 * compute required table length
	 */
	public static final int SC_PERIOD = (int) (360f * SC_INV_PREC);

	/**
	 * LUT for sine values
	 */
	public static final double[] sinLUT = new double[SC_PERIOD];

	/**
	 * LUT for cosine values
	 */
	public static final double[] cosLUT = new double[SC_PERIOD];

	/**
	 * Pre-multiplied degrees -> radians
	 */
	private static final double DEG_TO_RAD = Math.PI / 180.0
			* SC_PRECISION;
	
	/**
	 * Pre-multiplied radians - degrees
	 */
	private static final double RAD_TO_DEG = 180.0 / Math.PI
			/ SC_PRECISION;

	// init sin/cos tables with values
	static {
		for (int i = 0; i < SC_PERIOD; i++) {
			sinLUT[i] = Math.sin(i * DEG_TO_RAD);
			cosLUT[i] = Math.cos(i * DEG_TO_RAD);
		}
	}

	/**
	 * Calculates sine for the passed angle in radians.
	 * 
	 * @param angle
	 * @return sine value for theta
	 */
	public static final double sin(double angle) {
		while (angle < 0) {
			angle += GameMath.TWO_PI;
		}
		return sinLUT[(int) (angle * RAD_TO_DEG) % SC_PERIOD];
	}

	/**
	 * Calculate cosine for the passed in angle in radians.
	 * 
	 * @param angle
	 * @return cosine value for theta
	 */
	public static final double cos(double angle) {
		while (angle < 0) {
			angle += GameMath.TWO_PI;
		}
		return cosLUT[(int) (angle * RAD_TO_DEG) % SC_PERIOD];
	}
	
}
