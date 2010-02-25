package raisin.android.engine.math;

public class Cube {
	public Point3d upperLeftBack;
	public Point3d lowerRightFront;
	
	Cube(Point3d upperLeftBack, Point3d lowerRightFront) {
		this.upperLeftBack= new Point3d(upperLeftBack);
		this.lowerRightFront= new Point3d(lowerRightFront);
	}
	
	Cube(Cube other) {
		upperLeftBack= new Point3d(other.upperLeftBack);
		lowerRightFront= new Point3d(other.lowerRightFront);
	}
	
	public static Cube CubeByHotspot(Point3d hotspot, Point3d dimension) {
		Point3d upperLeftBack= new Point3d(hotspot).inverse();
		return new Cube(
			upperLeftBack,
			new Point3d(dimension).add(upperLeftBack)
		);
	}
	
	public Cube add(Point3d offset) {
		upperLeftBack.add(offset);
		lowerRightFront.add(offset);
		return this;
	}
	
	public Cube scaleBy(double factor) {
		upperLeftBack.scaleSelf(factor);
		lowerRightFront.scaleSelf(factor);
		return this;
	}
}
