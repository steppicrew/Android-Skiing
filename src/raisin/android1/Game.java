package raisin.android1;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

@SuppressWarnings("serial")
public final class Game extends GameBase implements SensorEventListener, Serializable {

	private static Random random= new Random();

	private static final int MAX_TREES= 8;
	private static final int TREE_TYPES= 4;

    public static final int STATE_INRO = 0;
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;
	
	private static class StageData {
		public double top;
	    public transient int mCanvasHeight = 1;
	    public transient int mCanvasWidth = 1;
		public transient Drawable[] mTreeImages;
	}

	private static abstract class Sprite implements Comparable<Sprite>, Serializable {

		// Unserializable
		protected transient StageData mStageData;
		
		protected transient int width, height;
		protected transient int hotx, hoty;

		// Serializable
		protected double x, y;

		Sprite( StageData stageData ) {
			init(stageData);
		}

		public void init( StageData stageData ) {
			this.mStageData= stageData;
		}
		
		@Override
		public int compareTo( Sprite another ) {
			return (int)(y - another.y);
		}

		public abstract void draw( Canvas canvas );

		void drawDrawable( Canvas canvas, Drawable drawable, int ofsx, int ofsy ) {
			int ix= (int)x - hotx + ofsx;
			int iy= (int)(y - mStageData.top) - hoty + ofsy;
			drawable.setBounds(ix, iy, ix + width, iy + height);
			drawable.draw(canvas);
		}

