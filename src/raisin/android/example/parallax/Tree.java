/**
 * 
 */
package raisin.android.example.parallax;

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
		width= 128;
		height= 128;
		hotx= 64;
		hoty= 120;
	}
	
	void randomize() {
    	type= Parallax.random.nextInt(Parallax.TREE_TYPES);
    	x= Parallax.random.nextInt(mStageData.mCanvasWidth + width) + hotx;
    	y= mStageData.top + mStageData.mCanvasHeight + hoty;
	}

	@Override
	public void update( GameState state ) {
	}

	@Override
	public void draw( Canvas canvas ) {
		drawDrawable(canvas, mStageData.mTreeImages[type], 0, 0);
	}
}