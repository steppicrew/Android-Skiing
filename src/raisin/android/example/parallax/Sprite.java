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
	protected transient GameRuntime.StageData mStageData;
	
	protected transient Point3d dimension;
	protected transient Point3d hotspot;

	// Serializable
	protected Point3d coord;

	Sprite( GameRuntime.StageData stageData ) {
		init(stageData);
	}

	public void init( GameRuntime.StageData stageData ) {
		this.mStageData= stageData;
	}
	
	@Override
	public int compareTo( Sprite another ) {
		Point3d diffPoint= new Point3d();
		diffPoint.sub(coord, another.coord);
		// 
		diffPoint.add(new Point3d(0d, 0d, dimension.z));
		int zDiff= (int)diffPoint.z;
		if ( zDiff == 0 ) return (int)diffPoint.y;
		return zDiff;
	}

	public abstract void update( GameRuntime.GameState state );
	public abstract void draw( Canvas canvas );

	protected void drawDrawable( Canvas canvas, Drawable drawable, Point3d ofs ) {
		Point3d pointHot= new Point3d(hotspot);
		pointHot.add(ofs);
		pointHot.sub(coord, new Point3d(0, -mStageData.top, 0));
		
		Point3d scaledHalfDimension= new Point3d(dimension);
		// TODO: make factor (i.e. viewers point) changable
//		scaledHalfDimension.scaleSelf(ofs.z - pointHot.z);

//		scaledHalfDimension.scaleSelf(0.5d);
		
		Point3d pointUpperLeftBack= new Point3d();
		pointUpperLeftBack.sub(pointHot, scaledHalfDimension);
		
		Point3d pointLowerRightFront= new Point3d(pointHot);
		pointLowerRightFront.add(scaledHalfDimension);
		
		// TODO: calculate bounds from all three coordinates
		drawable.setBounds(
				pointUpperLeftBack.intX(), pointUpperLeftBack.intY(),
				pointLowerRightFront.intX(), pointLowerRightFront.intY()
		);
		drawable.draw(canvas);
	}
}