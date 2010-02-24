/**
 * 
 */
package raisin.android.example.parallax;

import raisin.android.engine.GameRuntime;
import raisin.android.example.parallax.Parallax.StageData;
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
    	String prefix= GameRuntime.class.getPackage().getName() + ":drawable/tree";
        for ( int i= 0; i < TREE_TYPES; i++ ) {
	        int id= context.getResources().getIdentifier(prefix + (i + 1), null, null);
	        mTreeImages[i]= context.getResources().getDrawable(id);
        }
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
    	type= Parallax.random.nextInt(TREE_TYPES);
    	x= Parallax.random.nextInt(mStageData.mCanvasWidth + width) + hotx;
    	y= mStageData.top + mStageData.mCanvasHeight + hoty;
	}

	@Override
	public void update( GameRuntime.GameState state ) {
	}

	@Override
	public void draw( Canvas canvas ) {
		drawDrawable(canvas, mTreeImages[type], 0, 0);
	}
}