		private void writeObject(java.io.ObjectOutputStream out) throws IOException {
	    	out.writeDouble(x);
	    	out.writeDouble(y);
	    }

	    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
	    	x= in.readDouble();
	    	y= in.readDouble();
	    }
	}
	
	private static class Tree extends Sprite {

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
        	type= random.nextInt(TREE_TYPES);
        	x= random.nextInt(mStageData.mCanvasWidth + width) + hotx;
        	y= mStageData.top + mStageData.mCanvasHeight + hoty;
		}

		@Override
		public void draw( Canvas canvas ) {
			drawDrawable(canvas, mStageData.mTreeImages[type], 0, 0);
		}
	}

	private static class Player extends Sprite {

		// Unserializable
		private transient static final int[] playerOfs= {
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
			
		private transient int playerOfsIndex;

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
		public void draw( Canvas canvas ) {
	        if ( crash ) {
	        	drawDrawable(canvas, mCrashImage, 0, 0);
	        	return;
	        }

			playerOfsIndex= playerOfsIndex + 1 >= playerOfs.length ? 0 : playerOfsIndex + 1;
	        drawDrawable(canvas, mShadowImage, 0, -playerOfs[playerOfsIndex]);
        	drawDrawable(canvas, mDriveImage, 0, -2 * playerOfs[playerOfsIndex]);
		}
	}

	private final static int speed= 300;

	// Serializable
	private int gameState;
	
	private int lifes;
	private double score;
	
    private long mNextTreeTime;
    private long playerLastMoveTime;
    private long crashUntilTime;

    private float accel;
    private float fspeed;

    StageData mStageData= new StageData();
	private Player player= new Player(mStageData);

    private List<Tree> mTrees= new ArrayList<Tree>();

	// Unserializable
    private transient long mLastTime;
    
	private transient float playerMoveX;
	private transient float playerMoveY;

	private transient boolean rebuildSpriteList;

	private transient List<Sprite> sprites= new ArrayList<Sprite>();

    private transient SensorManager mSensorManager;
	private transient Sensor mSensor;
	
    private transient Bitmap mBackgroundImage;

	private transient Paint mScoreTextPaint;
	private transient Paint mHitsTextPaint;

    Game() {
    	restart();
    }

    @Override
    public void init( Context context ) {
    	super.init(context);
    	mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    	List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
    	if ( sensors.size() > 0 ) {
    		mSensor= sensors.get(0);
    		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    	}
    	else {
    		mSensorManager= null;
    	}
    }
    
    @Override
    public void restart() {
    	super.restart();
    	player= new Player(mStageData);
    	lifes= 5;
    	mStageData.top= 0;
    	score= 0;
    	mLastTime= 0;
        mNextTreeTime= 0;
        playerLastMoveTime= 0;
        crashUntilTime= 0;
        accel= 0;
        fspeed= 0;
    	rebuildSpriteList= true;
    	gameState= STATE_RUNNING;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    	out.writeInt(gameState);
    	out.writeInt(lifes);
    	out.writeDouble(mStageData.top);
    	out.writeDouble(score);
    	out.writeLong(mNextTreeTime - mLastTime);
    	out.writeLong(crashUntilTime - mLastTime);
    	out.writeFloat(accel);
    	out.writeFloat(fspeed);
    	out.writeObject(player);
    	out.writeInt(mTrees.size());
    	for ( Tree tree: mTrees ) out.writeObject(tree);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

    	mStageData= new StageData();
        sprites= new ArrayList<Sprite>();
        mTrees= new ArrayList<Tree>();

        restart();
    	gameState= in.readInt();
    	lifes= in.readInt();
    	mStageData.top= in.readDouble();
    	score= in.readDouble();
    	mLastTime= 0;	// Auf 0 setzen, damit die Zeitwerte in update() korrigiert werden.
    	mNextTreeTime= in.readLong();
    	crashUntilTime= in.readLong();
    	accel= in.readFloat();
    	fspeed= in.readFloat();
    	player= (Player) in.readObject();
    	player.init(mStageData);
    	mTrees.clear();
    	for ( int i= in.readInt(); i > 0; i-- ) {
    		Tree tree= (Tree) in.readObject();
    		tree.init(mStageData);
    		mTrees.add(tree);
    	}
    }

    @Override
    public void setSurfaceSize( int width, int height ) {
    	super.setSurfaceSize(width, height);
    	mStageData.mCanvasWidth = width;
    	mStageData.mCanvasHeight = height;
    }
    
    private void fixContent() {

    	if ( mBackgroundImage == null ) {
            Resources res = mContext.getResources();
            mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.snow);
    	}

    	player.fixContext(mContext);

    	if ( mStageData.mTreeImages == null ) {
    	
    		mStageData.mTreeImages= new Drawable[TREE_TYPES];
	    	String prefix= Game.class.getPackage().getName() + ":drawable/tree";
	        for ( int i= 0; i < TREE_TYPES; i++ ) {
		        int id= mContext.getResources().getIdentifier(prefix + (i + 1), null, null);
		        mStageData.mTreeImages[i]= mContext.getResources().getDrawable(id);
	        }
	
	        mScoreTextPaint= new Paint();
	        mScoreTextPaint.setTextSize(50f);
	
	        mHitsTextPaint= new Paint();
	        mHitsTextPaint.setTextAlign(Align.RIGHT);
	        mHitsTextPaint.setTextSize(50f);
    	}
    }
    
    private void buildSpriteList() {

    	Log.e("Game", "rebuildspritelist");

    	sprites.clear();
    	sprites.addAll(mTrees);
        sprites.add(player);
    	rebuildSpriteList= false;
    }
    
    private void update() {
        long now = System.currentTimeMillis();
        
        if ( mLastTime > now ) return;

        if ( mLastTime == 0 ) {
        	mLastTime= now;

        	// Wenn mLastTime 0 ist, enthalten folgende Zeitvariablen
        	// relative Werte und m�ssen angepasst werden
            playerLastMoveTime += mLastTime;
            crashUntilTime += mLastTime;
        }
        
        double elapsed = (now - mLastTime) / 1000.0;
        mLastTime = now;

        // FIXME zu grosses elapsed abfangen
        // if ( elapsed > 10 ) return;

        if ( playerLastMoveTime < now ) {

        	playerLastMoveTime += 10;
        	if ( playerLastMoveTime < now ) playerLastMoveTime= now;
        
        	if ( lifes == 0 ) {
        		accel -= 0.03f;
        		if ( accel < 0 ) accel= 0;
        	}
        	else {
        		player.addX(playerMoveX);
	
        		float my= (playerMoveY - 25) / 400;

        		if ( crashing() ) my= -0.1f;
        		else if ( my < -1 ) my= -1;
        		else if ( my > 1 ) my= 1;
	        
        		accel += my;
	        
        		if ( accel < 1 ) accel= 1;
        		else if ( accel > 5 ) accel= 5;

        		score += (accel - 0.9f) / 10;
        	}
	        fspeed = speed * accel;
        }

        mStageData.top += elapsed * fspeed;
        player.y += elapsed * fspeed;
	        
        // Log.e("update-top", "elapsed=" + elapsed + " top=" + top);

        if ( mNextTreeTime < now ) {

        	mNextTreeTime += 1000;
        	if ( mNextTreeTime < now ) mNextTreeTime= now;

            Iterator<Tree> it = mTrees.iterator();
            while ( it.hasNext() ){
            	Tree tree= it.next();
            	if ( tree.y < mStageData.top - tree.height ) {
            		sprites.remove(tree);
            		it.remove();
            		// rebuildSpriteList= true;
            	}
            }

        	if ( mTrees.size() < MAX_TREES ) {
	        	Tree tree= new Tree(mStageData);
	        	tree.randomize();
	        	mTrees.add(tree);
        		sprites.add(tree);
	    		// rebuildSpriteList= true;
        	}
        }
    }

    private boolean crashing() {
    	return mLastTime < crashUntilTime || lifes == 0;
    }
    
    private boolean crashed() {
    	if ( crashing() ) return true;

    	for ( Tree tree: mTrees ) {
    		int treeLeft= (int)tree.x - tree.width / 4;
    		int treeRight= (int)tree.x + tree.width / 4;

    		int playerLeft= (int)player.x - player.width / 3;
    		int playerRight= (int)player.x + player.width / 3;

    		if ( playerRight < treeLeft || playerLeft > treeRight ) continue;

    		int py= (int)player.y;
    		int treeTop= (int)tree.y - 10;
    		int treeBottom= (int)tree.y + 7;

    		if ( py < treeTop || py > treeBottom ) continue;

    		crashUntilTime= mLastTime + 1500;
    		if ( lifes > 0 ) lifes--;
    		return true;
    	}

    	return false;
    }

    private void draw( Canvas canvas ) {

    	int width= mBackgroundImage.getWidth();
        int height= mBackgroundImage.getHeight();

        int ytop= -((int)mStageData.top % height);
        for ( int y = ytop; y < mStageData.mCanvasHeight - ytop; y += height ) {
    		for ( int x = 0; x < mStageData.mCanvasWidth; x += width ) {
            	canvas.drawBitmap(mBackgroundImage, x, y, null);
    		}
    	}

        player.setCrash(crashed());

        if ( rebuildSpriteList ) buildSpriteList();
	    Collections.sort(sprites);

	    for ( Sprite sprite: sprites )
	    	sprite.draw(canvas);

    	canvas.drawText("" + (int)(score), 40, 60, mScoreTextPaint);
    	canvas.drawText("" + lifes + " to go", mStageData.mCanvasWidth - 40, 60, mHitsTextPaint);
    }

    @Override
    public void refresh( Canvas canvas ) {
        fixContent();
    	update();
    	draw(canvas);
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged( SensorEvent event ) {
		// if ( event.values.length > 0 ) Log.w("Sensor 0", "" + event.values[0]);
		// if ( event.values.length > 1 ) Log.w("Sensor 1", "" + event.values[1]);
		// if ( event.values.length > 2 ) Log.w("Sensor 2", "" + event.values[2]);

		if ( event.values.length > 2 ) {
			playerMoveX= mStageData.mCanvasHeight > mStageData.mCanvasWidth ? -event.values[2] : -event.values[1];
			playerMoveY= mStageData.mCanvasHeight > mStageData.mCanvasWidth ? -event.values[1] : event.values[2];
		}
	}
    
    @Override
    public void destroy() {
    	super.destroy();
    	if ( mSensorManager != null ) {
    		mSensorManager.unregisterListener(this, mSensor);
    	}
    		// if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
    }

	public boolean handleKeyEvent( View v, int keyCode, KeyEvent event ) {
		switch ( event.getAction() ) {
			case KeyEvent.ACTION_DOWN:
				int factor= event.getRepeatCount() + 1;
				switch ( event.getKeyCode() ) {
					case KeyEvent.KEYCODE_DPAD_DOWN: 
						playerMoveY=  1000 * factor;
						return true;
					case KeyEvent.KEYCODE_DPAD_UP:    
						playerMoveY= -1000 * factor; 
						return true;
					case KeyEvent.KEYCODE_DPAD_LEFT:  
						playerMoveX= -2 * factor;
						return true;
					case KeyEvent.KEYCODE_DPAD_RIGHT: 
						playerMoveX=  2 * factor;
						return true;
				}
			case KeyEvent.ACTION_UP:
				switch (event.getKeyCode()) {
					case KeyEvent.KEYCODE_DPAD_DOWN:
					case KeyEvent.KEYCODE_DPAD_UP:   
						playerMoveY= 0;
						return true;
					case KeyEvent.KEYCODE_DPAD_LEFT:
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						playerMoveX= 0;
						return true;
				}
		}
		return false;
	}

	@Override
    public void pause() {
		super.pause();
        // mLastTime = System.currentTimeMillis() + 100;
        gameState= STATE_PAUSE;
    	// setState(STATE_RUNNING);
    }

	@Override
    public void unpause() {
		super.unpause();
        // mLastTime = System.currentTimeMillis() + 100;
        gameState= STATE_RUNNING;
    	// setState(STATE_RUNNING);
    }
}
