/**
 * 
 */
package raisin.android.example.parallax;

import raisin.android.engine.GameRuntime;
import raisin.android.engine.R;
import raisin.android.engine.math.Point3d;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
class Player extends Sprite {

	// Unserializable
	private transient static final int[] playerJudder= {
		0, 0, 0, 0,
		1,
		0, 0, 0,
		1, 2, 1,
		0, 0, 0, 0, 0,
		1, 2, 3, 3, 2, 1,
		0, 0, 0, 0, 0, 0,
		1, 2, 3, 3, 4, 4, 4, 3, 3, 2, 1,
		0, 0, 0, 0, 0,
		1,
		0, 0,
		1
	};
		
	private transient int playerJudderIndex;

	private transient Point3d shadowOfs;
	private transient Point3d playerOfs;

	private static transient Drawable mDriveImage;
	private static transient Drawable mShadowImage;
	private static transient Drawable mCrashImage;

	// Serializable
	private boolean crash;
	
	Player( GameRuntime.Stage stageData ) {
		super(stageData);
		coord= new Point3d(-1, 190, 40);
	}

	@Override
	public void init( GameRuntime.Stage stageData ) {
		super.init(stageData);
		shadowOfs= new Point3d(0, 0, 0);
		playerOfs= new Point3d(0, 0, 0);
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
		if ( dimension != null || mDriveImage == null ) return;

		dimension= new Point3d(
		    	mDriveImage.getIntrinsicWidth(), mDriveImage.getIntrinsicHeight(), 10
		);
		hotspot= new Point3d(dimension.x / 2, dimension.y - 8, 5);
	}

	@Override
	public void update( GameRuntime.GameState state ) {
		if ( state == GameRuntime.GameState.RUNNING ) {
			playerJudderIndex= playerJudderIndex + 1 >= playerJudder.length ? 0 : playerJudderIndex + 1;
		}
	}

	public void addX(double diffx) {
		fixWH();
		if ( coord.x < 0 ) coord.x= GameRuntime.mCanvasWidth / 2;

        coord.x += diffx;
        if ( coord.x < hotspot.x ) coord.x= hotspot.x;
        if ( coord.x > GameRuntime.mCanvasWidth - dimension.x + hotspot.x ) {
        	coord.x= GameRuntime.mCanvasWidth - dimension.x + hotspot.x;
        }
	}

	@Override
	public void draw( Canvas canvas ) {
		fixWH();

        if ( crash ) {
        	drawDrawable(canvas, mCrashImage, Point3d.Zero);
        	return;
        }

    	shadowOfs.y= -playerJudder[playerJudderIndex];
    	playerOfs.z=  20 * playerJudder[playerJudderIndex];
        
        drawDrawable(canvas, mShadowImage, shadowOfs);
    	drawDrawable(canvas, mDriveImage, playerOfs);
	}
}