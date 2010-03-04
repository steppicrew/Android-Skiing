package raisin.android.engine;

import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;

public class GameThread extends Thread {

    private SurfaceHolder mSurfaceHolder;
    private GameRuntime mGameRuntime;
    
    private boolean mRun = false;
            
	public GameThread(SurfaceHolder surfaceHolder, GameRuntime gameRuntime,
			Handler handler) {

        mSurfaceHolder = surfaceHolder;
		mGameRuntime= gameRuntime;
    }

	public boolean isRunning() {
		return mRun;
	}
	
	public void setRunning(boolean running) {
        mRun = running;
    }
    
    @Override
    public void run() {
    	Log.w("GameThread", "run start");
    	while (mRun) {
            Canvas canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas(null);
                boolean doSleep;
                synchronized (mSurfaceHolder) {
                	doSleep= mGameRuntime.refresh(canvas);
                }
                if ( doSleep ) {
					try {
						Log.w("Sleeping...", "");
						sleep(100);
					} catch (InterruptedException e) {
					}
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    	// mGameRuntime.destroy();
    	// mGameRuntime= null;
    	Log.w("GameThread", "run end");
    }

    public void setSurfaceSize(int width, int height) {
        // synchronized to make sure these all change atomically
        synchronized (mSurfaceHolder) {
        	mGameRuntime.setSurfaceSize(width, height);
        }
    }

    public void restart() {
        synchronized (mSurfaceHolder) {
        	mGameRuntime.restart();
        }
    }
    
    public void pause() {
     	Log.w("GameThread", "pause start");

        synchronized (mSurfaceHolder) {
        	mGameRuntime.pause();
        }
    	Log.w("GameThread", "pause end");
    }

    public void unpause() {
    	Log.w("GameThread", "unpause start");
        synchronized (mSurfaceHolder) {
        	mGameRuntime.unpause();
        }
    	Log.w("GameThread", "unpause end");
    }

	public boolean handleKeyEvent(View v, int keyCode, KeyEvent event) {
        synchronized (mSurfaceHolder) {
        	return mGameRuntime.handleKeyEvent(v, keyCode, event);
        }
	}
}
