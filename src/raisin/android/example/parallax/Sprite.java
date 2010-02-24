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

	public abstract void update( GameRuntime.GameState state );
	public abstract void draw( Canvas canvas );

	protected void drawDrawable( Canvas canvas, Drawable drawable, Point3d ofs ) {
		Point3d upperLeftBack= new Point3d(coord)
			.sub(hotspot)
			.sub(ofs)
			.sub(mStageData.origin)
		;
		Point3d lowerRightFront= new Point3d(upperLeftBack)
			.add(dimension)
		;

		// TODO: calculate bounds from all three coordinates
		drawable.setBounds(
				upperLeftBack.intX(), upperLeftBack.intY(),
				lowerRightFront.intX(), lowerRightFront.intY()
		);
		drawable.draw(canvas);
	}
}