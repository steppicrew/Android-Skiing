package raisin.android.engine.old;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

import raisin.android.engine.math.Point3d;


import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

@SuppressWarnings("serial")
public class GameRuntime implements Serializable {

	// Static data
	
	public static enum GameState {
		INTRO, LOSE, PAUSE, READY, RUNNING, WIN,
	}

	public static transient int mCanvasHeight = 1;
	public static transient int mCanvasWidth = 1;
	
	public static class StageData {
		public Point3d origin= new Point3d();
		public Point3d getPointOfView() {
			return new Point3d(getSlopeWidth() / 2, 0, 100);
		}
		public double getSlopeWidth() {
			return mCanvasHeight > mCanvasWidth ? 200 : (getSlopeHeight() / mCanvasHeight * mCanvasWidth);
		}
		public double getSlopeHeight() {
			return mCanvasWidth > mCanvasHeight ? 200 : (getSlopeWidth() / mCanvasWidth * mCanvasHeight);
		}
		public double getProjection(Canvas canvas) {
			return canvas.getWidth() / getSlopeWidth();
		}
	}

	public static Random random= new Random();

	private static ByteArrayOutputStream baos= new ByteArrayOutputStream();

	private static GameRuntime instance;
	
    protected static GameRuntime.StageData mStage= new GameRuntime.StageData();

    // Unserializable

	protected transient Context mContext;

	// Serializable

	protected GameRuntime.GameState gameState;

	static public GameRuntime instance( Context context ) {
		if ( instance == null ) {
			instance= new raisin.android.app.parallax.Parallax();
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
    
    public void restart() {
    	gameState= GameRuntime.GameState.INTRO;
    }
    
    public void destroy() {
    	// Empty
    }
    
    public void setSurfaceSize( int width, int height ) {
        	
    	Log.w("SetSurfaceSize", "width=" + width + ", height=" + height);
    	
    	GameRuntime.mCanvasWidth = width;
    	GameRuntime.mCanvasHeight = height;
    }

	public boolean handleKeyEvent(View v, int keyCode, KeyEvent event) {
    	// Empty
		return false;
	}

    public void pause() {
        setState(GameRuntime.GameState.PAUSE);
    }

    public void unpause() {
        setState(GameRuntime.GameState.RUNNING);
    }

	public boolean refresh(Canvas canvas) {
    	return false;
	}

}
