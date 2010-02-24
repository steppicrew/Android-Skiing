/**
 * 
 */
package raisin.android.example.parallax;

import raisin.android.engine.R;
import raisin.android.engine.math.Point3d;
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
		coord= new Point3d(-1, 190, 100);
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

        dimension= new Point3d(
        	mDriveImage.getIntrinsicWidth(), mDriveImage.getIntrinsicHeight(), 10
        );
        hotspot= new Point3d(dimension.x / 2, dimension.y - 8, 5);
	}

	public void setCrash( boolean crash ) {
		this.crash= crash;
	}

	public void addX(double diffx) {
		if ( coord.x < 0 ) coord.x= mStageData.mCanvasWidth / 2;

        coord.x += diffx;
        if ( coord.x < hotspot.x ) coord.x= hotspot.x;
        if ( coord.x > mStageData.mCanvasWidth - dimension.x + hotspot.x ) {
        	coord.x= mStageData.mCanvasWidth - dimension.x + hotspot.x;
        }
	}

	@Override
	public void update( GameState state ) {
	}

	@Override
	public void draw( Canvas canvas ) {
		Point3d origin= new Point3d(0, 0, 0);
        if ( crash ) {
        	drawDrawable(canvas, mCrashImage, origin);
        	return;
        }
        drawDrawable(canvas, mShadowImage, origin);
    	drawDrawable(canvas, mDriveImage, origin);
	}
}