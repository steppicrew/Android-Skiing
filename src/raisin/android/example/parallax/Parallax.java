package raisin.android.example.parallax;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import raisin.android.engine.GameRuntime;
import raisin.android.engine.GameTime;
import raisin.android.engine.R;

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
public final class Parallax extends GameRuntime implements SensorEventListener, Serializable {

	private static final int MAX_TREES= 8;
	static final int TREE_TYPES= 4;

	public static enum GameState {
		INTRO, LOSE, PAUSE, READY, RUNNING, WIN,
	};
	
	public static class StageData {
		public double top;
	    public transient int mCanvasHeight = 1;
	    public transient int mCanvasWidth = 1;
		public transient Drawable[] mTreeImages;
	}

	private final static int speed= 300;

	// Serializable
	private GameState gameState;
	
	private int lifes;
	private double score;

    private transient GameTime GTmPlayerLastMoveTime;
    private GameTime GTmNextTreeTime;
    private GameTime GTmCrashUntilTime;
	
    // private long mNextTreeTime;
    // private long playerLastMoveTime;
    // private long crashUntilTime;

    private float accel;
    private float fspeed;

    StageData mStageData= new StageData();
	private Player player= new Player(mStageData);

    private List<Tree> mTrees= new ArrayList<Tree>();

	// Unserializable
    // private transient long mLastTime;
    
	private transient float playerMoveX;
	private transient float playerMoveY;

	private transient boolean rebuildSpriteList;

	private transient List<Sprite> sprites= new ArrayList<Sprite>();

    private transient SensorManager mSensorManager;
	private transient Sensor mSensor;
	
    private transient Bitmap mBackgroundImage;

	private transient Paint mScoreTextPaint;
	private transient Paint mHitsTextPaint;
	private transient Paint mStatusTextPaint;

    public Parallax() {
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

    	mStageData= new StageData();
        sprites= new ArrayList<Sprite>();
        mTrees= new ArrayList<Tree>();

    	player= new Player(mStageData);
    	
    	GameTime.reset();
        GTmPlayerLastMoveTime= GameTime.newInstance();
        GTmNextTreeTime= GameTime.newInstance();
        GTmCrashUntilTime= GameTime.newInstance();

        lifes= 5;
    	mStageData.top= 0;
    	score= 0;

        accel= 0;
        fspeed= 0;
    	rebuildSpriteList= true;
    	gameState= GameState.RUNNING;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    	out.writeInt(gameState.ordinal());
    	out.writeInt(lifes);
    	out.writeDouble(mStageData.top);
    	out.writeDouble(score);
    	out.writeLong(GTmNextTreeTime.getOffset());
    	out.writeLong(GTmCrashUntilTime.getOffset());
    	out.writeFloat(accel);
    	out.writeFloat(fspeed);
    	out.writeObject(player);
    	out.writeInt(mTrees.size());
    	for ( Tree tree: mTrees ) out.writeObject(tree);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

        restart();
    	gameState= GameState.values()[in.readInt()];
    	lifes= in.readInt();
    	mStageData.top= in.readDouble();
    	score= in.readDouble();
        GTmNextTreeTime.readFromStream(in);
        GTmCrashUntilTime.readFromStream(in);
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
	    	String prefix= GameRuntime.class.getPackage().getName() + ":drawable/tree";
	        for ( int i= 0; i < TREE_TYPES; i++ ) {
		        int id= mContext.getResources().getIdentifier(prefix + (i + 1), null, null);
		        mStageData.mTreeImages[i]= mContext.getResources().getDrawable(id);
	        }
	
	        mScoreTextPaint= new Paint();
	        mScoreTextPaint.setTextSize(50f);
	
	        mHitsTextPaint= new Paint();
	        mHitsTextPaint.setTextAlign(Align.RIGHT);
	        mHitsTextPaint.setTextSize(50f);

	        mStatusTextPaint= new Paint();
	        mStatusTextPaint.setTextAlign(Align.CENTER);
	        mStatusTextPaint.setTextSize(50f);
    	}
    }
    
    private void buildSpriteList() {

    	Log.w("Game", "rebuildspritelist");

    	sprites.clear();
    	sprites.addAll(mTrees);
        sprites.add(player);
    	rebuildSpriteList= false;
    }

    // Check Integrity
    private void setState( GameState state ) {
    	switch ( state ) {
	    	case PAUSE:
	    		if ( gameState == GameState.RUNNING ) {
	    	        GameTime.stop();
	    			gameState= state;
	    			break;
	    		}
	    		break;

	    	case RUNNING:
	    		if ( gameState == GameState.PAUSE ) {
	    			gameState= state;
	    			break;
	    		}
	    		break;
    	}
    	Log.w("setState", "state: " + state + " gameState:" + gameState);
    }
    
    private void update() {

        // Log.e("gameState", "" + gameState);
        
        double elapsed = GameTime.getElapsed();

        if ( GTmPlayerLastMoveTime.runOut() ) {
        	GTmPlayerLastMoveTime.setOffset(10);
        
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

        if ( GTmNextTreeTime.runOut() ) {
        	GTmNextTreeTime.setOffset(1000);

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
    	return !GTmCrashUntilTime.runOut() || lifes == 0;
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

    		GTmCrashUntilTime.setOffset(1500);
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

    	if ( gameState == GameState.PAUSE ) {
    		canvas.drawText("PAUSED", mStageData.mCanvasWidth / 2, mStageData.mCanvasHeight / 2, mStatusTextPaint);
    	}
    
    }

    @Override
    public void refresh( Canvas canvas ) {
        fixContent();
        if ( gameState == GameState.RUNNING ) update();
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

    private boolean pauseKeyPressed;
    
    @Override
	public boolean handleKeyEvent( View v, int keyCode, KeyEvent event ) {

    	Log.e("Key", "" + keyCode + " event: " + event);
    	
    	if ( super.handleKeyEvent(v, keyCode, event) ) return true;
		
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
						
					case KeyEvent.KEYCODE_ENTER:
						if (lifes == 0) restart();
						return true;
						
					case KeyEvent.KEYCODE_P:
					case KeyEvent.KEYCODE_SPACE:
						if ( !pauseKeyPressed ) {
							if ( gameState == GameState.RUNNING ) setState(GameState.PAUSE);
							else if ( gameState == GameState.PAUSE ) setState(GameState.RUNNING);
						}
						pauseKeyPressed= true;
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

					case KeyEvent.KEYCODE_P:
					case KeyEvent.KEYCODE_SPACE:
						pauseKeyPressed= false;
						return true;
				}
		}
		return false;
	}

	@Override
    public void pause() {
		super.pause();
        setState(GameState.PAUSE);
    }

	@Override
    public void unpause() {
		super.unpause();
        setState(GameState.RUNNING);
    }
}
