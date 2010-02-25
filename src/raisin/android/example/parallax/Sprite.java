/**
 * 
 */
package raisin.android.example.parallax;

import java.io.Serializable;

import raisin.android.engine.math.Cube;
import raisin.android.engine.math.Point3d;
import raisin.android.engine.GameRuntime;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
abstract class Sprite implements Comparable<Sprite>, Serializable {

	// Unserializable
	protected transient GameRuntime.Stage mStageData;
	protected transient Point3d imageWHD;
	protected transient Point3d imageScale;
	
	// Serializable
	protected Point3d coord;
	protected Cube dimension;

	Sprite( GameRuntime.Stage stageData ) {
		init(stageData);
	}

	Sprite( GameRuntime.Stage stageData, Point3d hotspot, Point3d widthHeightDepth ) {
		init(stageData);
		dimension= Cube.CubeByHotspotDimension(hotspot, widthHeightDepth);
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
		if ( dimension == null ) return;
		
//		Point3d pointOfView= new Point3d(mStageData.origin);
//		pointOfView.z= mStageData.pointOfView.z;
//		double factor= pointOfView.sub(coord).length();

//		double factor= mStageData.pointOfView.z / (mStageData.pointOfView.z - (coord.z + ofs.z));
		
		double projection= GameRuntime.mCanvasWidth / mStageData.slopeWidth;
		
		Cube cube= (new Cube(dimension))
			.add(coord).sub(ofs).sub(mStageData.origin)
//			.scaleBy(factor)
			.scaleBy(projection, projection, 0)
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
				cube.upperLeftBack.intX(), cube.upperLeftBack.intY(),
				cube.lowerRightFront.intX(), cube.lowerRightFront.intY()
		);
		drawable.draw(canvas);
	}
}