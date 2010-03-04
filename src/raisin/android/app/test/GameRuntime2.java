package raisin.android.app.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

@SuppressWarnings("serial")
public abstract class GameRuntime2 implements Serializable {

	// Static data
	
	public static enum GameState {
		INTRO, LOSE, PAUSE, READY, RUNNING, WIN,
	}

	public static Random random= new Random();

	private static ByteArrayOutputStream baos= new ByteArrayOutputStream();

    // Unserializable

	protected transient StageData mStage= new StageData();
	
	protected transient Context mContext;

	// Serializable

	protected GameRuntime2.GameState gameState;

	public void freeze() {
		try {
    		baos.reset();
    		ObjectOutputStream oos= new ObjectOutputStream(baos);
    		oos.writeObject(this);
    		oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	// TODO: Fix Exceptions!
	public static GameRuntime2 thaw( Context context ) throws Exception {
		if ( baos.size() == 0 ) throw new Exception("No Stream, no data");
		try {
    		ByteArrayInputStream bais= new ByteArrayInputStream(baos.toByteArray());
    		ObjectInputStream ois= new ObjectInputStream(bais);
    		GameRuntime2 instance= (GameRuntime2) ois.readObject();
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
    	mContext= context;
    }
    
    // Check Integrity
    public void setState( GameRuntime2.GameState state ) {
    	switch ( state ) {
	    	case PAUSE:
	    		if ( gameState == GameRuntime2.GameState.RUNNING ) {
	    	        GameTime2.stop();
	    			gameState= state;
	    			break;
	    		}
	    		break;

	    	case RUNNING:
	    		if ( gameState == GameRuntime2.GameState.INTRO
		    		 || gameState == GameRuntime2.GameState.PAUSE )
	    		{
	    			gameState= state;
	    			break;
	    		}
	    		break;
    	}
    	Log.w("setState", "state: " + state + " gameState:" + gameState);
    }
    
    public void restart() {
    	gameState= GameRuntime2.GameState.INTRO;
    }
    
    public void destroy() {
    	// Empty
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

	public boolean refresh(Canvas canvas) {
    	return false;
	}

}
