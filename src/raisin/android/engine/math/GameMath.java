package raisin.android.engine.math;

public class GameMath {

    public static final double RADIANS_PER_DEGREE = Math.PI / 180.0;
    public static final double DEGREES_PER_RADIAN = 180.0 / Math.PI;

    public static final int BIG_ENDIAN = 0x02;
    public static final int LITTLE_ENDIAN = 0x04;
    
    public static final double PI = Math.PI;
    
    ///< floating point epsilon for single precision. todo: verify epsilon value and usage
    public static final double EPSILON = 0.00001d;
    
    ///< epsilon value squared
	public static final double EPSILON_SQUARED = EPSILON * EPSILON;
    
	/**
	 * PI*2
	 */
	public static final double TWO_PI = PI * 2;
    
    public static double clamp(double val, double min, double max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    public static int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    public static double len(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static double dist(double x1, double y1, double x2, double y2) {
        return len(x2 - x1, y2 - y1);
    }

    public static double getMinAngle(double cur, double target) {
        cur = normaliseAngle(cur);
        target = normaliseAngle(target);
        double diff = target - cur;
        if (diff > Math.PI) {
            diff -= Math.PI * 2;
        } else if (diff < -Math.PI) {
            diff += Math.PI * 2;
        }
        return diff;
    }

    public static double normaliseAngle(double a) {
        if (a >= 0 && a <= Math.PI * 2) {
            return a;
        }

        while (a < 0) {
            a += 2 * Math.PI;
        }
        while (a > 2 * Math.PI) {
            a -= 2 * Math.PI;
        }
        return a;
    }

    public final static double lineCircleIntersect(double x1, double y1, double x2, double y2, double r) {

        if ((x1 * x1 + y1 * y1) < r * r) {
            // Already inside
            return Float.NaN;
        }

        double dx = x2 - x1;
        double dy = y2 - y1;

        double a, b, c;
        a = dx * dx + dy * dy;
        b = 2.0f * (dx * x1 + dy * y1);
        c = (x1 * x1 + y1 * y1) - r * r;

        double d = b * b - 4 * a * c;
        if (d < 0) {
            return Float.NaN;
        }

        d = Math.sqrt(d);
        double u;
        double u1 = (-b + d) / (2.0f * a);
        double u2 = (-b - d) / (2.0f * a);

        if (u1 > 0 && u1 < 1.0f && u2 > 0 && u2 < 1.0f) {
            u = Math.min(u1, u2);
        } else if (u1 > 0 && u1 < 1.0f) {
            u = u1;
        } else if (u2 > 0 && u2 < 1.0f) {
            u = u2;
        } else {
            u = Float.NaN;
        }
        return u;
    }

    public static double lerp(double v1, double v2, double dx) {
        return v2 * dx + (1 - dx) * v1;
    }
    
    public static int byte2int(byte[] b,int endian)
    {
            int result=0;
            if(endian==LITTLE_ENDIAN)
            {
                    for(int i=0;i<b.length;i++)
                    {
                            result|=((b[i]&0xff)<<i*8);
                    }
            }
            else
            {
                    for(int i=b.length-1;i>=0;i--)
                    {
                            result|=((b[i]&0xff)<<(b.length-i-1)*8);
                    }
            }
            return result;
    }
    
    public static byte[] int2byte(int num,int endian)
    {
        byte[] targets=new byte[4];
        if(endian==LITTLE_ENDIAN)
        {
                for(int i=0;i<4;i++)
                {
                        targets[i]=(byte)((num>>i*8)&0xff);
                }
        }
        else
        {
                for(int i=3;i>=0;i--)
                {
                        targets[i]=(byte)((num>>(3-i)*8)&0xff);
                }
        }
        return targets;
    }
	
    
    
}
