package raisin.android.engine.math;

import java.io.Serializable;

/**
 * 
 * This class is used to store a point in a 3D space
 * 
 */
@SuppressWarnings("serial")
public final class Point3d implements Serializable {

    public double x;
    public double y;
    public double z;

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

    public Point3d clear(){
        x = 0.d;
        y = 0.d;
        z = 0.d;
        return this;
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
    
    public Point3d setValues(double inx, double iny, double inz) {
        this.x = inx;
        this.y = iny;
        this.z = inz;
        return this;
    }

    /**
     * get a vector from adding two vectors
     */
 /*
  * unexpected change of parameter
    public void addVector(Point3d pointStart , Point3d dest) {
        dest.x = x + pointStart.x;
        dest.y = y + pointStart.y;
        dest.z = z + pointStart.z;
    }
*/

    /**
     * get a vector by scaling
     */
    public Point3d scaleVector(double scaler) {
        x *= scaler;
        y *= scaler;
        z *= scaler;
        return this;
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
    public Point3d normalize() {
        double length;
        length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;
        return this;
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
    
    public double distanceTo2D(Point3d v) {
        return Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y));
    }
    
    public double distanceSquaredTo2D(Point3d v) {
        return (v.x - x) * (v.x - x) + (v.y - y) * (v.y - y);
    }

    public double distanceTo2D(double px, double py) {
        return Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
    }
    
    public Point3d inverse() {
    	this.x = -this.x;
    	this.y = -this.y;
    	this.z = -this.z;
    	return this;
    }
    
    public Point3d min(double x, double y, double z) {
    	this.x= Math.min(this.x, x);
    	this.y= Math.min(this.y, y);
    	this.z= Math.min(this.z, z);
    	return this;
    }
    
    public Point3d min(Point3d other) {
    	return this.min(other.x, other.y, other.z);
    }
    
    public Point3d max(double x, double y, double z) {
    	this.x= Math.max(this.x, x);
    	this.y= Math.max(this.y, y);
    	this.z= Math.max(this.z, z);
    	return this;
    }
    
    public Point3d max(Point3d other) {
    	return this.max(other.x, other.y, other.z);
    }
    
    public Point3d rotateXY(double angle) {
        double cosAngle = SinCosTable.cos(angle); //(float) Math.cos(angle);
        double sinAngle = SinCosTable.sin(angle); //(float) Math.sin(angle);
        double oldX = x;
        x = cosAngle * x - sinAngle * y;
        y = sinAngle * oldX + cosAngle * y;
        return this;
    }
    
    public double distanceTo(Point3d other) {
    	double deltaX = other.x - this.x;
    	double deltaY = other.y - this.y;
    	double deltaZ = other.z - this.z;
    	return Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ );
    }
    
    public double length() {
    	return Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z)); 
    }
     
    public final double lengthSquared() {
        return x*x + y*y + z*z;
    }
    
    public double dot(Point3d other) {		
    	return this.x*other.x + this.y*other.y + this.z*other.z;
    }

    public Point3d cross(final Point3d a, double s)
    {
    	this.x = a.x*s;
    	this.y = a.y*s;
    	this.z = a.z*s;
    	return this;
    }
    
    public Point3d cross(Point3d other) {
    	this.x = (this.y * other.z) - (this.z * other.y);
    	this.y = (this.z * other.x) - (this.x * other.z);
    	this.z = (this.x * other.y) - (this.y * other.x);
    	return this;
    }

    /**
     * 
     * @param one
     * @param other
     * @param into
     * @return
     */
    public final Point3d cross(Point3d one, Point3d other) {
    	this.x = (one.y * other.z) - (one.z * other.y);
    	this.y = (one.z * other.x) - (one.x * other.z);
    	this.z = (one.x * other.y) - (one.y * other.x);
    	return this;
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
    
    public Point3d add(Point3d other) {
    	this.x += other.x;
    	this.y += other.y;
    	this.z += other.z;
    	return this;
    }
    
    public Point3d addXYZ( double x, double y, double z ) {
    	this.x += x;
    	this.y += y;
    	this.z += z;
    	return this;
    }
    
    public final Point3d sub(final Point3d other) {
    	this.x -= other.x;
    	this.y -= other.y;
    	this.z -= other.z;
    	return this;
    }    

    public Point3d negate(Point3d vector) {
    	this.x = -this.x;
    	this.y = -this.y;
    	this.z = -this.z;
    	return this;
    }
    
    public static final Point3d diff(final Point3d a, final Point3d b) {
    	return new Point3d(
    		a.x - b.x,
    		a.y - b.y,
    		a.z - b.z
    	);
    }    

    /*
    public void rotateToDirection (double pitch, double azimuth) {
    	this.x = -Math.sin(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(pitch));
    	this.y = Math.sin(Math.toRadians(pitch));
    	this.z = Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(azimuth));	
    }    
    */
}
