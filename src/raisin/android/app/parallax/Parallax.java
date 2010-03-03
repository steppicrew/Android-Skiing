package raisin.android.app.parallax;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import raisin.android.engine.GameRuntime;
import raisin.android.engine.GameTime;
import raisin.android.R;
import raisin.android.engine.math.Cube;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
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

	private final static int speed= 30;

	// Serializable
	private int lifes;
	private double score;

    private GameTime mNextTreeTime;
    private GameTime mCrashUntilTime;
	
    private float accel;
    private float fspeed;

	private Player player= new Player(mStage);

    private List<Tree> mTrees= new ArrayList<Tree>();

	// Unserializable
    private transient GameTime mPlayerLastMoveTime;

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

    	mStage= new GameRuntime.StageData();
        sprites= new ArrayList<Sprite>();
        mTrees= new ArrayList<Tree>();

    	player= new Player(mStage);
    	
    	GameTime.reset();
        mPlayerLastMoveTime= GameTime.newInstance();
        mNextTreeTime= GameTime.newInstance();
        mCrashUntilTime= GameTime.newInstance();

        lifes= 5;
    	mStage.origin.y= 0;
    	score= 0;

        accel= 0;
        fspeed= 0;
    	rebuildSpriteList= true;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    	out.writeInt(gameState.ordinal());
    	out.writeInt(lifes);
    	out.writeDouble(mStage.origin.y);
    	out.writeDouble(score);
    	mNextTreeTime.writeToStream(out);
    	mCrashUntilTime.writeToStream(out);
    	out.writeFloat(accel);
    	out.writeFloat(fspeed);
    	out.writeObject(player);
    	out.writeInt(mTrees.size());
    	for ( Tree tree: mTrees ) out.writeObject(tree);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        restart();
    	gameState= GameRuntime.GameState.values()[in.readInt()];
    	lifes= in.readInt();
    	mStage.origin.y= in.readDouble();
    	score= in.readDouble();
        mNextTreeTime.readFromStream(in);
        mCrashUntilTime.readFromStream(in);
    	accel= in.readFloat();
    	fspeed= in.readFloat();
    	player= (Player) in.readObject();
    	player.init(mStage);
    	mTrees.clear();
    	for ( int i= in.readInt(); i > 0; i-- ) {
    		Tree tree= (Tree) in.readObject();
    		tree.init(mStage);
    		mTrees.add(tree);
    	}
    }

    private void fixContent() {

    	if ( mBackgroundImage == null ) {
            Resources res = mContext.getResources();
            mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.snow);
    	}

    	Player.fixContext(mContext);
    	Tree.fixContent(mContext);
    	
    	if ( mScoreTextPaint == null ) {
    	
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

    private void update() {

        // Log.e("gameState", "" + gameState);
        
        double elapsed = GameTime.getElapsed();

        if ( mPlayerLastMoveTime.runOut() ) {
        	mPlayerLastMoveTime.setOffset(10);
        
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

        mStage.origin.y += elapsed * fspeed;
        player.coord.y += elapsed * fspeed;

        Log.w("parallax", "stage.origin.y" + mStage.origin.y);
        Log.w("parallax", "player.coord.y" + player.coord.y);
        
        player.update(gameState);
        
        // Log.e("update-top", "elapsed=" + elapsed + " top=" + top);

        if ( mNextTreeTime.runOut() ) {
        	mNextTreeTime.setOffset(1000);

            Iterator<Tree> it = mTrees.iterator();
            while ( it.hasNext() ){
            	Tree tree= it.next();
            	if ( tree.coord.y < mStage.origin.y - tree.dimension.lowerRightFront.y ) {
            		sprites.remove(tree);
            		it.remove();
            		// rebuildSpriteList= true;
            	}
            }

        	if ( mTrees.size() < MAX_TREES ) {
	        	Tree tree= new Tree(mStage);
	        	tree.randomize();
	        	mTrees.add(tree);
        		sprites.add(tree);
	    		// rebuildSpriteList= true;
        	}
        }
    }

    private boolean crashing() {
    	
    	// FIXME: Bug: Gibt bei PAUSE true zurueck
    	return !mCrashUntilTime.runOut() || lifes == 0;
    }
    
    private boolean crashed() {
    	if ( crashing() ) return true;

		Cube playerHot= (new Cube(player.hotCube)).add(player.coord);

		for ( Tree tree: mTrees ) {
    		Cube treeHot= (new Cube(tree.hotCube)).add(tree.coord);
    		if (!playerHot.overlaps(treeHot)) continue;

    		mCrashUntilTime.setOffset(1500);
    		if ( lifes > 0 ) lifes--;
    		return true;
    	}

    	return false;
    }

    private void draw( Canvas canvas ) {

    	int width= mBackgroundImage.getWidth();
        int height= mBackgroundImage.getHeight();

        int ytop= -((int)mStage.origin.y % height);
        for ( int y = ytop; y < GameRuntime.mCanvasHeight - ytop; y += height ) {
    		for ( int x = 0; x < GameRuntime.mCanvasWidth; x += width ) {
            	canvas.drawBitmap(mBackgroundImage, x, y, null);
    		}
    	}

        player.setCrash(crashed());

        if ( rebuildSpriteList ) buildSpriteList();
	    Collections.sort(sprites);

	    for ( Sprite sprite: sprites ) {
	    	sprite.draw(canvas);
	    }

    	canvas.drawText("" + (int)(score), 40, 60, mScoreTextPaint);
    	canvas.drawText("" + lifes + " to go", GameRuntime.mCanvasWidth - 40, 60, mHitsTextPaint);

    	if ( gameState == GameState.INTRO ) {
    		canvas.drawText("PLAY!", GameRuntime.mCanvasWidth / 2, GameRuntime.mCanvasHeight / 2, mStatusTextPaint);
    	}

    	if ( gameState == GameRuntime.GameState.PAUSE ) {
    		canvas.drawText("PAUSED", GameRuntime.mCanvasWidth / 2, GameRuntime.mCanvasHeight / 2, mStatusTextPaint);
    	}
    
    }

    @Override
    public boolean refresh( Canvas canvas ) {
        fixContent();
        if ( gameState == GameRuntime.GameState.RUNNING ) update();
    	draw(canvas);

    	return gameState == GameRuntime.GameState.INTRO
    	 	|| gameState == GameRuntime.GameState.PAUSE;
    }

	@Override
	public void onAccuracyChanged( Sensor sensor, int accuracy ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged( SensorEvent event ) {
		// if ( event.values.length > 0 ) Log.w("Sensor 0", "" + event.values[0]);
		// if ( event.values.length > 1 ) Log.w("Sensor 1", "" + event.values[1]);
		// if ( event.values.length > 2 ) Log.w("Sensor 2", "" + event.values[2]);

		if ( event.values.length > 2 ) {
			playerMoveX= GameRuntime.mCanvasHeight > GameRuntime.mCanvasWidth ? -event.values[2] : -event.values[1];
			playerMoveY= GameRuntime.mCanvasHeight > GameRuntime.mCanvasWidth ? -event.values[1] : event.values[2];
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

    public void skipToNextState() {
		if ( gameState == GameRuntime.GameState.INTRO ) setState(GameRuntime.GameState.RUNNING);
		else if ( gameState == GameRuntime.GameState.RUNNING ) setState(GameRuntime.GameState.PAUSE);
		else if ( gameState == GameRuntime.GameState.PAUSE ) setState(GameRuntime.GameState.RUNNING);
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
						if ( !pauseKeyPressed ) skipToNextState();
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
}
