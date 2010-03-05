package raisin.android.engine;

import android.content.Context;
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

    /** The thread that actually draws the animation */
    private GameThread thread;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    // private Context mContext;

    private GameRuntime mGameRuntime;
    
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

    	Log.w("ActView", "constructor");
        
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // mContext= context;

        setFocusable(true); // make sure we get key events
    }

    public void setGameRuntime( GameRuntime gameRuntime ) {
    	mGameRuntime= gameRuntime;
    	mGameRuntime.init(getContext());
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

    	GameRuntime newGameRuntime= null;
        try {
        	newGameRuntime= GameRuntime.thaw(getContext());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if ( newGameRuntime != null ) mGameRuntime= newGameRuntime;
        
        thread = new GameThread(holder, mGameRuntime, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });
        
        setOnTouchListener(this);
        
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

    	mGameRuntime.freeze();

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
		if ( event.getAction() != MotionEvent.ACTION_DOWN ) return false;
	
		if ( thread == null ) Log.e("onTouch", "Thread is NULL!");
		if ( thread != null ) {
			thread.togglePause();
			return true;
		}
		return false;
	}
}
