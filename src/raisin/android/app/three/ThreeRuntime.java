package raisin.android.app.three;

import java.io.Serializable;

import raisin.android.engine.GameRuntime;
import raisin.android.engine.GameTime;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.SensorEvent;

@SuppressWarnings("serial")
public class ThreeRuntime extends GameRuntime implements Serializable {

    public ThreeRuntime() {
    	restart();
    }

    @Override
    public void init( Context context ) {
    	super.init(context);
    }
    
    private void fixContent() {
    }
    
    private void update() {

        // Log.e("gameState", "" + gameState);
        
        double elapsed = GameTime.getElapsed();

    }

    private void draw( Canvas canvas ) {
    }

    @Override
    public boolean refresh( Canvas canvas ) {
        fixContent();
        if ( gameState == GameState.RUNNING ) update();
    	draw(canvas);

    	return gameState == GameState.INTRO
    	 	|| gameState == GameState.PAUSE;
    }

	@Override
	public void doOnSensorChanged( SensorEvent event ) {
	}
    
    public void skipToNextState() {
		if ( gameState == GameState.INTRO ) setState(GameState.RUNNING);
		else if ( gameState == GameState.RUNNING ) setState(GameState.PAUSE);
		else if ( gameState == GameState.PAUSE ) setState(GameState.RUNNING);
    }
    
}
