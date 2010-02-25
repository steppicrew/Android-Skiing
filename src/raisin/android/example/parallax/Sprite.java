/**
 * 
 */
package raisin.android.example.parallax;

import java.io.Serializable;

import raisin.android.engine.math.Point3d;
import raisin.android.engine.GameRuntime;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
abstract class Sprite implements Comparable<Sprite>, Serializable {

	// Unserializable
	protected transient GameRuntime.Stage mStageData;
	
	protected transient Point3d dimension;
	protected transient Point3d hotspot;
	
	// Serializable
	protected Point3d coord;

	Sprite( GameRuntime.Stage stageData ) {
		init(stageData);
	}

	public void init( GameRuntime.Stage stageData ) {
		this.mStageData= stageData;
	}
	
	@Override
	public int compareTo( Sprite another ) {
		Point3d diffPoint= new Point3d(coord)
			.sub(another.coord)
		;

		int zDiff= (int)diffPoint.z;
		if ( zDiff == 0 ) return (int)diffPoint.y;
		return zDiff;
	}

	public void update( GameRuntime.GameState state ) {
		// Empty
	}

	public abstract void draw( Canvas canvas );

//	private static class SizedDrawable {
//		
//		public int width;
//		public int height;
//		
//		public Drawable cached;
//
//		public void set( Drawable in, int width, int height ) {
//			// cached= in.createScaledBitmap(drawable, width, height, true);
//			this.width= width;
//			this.height= height;
//		}
//	}
	
//	// Erst mal ausserhalb von SizedDrawable
//	private static final HashMap<Integer, SizedDrawable> drawableCache= new HashMap<Integer, SizedDrawable>();

	protected void drawDrawable( Canvas canvas, Drawable drawable, Point3d ofs ) {
		if ( hotspot == null || dimension == null ) return;
		
		double scaleBy= mStageData.pointOfView.z - coord.z;
		scaleBy= Math.max(scaleBy / 10, 1);
//		Point3d pointOfView= new Point3d(mStageData.origin);
//		pointOfView.z= mStageData.pointOfView.z;
//		double scaleBy= pointOfView.sub(coord).length();
		scaleBy= 1 / scaleBy;
		//		scaleBy= .5d;
		
		Point3d upperLeftBack= new Point3d(hotspot).inverse().scaleSelf(scaleBy)
			.add(coord)
			.sub(ofs)
			.sub(mStageData.origin)
		;
		Point3d lowerRightFront= new Point3d(dimension).scaleSelf(scaleBy)
			.add(upperLeftBack)
		;

//		int width= lowerRightFront.intX() - upperLeftBack.intX();
//		int height= upperLeftBack.intY() - lowerRightFront.intY();
//		
//		SizedDrawable image= drawableCache.get(drawable.hashCode());
//		if ( image == null || image.width != width || image.height != height ) {
//		 	if ( image == null ) image= new SizedDrawable();
//		 	image.set(drawable, width, height);
//		}
		
		// TODO: calculate bounds from all three coordinates
		drawable.setBounds(
				upperLeftBack.intX(), upperLeftBack.intY(),
				lowerRightFront.intX(), lowerRightFront.intY()
		);
		drawable.draw(canvas);
	}
}