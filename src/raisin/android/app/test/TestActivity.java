package raisin.android.app.test;

import raisin.android.engine.GameActivity;
import raisin.android.engine.GameRuntime;

public class TestActivity extends GameActivity {

	protected final GameRuntime createGameRuntime() {
		return new TestRuntime();
	}
	
}
