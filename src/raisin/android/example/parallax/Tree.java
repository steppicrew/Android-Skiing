/**
 * 
 */
package raisin.android.example.parallax;

import raisin.android.engine.math.Point3d;
import raisin.android.example.parallax.Parallax.GameState;
import raisin.android.example.parallax.Parallax.StageData;
import android.graphics.Canvas;

@SuppressWarnings("serial")
class Tree extends Sprite {

	// Serializable
	private int type;

	Tree( StageData stageData ) {
		super(stageData);
	}
	
	@Override
	public void init( StageData stageData ) {
		super.init(stageData);
		dimension= new Point3d(128, 128, 10);
		hotspot= new Point3d(64, 120, 5);
	}
	
	void randomize() {
    	type= Parallax.random.nextInt(Parallax.TREE_TYPES);
    	coord= new Point3d(
    			Parallax.random.nextDouble() * (mStageData.mCanvasWidth + dimension.x + hotspot.x),
    			mStageData.top + mStageData.mCanvasHeight + hotspot.y,
    			50
    	);
	}

	@Override
	public void update( GameState state ) {
	}

	@Override
	public void draw( Canvas canvas ) {
		drawDrawable(canvas, mStageData.mTreeImages[type], new Point3d(0, 0, 0));
	}
}