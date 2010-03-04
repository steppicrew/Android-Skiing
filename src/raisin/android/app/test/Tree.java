/**
 * 
 */
package raisin.android.app.test;

import raisin.android.MainActivity;
import raisin.android.engine.math.Cube;
import raisin.android.engine.math.Point3d;
import raisin.android.engine.old.GameRuntime;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
class Tree extends Sprite {

	static final int TREE_TYPES= 4;
	public static transient Drawable[] mTreeImages;

	// Serializable
	private int type;

	Tree( StageData stageData ) {
		super(stageData);
	}

	public static void fixContent( Context context ) {
    	if ( mTreeImages != null ) return;
        	
		mTreeImages= new Drawable[TREE_TYPES];
    	String prefix= MainActivity.class.getPackage().getName() + ":drawable/tree";
        for ( int i= 0; i < TREE_TYPES; i++ ) {
	        int id= context.getResources().getIdentifier(prefix + (i + 1), null, null);
	        mTreeImages[i]= context.getResources().getDrawable(id);
        }
	}
	
	@Override
	public void init( StageData stageData ) {
		super.init(stageData);
		dimension= Cube.CubeByHotspotDimension(
			new Point3d(100, 25, 0), // hotspot
			new Point3d(200, 50, 200) // dimension
		);
		hotCube= (new Cube(dimension)).scaleBy(0.25d, 0.25d, 1);
	}
	
	void randomize( TestRuntime runtime ) {
    	type= GameRuntime.random.nextInt(TREE_TYPES);
		coord= new Point3d(
				GameRuntime.random.nextDouble() * runtime.getSlopeWidth(),
				mStageData.origin.y
					+ runtime.getSlopeHeight()
					+ dimension.dY(),
				0
		);
	}

	@Override
	public void draw( Canvas canvas ) {
		drawDrawable(canvas, mTreeImages[type], new Point3d(0, 0, 0));
	}
}