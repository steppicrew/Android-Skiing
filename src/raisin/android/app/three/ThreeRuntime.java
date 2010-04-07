package raisin.android.app.three;

import java.io.Serializable;

import raisin.android.engine2.GameRuntime;
import raisin.android.engine2.GameTime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorEvent;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

@SuppressWarnings("serial")
public class ThreeRuntime extends GameRuntime implements Serializable {

    public ThreeRuntime() {
    	restart();
    }

    Paint mGreen;
    
    @Override
    public void init( Context context ) {
    	super.init(context);
    }
    
    private void fixContent() {
    	if ( mGreen == null ) {
        	
	        mGreen= new Paint();
	        mGreen.setColor(Color.GREEN);
    	}
    }
    
    private void update() {

        // Log.e("gameState", "" + gameState);
        
        double elapsed = GameTime.getElapsed();

    }

    private void draw( Canvas canvas ) {
    	
    	int x= GameRuntime.random.nextInt(100);
    	int y= GameRuntime.random.nextInt(100);
    	canvas.drawPoint(x, y, mGreen);
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
	// public void doOnSensorChanged( SensorEvent event ) {
	// }
    
    public void skipToNextState() {
		if ( gameState == GameState.INTRO ) setState(GameState.RUNNING);
		else if ( gameState == GameState.RUNNING ) setState(GameState.PAUSE);
		else if ( gameState == GameState.PAUSE ) setState(GameState.RUNNING);
    }
    
}
