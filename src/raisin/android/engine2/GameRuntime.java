package raisin.android.engine2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Random;


import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

@SuppressWarnings("serial")
public abstract class GameRuntime implements Serializable {

	// Static data
	
	public static enum GameState {
		INTRO, LOSE, PAUSE, READY, RUNNING, WIN,
	}

	public static Random random= new Random();

	private static ByteArrayOutputStream baos= new ByteArrayOutputStream();

    // Unserializable

	// protected transient Context mContext;

	// Serializable

	protected StageData mStage= new StageData();
	
	protected GameRuntime.GameState gameState;

    public abstract boolean refresh( Canvas canvas );
    public abstract void skipToNextState();

    
	public void freeze() {
		try {
			pause();
    		baos.reset();
    		ObjectOutputStream oos= new ObjectOutputStream(baos);
    		oos.writeObject(this);
    		oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	// Erzeugt ein neues GameRuntime!
	
	// TODO: Fix Exceptions!
	public static GameRuntime thaw( Context context ) throws Exception {
		if ( baos.size() == 0 ) throw new Exception("No Stream, no data");
		try {
    		ByteArrayInputStream bais= new ByteArrayInputStream(baos.toByteArray());
    		ObjectInputStream ois= new ObjectInputStream(bais);
    		GameRuntime instance= (GameRuntime) ois.readObject();
    		instance.initAfterThaw();
    		instance.init(context);
    		ois.close();
            return instance;

		} catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}
        throw new Exception("Uh oh!");
	}
	
	
    public void init( Context context ) {
    	// mContext= context;

    }

    public void restart() {
    	gameState= GameRuntime.GameState.INTRO;
    	GameTime.reset();
    	restartOrThaw();
    }

    protected void restartOrThaw() {
    	// stub, may be overwritten
    	// contains common parts for restart() and initAfterThaw()
    }
    
    protected void initAfterThaw() {
    	restartOrThaw();
    }
    
    // Check Integrity
    public void setState( GameRuntime.GameState state ) {
    	switch ( state ) {
	    	case PAUSE:
	    		if ( gameState == GameRuntime.GameState.RUNNING ) {
	    	        GameTime.stop();
	    			gameState= state;
	    			break;
	    		}
	    		break;

	    	case RUNNING:
	    		if ( gameState == GameRuntime.GameState.INTRO
		    		 || gameState == GameRuntime.GameState.PAUSE )
	    		{
	    			gameState= state;
	    			break;
	    		}
	    		break;
    	}
    	Log.w("setState", "state: " + state + " gameState:" + gameState);
    }
    
    public void setSurfaceSize( int width, int height ) {
        	
    	Log.w("SetSurfaceSize", "width=" + width + ", height=" + height);
    	
    	mStage.mCanvasWidth = width;
    	mStage.mCanvasHeight = height;
    	mStage.setWidth(200);
    }

	public boolean handleKeyEvent(View v, int keyCode, KeyEvent event) {
    	// Empty
		return false;
	}

    public void pause() {
        setState(GameState.PAUSE);
    }

    public void unpause() {
        setState(GameState.RUNNING);
    }

    public void togglePause() {
    	if ( gameState == GameState.RUNNING ) { 
    		pause();
    		return;
    	}
    	unpause();
    }
    
    
    public void onSensorChanged( SensorEvent event ) {
    	// STUB
    }
}
