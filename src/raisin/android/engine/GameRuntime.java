package raisin.android.engine;

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
public class GameRuntime implements Serializable {

	public static enum GameState {
		INTRO, LOSE, PAUSE, READY, RUNNING, WIN,
	}

	public static class StageData {
		public double top;
	    public transient int mCanvasHeight = 1;
	    public transient int mCanvasWidth = 1;
	}

	private static ByteArrayOutputStream baos= new ByteArrayOutputStream();

	private static GameRuntime instance;
	protected static Context mContext;
	
    protected static GameRuntime.StageData mStageData= new GameRuntime.StageData();

	public static Random random= new Random();
	
	// Serializable
	protected GameRuntime.GameState gameState;

	static public GameRuntime instance( Context context ) {
		if ( instance == null ) {
			instance= new raisin.android.example.parallax.Parallax();
			instance.init(context);
		}
		return instance;
	}

	public static void freeze() {
		try {
    		baos.reset();
    		if ( instance == null ) return;

    		ObjectOutputStream oos= new ObjectOutputStream(baos);
    		oos.writeObject(instance);
    		oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public static void thaw( Context context ) {
		if ( baos.size() == 0 ) return;
		try {
    		ByteArrayInputStream bais= new ByteArrayInputStream(baos.toByteArray());
    		ObjectInputStream ois= new ObjectInputStream(bais);
    		instance= (GameRuntime) ois.readObject();
    		instance.init(context);
    		ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    public void init( Context context ) {
    	mContext= context;
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
	    		if ( gameState == GameRuntime.GameState.PAUSE ) {
	    			gameState= state;
	    			break;
	    		}
	    		break;
    	}
    	Log.w("setState", "state: " + state + " gameState:" + gameState);
    }
    
    public void restart() {
    	// Empty
    }
    
    public void destroy() {
    	// Empty
    }
    
    public void setSurfaceSize( int width, int height ) {
    	// Empty
    }

	public boolean handleKeyEvent(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void unpause() {
		// TODO Auto-generated method stub
		
	}

	public void refresh(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

}
