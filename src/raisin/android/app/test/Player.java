/**
 * 
 */
package raisin.android.app.test;

import raisin.android.R;
import raisin.android.engine.GameRuntime;
import raisin.android.engine.StageData;
import raisin.android.engine.math.Cube;
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
	
	Player( StageData stageData ) {
		super(stageData);
		coord= new Point3d(-1, 50, 0);

		Point3d dim= new Point3d(50, 30, 180); // dimension
		Point3d hotSpot= new Point3d(dim).scaleBy(.5, .5, 0); // hotspot
		dimension= Cube.CubeByHotspotDimension(hotSpot, dim);
		hotCube= (new Cube(dimension)).scaleBy(0.25d, 0.25d, 1);
	}

	@Override
	public void init( StageData stageData ) {
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
		if ( imageWHD != null || mDriveImage == null ) return;

		imageWHD= new Point3d(
		    	mDriveImage.getIntrinsicWidth(), mDriveImage.getIntrinsicHeight(), 10
		);
		imageScale= new Point3d(
			imageWHD.x / Math.max(dimension.dX(), 1),
			imageWHD.y / Math.max(dimension.dY(), 1),
			imageWHD.z / Math.max(dimension.dZ(), 1)
		);
	}

	@Override
	public void update( GameRuntime.GameState state ) {
		if ( state == GameRuntime.GameState.RUNNING ) {
			playerJudderIndex= ++playerJudderIndex % playerJudder.length;
		}
	}

	public void addX( TestRuntime runtime, double diffx) {
		fixWH();
		double slopeWidth= runtime.getSlopeWidth();
		if ( coord.x < 0 ) coord.x= slopeWidth / 2;

        coord.x += diffx;
        if ( coord.x + dimension.upperLeftBack.x < 0 ) coord.x= -dimension.upperLeftBack.x;
        if ( coord.x + dimension.lowerRightFront.x > slopeWidth ) {
        	coord.x= slopeWidth - dimension.lowerRightFront.x;
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
    	playerOfs.z=  2 * playerJudder[playerJudderIndex];
        
        drawDrawable(canvas, mShadowImage, shadowOfs);
    	drawDrawable(canvas, mDriveImage, playerOfs);
	}
}