package raisin.android.app.three;

import raisin.android.engine.GameActivity;
import raisin.android.engine.GameRuntime;

public class ThreeActivity extends GameActivity {

	protected final GameRuntime createGameRuntime() {
		return new ThreeRuntime();
	}
	
}
