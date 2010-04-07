package raisin.android.engine2;

// UNUSED

import raisin.android.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BitmapView extends SurfaceView {

	private GameView mGameView;
	
	public BitmapView(Context context) {
		super(context);

        // mGameView = (GameView) findViewById(R.id.surface);

	}

/*
	static public GameView2 GEN(Activity activity) {
		
        mGameView = (GameView) findViewById(R.id.surface);
        mGameView.setGameRuntime(createGameRuntime());

	}
*/

/* 
	@Override
	public void setGameView(GameView gameView) {
		mGameView= gameView;
	}

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        mGameView.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mGameView.surfaceChanged(holder, format, width, height);
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mGameView.surfaceCreated(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mGameView.surfaceDestroyed(holder);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mGameView.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return mGameView.onKeyUp(keyCode, event);
	}
*/
}
