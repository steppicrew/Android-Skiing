package raisin.android.engine.math;

/**
 * 
 * This class is used to store a point in a 3D space
 * 
 */
public final class Point3d {

    public double x = 0.f;
    public double y = 0.f;
    public double z = 0.f;

    public Point3d() {

    }

    public Point3d(Point3d other) {
    	this.x = other.x;
    	this.y = other.y;
    	this.z = other.z;
    }
    
    public Point3d(double inx, double iny, double inz) {
        this.x = inx;
        this.y = iny;
        this.z = inz;
    }

    public void clear(){
        x = 0.d;
        y = 0.d;
        z = 0.d;
    }
    
    public int intX() {
    	return (int)x;
    }
    
    public int intY() {
    	return (int)y;
    }
    
    public int intZ() {
    	return (int)z;
    }
    
    public void setValues(double inx, double iny, double inz) {
        this.x = inx;
        this.y = iny;
        this.z = inz;
    }

    /**
     * get a vector from adding two vectors
     */
    public void addVector(Point3d pointStart , Point3d dest) {
        dest.x = x + pointStart.x;
        dest.y = y + pointStart.y;
        dest.z = z + pointStart.z;
    }

    /**
     * get a vector by scaling
     */
    public void scaleVector(double scaler) {
        x *= scaler;
        y *= scaler;
        z *= scaler;
    }

    /**
     * get a vector by scaling
     */
    public Point3d scaleSelf(double scaler) {
        x *= scaler;
        y *= scaler;
        z *= scaler;
        return this;
    }
    
    /**
     * normalize a vector
     */
    public void normalize() {
        double length;
        length = Math.sqrt(x * x + y * y + z * z);
        x /= (float) length;
        y /= (float) length;
        z /= (float) length;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("Point = ");
        buffer.append(x);
        buffer.append(", ");
        buffer.append(y);
        buffer.append(", ");
        buffer.append(z);
        return buffer.toString();
    }
    
    public float distanceTo2D(Point3d v) {
        return (float) Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y));
    }
    
    public double distanceSquaredTo2D(Point3d v) {
        return (v.x - x) * (v.x - x) + (v.y - y) * (v.y - y);
    }

    public float distanceTo2D(float px, float py) {
        return (float) Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
    }
    
    public void inverse() {
    	this.x = -this.x;
    	this.y = -this.y;
    	this.z = -this.z;
    }
    
    public void rotateXY(float angle) {
        double cosAngle = SinCosTable.cos(angle); //(float) Math.cos(angle);
        double sinAngle = SinCosTable.sin(angle); //(float) Math.sin(angle);
        double oldX = x;
        x = cosAngle * x - sinAngle * y;
        y = sinAngle * oldX + cosAngle * y;
    }
    
    public float distanceTo(Point3d other) {
    	double deltaX = other.x - this.x;
    	double deltaY = other.y - this.y;
    	double deltaZ = other.z - this.z;
    	return (float)Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ );
    }
    
    public float length() {
    	return (float)Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z)); 
    }
     
    public final double lengthSquared() {
        return x*x + y*y + z*z;
    }
    
    public double dot(Point3d other) {		
    	return this.x*other.x + this.y*other.y + this.z*other.z;
    }

    public void cross(final Point3d a, float s)
    {
    	this.x = a.x*s;
    	this.y = a.y*s;
    	this.z = a.z*s;
    }
    
    public void cross(Point3d other) {
    	this.x = (this.y * other.z) - (this.z * other.y);
    	this.y = (this.z * other.x) - (this.x * other.z);
    	this.z = (this.x * other.y) - (this.y * other.x);
    }

    /**
     * 
     * @param one
     * @param other
     * @param into
     * @return
     */
    public final void cross(Point3d one, Point3d other) {
    	this.x = (one.y * other.z) - (one.z * other.y);
    	this.y = (one.z * other.x) - (one.x * other.z);
    	this.z = (one.x * other.y) - (one.y * other.x);
    }
    
    /* Code commented
    public static Point3f makeWithStartAndEndPoints(Point3f start, Point3f end) {
    	Point3f ret = new Point3f();
    	ret.x = end.x - start.x;
    	ret.y = end.y - start.y;
    	ret.z = end.z - start.z;
    	ret.normalize();
    	return ret;
    }
    */
    
    public void add(Point3d vector2) {
    	this.x += vector2.x;
    	this.y += vector2.y;
    	this.z += vector2.z;
    }
    
    public final void sub(final Point3d other) {
    	this.x -= other.x;
    	this.y -= other.y;
    	this.z -= other.z;
    }    

    public void negate(Point3d vector) {
    	this.x = -this.x;
    	this.y = -this.y;
    	this.z = -this.z;
    }
    
    // TODO. rename to diff
    public final void sub(final Point3d a, final Point3d b) {
    	this.x = a.x - b.x;
    	this.y = a.y - b.y;
    	this.z = a.z - b.z;
    }    

    /*
    public void rotateToDirection (float pitch, float azimuth) {
    	this.x =  (float)(-Math.sin(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(pitch)));
    	this.y = (float)(Math.sin(Math.toRadians(pitch)));
    	this.z = (float)(Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(azimuth)));	
    }    
    */
}
