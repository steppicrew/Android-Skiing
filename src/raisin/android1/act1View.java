package raisin.android1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.TextView;

public class act1View extends SurfaceView implements SurfaceHolder.Callback, OnKeyListener {

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
                    synchronized (mSurfaceHolder) {
                    	GameBase.instance(mContext).refresh(canvas);
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
        	GameBase.instance(mContext).destroy();
        	Log.w("GameThread", "run end");
        }

        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
            	GameBase.instance(mContext).setSurfaceSize(width, height);
            }
        }

        public void restart() {
            synchronized (mSurfaceHolder) {
            	GameBase.instance(mContext).restart();
            }
        }
        
        public void pause() {
         	Log.w("GameThread", "pause start");
 
            synchronized (mSurfaceHolder) {
            	GameBase.instance(mContext).pause();
            }
        	Log.w("GameThread", "pause end");
        }

        public void unpause() {
        	Log.w("GameThread", "unpause start");
            synchronized (mSurfaceHolder) {
            	GameBase.instance(mContext).unpause();
            }
        	Log.w("GameThread", "unpause end");
        }

		public boolean handleKeyEvent(View v, int keyCode, KeyEvent event) {
            synchronized (mSurfaceHolder) {
            	return GameBase.instance(mContext).handleKeyEvent(v, keyCode, event);
            }
		}
	}

    /** The thread that actually draws the animation */
    private GameThread thread;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    private Context mContext;
    
    public act1View(Context context, AttributeSet attrs) {
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
        
        setOnKeyListener(this);
        
        GameBase.thaw(mContext);
        
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

    	GameBase.freeze();

    	thread= null;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ( thread.isRunning() ) {
			return thread.handleKeyEvent(v, keyCode, event);
		}
		return false;
	}
	
	public void pause() {
		if ( thread != null ) thread.pause();
	}
	
	public void resume() {
		if ( thread != null ) thread.unpause();
	}
	
	// public GameThread getThread() {
    //     return thread;
	//}
}
