package raisin.android.engine2;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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


public class GameThreaded 
	implements SurfaceHolder.Callback,
		SensorEventListener,
		OnTouchListener
{

	private SurfaceView view;
	
    /** The thread that actually draws the animation */
    private GameThread thread;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    // private Context mContext;

    private GameRuntime mGameRuntime;
    
    public GameThreaded(SurfaceView view, Context context) {
        this.view= view;
        
        // view.setGameView(this);
        
    	Log.w("ActView", "constructor");

        view.setOnTouchListener(this);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = view.getHolder();
        holder.addCallback(this);

        // mContext= context;

        view.setFocusable(true); // make sure we get key events
    }

    
    public void setGameRuntime( GameRuntime gameRuntime ) {
    	mGameRuntime= gameRuntime;
    	mGameRuntime.init(view.getContext());
	}

    // SENSOR

    private SensorManager mSensorManager;
	private Sensor mSensor;

    @Override
	public void onAccuracyChanged( Sensor sensor, int accuracy ) {
		// TODO Auto-generated method stub
		
	}

	// public abstract void doOnSensorChanged( SensorEvent event );

	@Override
	public void onSensorChanged( SensorEvent event ) {
		// if ( event.values.length > 0 ) Log.w("Sensor 0", "" + event.values[0]);
		// if ( event.values.length > 1 ) Log.w("Sensor 1", "" + event.values[1]);
		// if ( event.values.length > 2 ) Log.w("Sensor 2", "" + event.values[2]);
		
		if ( mGameRuntime == null ) return;

		if ( event.values.length > 2 ) {
			mGameRuntime.onSensorChanged(event);
		}
	}

    
    public void restartGame() {
    	thread.restart();
    }
    
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
        	newGameRuntime= GameRuntime.thaw(view.getContext());
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
        
    	mSensorManager = (SensorManager) view.getContext().getSystemService(Context.SENSOR_SERVICE);
    	List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
    	if ( sensors.size() > 0 ) {
    		mSensor= sensors.get(0);
    		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    	}
    	else {
    		mSensorManager= null;
    	}

		thread.setRunning(true);
    	Log.w("GameThread", "thread.start start");
        // thread.start();
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
    	
    	if ( mSensorManager != null ) {
    		mSensorManager.unregisterListener(this, mSensor);
    	}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("Key", "Thread running: " + thread.isRunning());
		if ( thread.isRunning() ) {
			return thread.handleKeyEvent(view, keyCode, event);
		}
		return false;
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.e("Key", "Thread running: " + thread.isRunning());

    	// Log.e("Key", "" + keyCode + " event: " + event);
    	// if ( super.handleKeyEvent(view, keyCode, event) ) return true;

    	if ( thread.isRunning() ) {
			return thread.handleKeyEvent(view, keyCode, event);
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
