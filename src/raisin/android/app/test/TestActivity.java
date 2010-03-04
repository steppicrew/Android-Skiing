package raisin.android.app.test;

import raisin.android.engine.GameActivity;
import raisin.android.engine.GameRuntime;

public class TestActivity extends GameActivity {

	public TestActivity() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected final GameRuntime createGameRuntime() {
		return new TestRuntime();
	}
	
}
