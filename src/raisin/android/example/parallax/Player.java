/**
 * 
 */
package raisin.android.example.parallax;

import raisin.android.engine.R;
import raisin.android.example.parallax.Parallax.GameState;
import raisin.android.example.parallax.Parallax.StageData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
class Player extends Sprite {

	private transient Drawable mDriveImage;
	private transient Drawable mShadowImage;
	private transient Drawable mCrashImage;
	
	// Serializable
	private boolean crash;
	
	Player( StageData stageData ) {
		super(stageData);
    	x= -1;
    	y= 190;
	}

	@Override
	public void init( StageData stageData ) {
		super.init(stageData);
	}

	void fixContext( Context context ) {

		if ( mDriveImage != null ) return;
		
		mDriveImage = context.getResources().getDrawable(R.drawable.player);
        mShadowImage = context.getResources().getDrawable(R.drawable.player_shadow);
        mCrashImage = context.getResources().getDrawable(R.drawable.player_crash);

        width= mDriveImage.getIntrinsicWidth();
    	height= mDriveImage.getIntrinsicHeight();
    	hotx= width / 2;
    	hoty= height - 8;
	}

	public void setCrash( boolean crash ) {
		this.crash= crash;
	}

	public void addX(double diffx) {
		if ( x < 0 ) x= mStageData.mCanvasWidth / 2;

        x += diffx;
        if ( x < hotx ) x= hotx;
        if ( x > mStageData.mCanvasWidth - width + hotx ) {
        	x= mStageData.mCanvasWidth - width + hotx;
        }
	}

	@Override
	public void update( GameState state ) {
	}

	@Override
	public void draw( Canvas canvas ) {
        if ( crash ) {
        	drawDrawable(canvas, mCrashImage, 0, 0);
        	return;
        }
        drawDrawable(canvas, mShadowImage, 0, 0);
    	drawDrawable(canvas, mDriveImage, 0, 0);
	}
}