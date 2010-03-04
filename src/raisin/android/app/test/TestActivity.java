package raisin.android.app.test;

public class TestActivity extends GameActivity2 {

	public TestActivity() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected final GameRuntime2 createGameRuntime() {
		return new TestRuntime();
	}
	
}
