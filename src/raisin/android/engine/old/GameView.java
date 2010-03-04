package raisin.android.engine.old;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class GameView extends SurfaceView
	implements SurfaceHolder.Callback, OnTouchListener
{

	private final class GameThread extends Thread {

        private SurfaceHolder mSurfaceHolder;
        private Context mContext;

        private boolean mRun = false;
                
		public GameThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {

            mSurfaceHolder = surfaceHolder;
			mContext= context;
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
                    	doSleep= GameRuntime.instance(mContext).refresh(canvas);
                    }
                    if ( doSleep ) {
						try {
							Log.w("Sleeping...", "");
							sleep(100);
						} catch (InterruptedException e) {
						}
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        	GameRuntime.instance(mContext).destroy();
        	Log.w("GameThread", "run end");
        }

        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
            	GameRuntime.instance(mContext).setSurfaceSize(width, height);
            }
        }

        public void restart() {
            synchronized (mSurfaceHolder) {
            	GameRuntime.instance(mContext).restart();
            }
        }
        
        public void pause() {
         	Log.w("GameThread", "pause start");
 
            synchronized (mSurfaceHolder) {
            	GameRuntime.instance(mContext).pause();
            }
        	Log.w("GameThread", "pause end");
        }

        public void unpause() {
        	Log.w("GameThread", "unpause start");
            synchronized (mSurfaceHolder) {
            	GameRuntime.instance(mContext).unpause();
            }
        	Log.w("GameThread", "unpause end");
        }

		public boolean handleKeyEvent(View v, int keyCode, KeyEvent event) {
            synchronized (mSurfaceHolder) {
            	return GameRuntime.instance(mContext).handleKeyEvent(v, keyCode, event);
            }
		}
	}

    /** The thread that actually draws the animation */
    private GameThread thread;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    private Context mContext;
    
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

    	Log.w("ActView", "constructor");
        
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mContext= context;

        setFocusable(true); // make sure we get key events
    }

    public void restartGame() {
    	thread.restart();
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if ( !hasWindowFocus && thread != null ) thread.pause();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

        thread = new GameThread(holder, mContext, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });
        
        setOnTouchListener(this);
        
        GameRuntime.thaw(mContext);
        
		thread.setRunning(true);
    	Log.w("GameThread", "thread.start start");
        thread.start();
    	Log.w("GameThread", "thread.start end");
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
    	Log.w("GameThread", "thread.join start");
        while ( retry ) {
            try {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e) {
            }
        }
    	Log.w("GameThread", "thread.join end");

    	GameRuntime.freeze();

    	thread= null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("Key", "Thread running: " + thread.isRunning());
		if ( thread.isRunning() ) {
			return thread.handleKeyEvent(this, keyCode, event);
		}
		return false;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.e("Key", "Thread running: " + thread.isRunning());
		if ( thread.isRunning() ) {
			return thread.handleKeyEvent(this, keyCode, event);
		}
		return false;
	}
	
	public void pause() {
		if ( thread != null ) thread.pause();
	}
	
	public void resume() {
		if ( thread == null ) Log.e("resume", "Thread is NULL!");
		if ( thread != null ) thread.unpause();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if ( thread == null ) Log.e("onTouch", "Thread is NULL!");
		if ( thread != null ) {
			thread.unpause();
			return true;
		}
		return false;
	}
}
