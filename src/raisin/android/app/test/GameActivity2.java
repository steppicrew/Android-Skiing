package raisin.android.app.test;

import raisin.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public abstract class GameActivity2 extends Activity {

	final int MENU_NEW_GAME = 1;
	final int MENU_QUIT = 2;

    /** A handle to the thread that's actually running the animation. */
    // private GameThread mGameThread;

    /** A handle to the View in which the game is running. */
    private GameView2 mGameView;

    // private Game game;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.test);
        
        mGameView = (GameView2) findViewById(R.id.surface);
        mGameView.setGameRuntime(createGameRuntime());

        // mGameThread = mGameView.getThread();
    }

	protected abstract GameRuntime2 createGameRuntime();

    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_NEW_GAME, 0, "New Game");
        menu.add(0, MENU_QUIT, 0, "Quit");
        return true;
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {

    	Log.w("Act1", "onPause");
        
    	super.onPause();
        mGameView.pause();
    }

    @Override
	protected void onResume() {
		super.onResume();
        mGameView.resume();
	}

	/* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

	        case MENU_NEW_GAME:

	            mGameView.restartGame();

	/*
	        	final CharSequence[] items = {"Red", "Green", "Blue"};
	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle("Pick a color");
	        	builder.setItems(items, new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int item) {
	        	        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	        	    }
	        	});
	        	AlertDialog alert = builder.create();
	        	alert.show();
	        	// newGame();
*/
	            return true;
	
	        case MENU_QUIT:
	        	finish();
	            return true;
        }
        return false;
    }

}