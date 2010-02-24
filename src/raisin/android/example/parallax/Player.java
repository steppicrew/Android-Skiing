/**
 * 
 */
package raisin.android.example.parallax;

import raisin.android.engine.GameRuntime;
import raisin.android.engine.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
class Player extends Sprite {

	private static transient Drawable mDriveImage;
	private static transient Drawable mShadowImage;
	private static transient Drawable mCrashImage;
	
	// Serializable
	private boolean crash;
	
	Player( GameRuntime.StageData stageData ) {
		super(stageData);
    	x= -1;
    	y= 190;
	}

	@Override
	public void init( GameRuntime.StageData stageData ) {
		super.init(stageData);
	}

	public static void fixContext( Context context ) {

		if ( mDriveImage != null ) return;
		
		mDriveImage = context.getResources().getDrawable(R.drawable.player);
        mShadowImage = context.getResources().getDrawable(R.drawable.player_shadow);
        mCrashImage = context.getResources().getDrawable(R.drawable.player_crash);
	}

	public void setCrash( boolean crash ) {
		this.crash= crash;
	}

	private void fixWH() {
		if ( width != 0 ) return;

        width= mDriveImage.getIntrinsicWidth();
    	height= mDriveImage.getIntrinsicHeight();
    	hotx= width / 2;
    	hoty= height - 8;
	}
	
	public void addX(double diffx) {
		fixWH();
		
		if ( x < 0 ) x= GameRuntime.mCanvasWidth / 2;

        x += diffx;
        if ( x < hotx ) x= hotx;
        if ( x > GameRuntime.mCanvasWidth - width + hotx ) {
        	x= GameRuntime.mCanvasWidth - width + hotx;
        }
	}

	@Override
	public void update( GameRuntime.GameState state ) {
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