package raisin.android.app.test;

import android.graphics.Canvas;
import android.hardware.SensorEvent;
import android.view.KeyEvent;
import android.view.View;

public interface IGameListener {

    public boolean refresh( Canvas canvas );
	public void onSensorChanged( SensorEvent event );
    // public void destroy();
    public void skipToNextState();
	public boolean handleKeyEvent( View v, int keyCode, KeyEvent event );
}
