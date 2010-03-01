package raisin.android.engine.math;

public class Cube {
	public Point3d upperLeftBack;
	public Point3d lowerRightFront;
	
	public Cube(Point3d upperLeftBack, Point3d lowerRightFront) {
		this.upperLeftBack= new Point3d(upperLeftBack);
		this.lowerRightFront= new Point3d(lowerRightFront);
	}
	
	public Cube(Cube other) {
		upperLeftBack= new Point3d(other.upperLeftBack);
		lowerRightFront= new Point3d(other.lowerRightFront);
	}
	
	public static Cube CubeByHotspotDimension(Point3d hotspot, Point3d widthHeightDepth) {
		Point3d upperLeftBack= new Point3d(hotspot).inverse();
		return new Cube(
			upperLeftBack,
			new Point3d(widthHeightDepth).add(upperLeftBack)
		);
	}
	
	public double width() {
		return upperLeftBack.x - lowerRightFront.x;
	}
	
	public double height() {
		return upperLeftBack.y - lowerRightFront.y;
	}
	
	public double depth() {
		return lowerRightFront.z - upperLeftBack.z;
	}
	
	public Cube add(Point3d offset) {
		upperLeftBack.add(offset);
		lowerRightFront.add(offset);
		return this;
	}
	
	public Cube sub(Point3d offset) {
		upperLeftBack.sub(offset);
		lowerRightFront.sub(offset);
		return this;
	}
	
	public Cube scaleBy(double factor) {
		upperLeftBack.scaleBy(factor);
		lowerRightFront.scaleBy(factor);
		return this;
	}

	public Cube scaleBy( double x, double y, double z ) {
		upperLeftBack.scaleBy(x, y, z);
		lowerRightFront.scaleBy(x, y, z);
		return this;
	}
	
	public boolean overlaps(Cube other) {
		return !(lowerRightFront.x < other.upperLeftBack.x || upperLeftBack.x > other.lowerRightFront.x
			|| lowerRightFront.y < other.upperLeftBack.y || upperLeftBack.y > other.lowerRightFront.y
			|| lowerRightFront.z < other.upperLeftBack.z || upperLeftBack.z > other.lowerRightFront.z
		);
	}
}
