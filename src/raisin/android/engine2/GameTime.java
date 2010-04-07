/**
 * 
 */
package raisin.android.engine2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GameTime implements Serializable {
	
	public static GameTime master= new GameTime();
	public static List<GameTime> instances= new ArrayList<GameTime>();

	private long mLastTime;
	
	public static void reset() {
		instances.clear();
	}

	public static void register(GameTime gameTime) {
		instances.add(gameTime);
	}
	
	public static void unregister(GameTime gameTime) {
		instances.remove(gameTime);
	}
	
	public static void stop() {
		for ( GameTime instance: instances ) instance.mLastTime -= master.mLastTime;
		master.mLastTime= 0;
	}

	public static void resume() {
		if ( master.mLastTime > 0 ) return;

		master.mLastTime= now();
		for ( GameTime instance: instances ) instance.mLastTime += master.mLastTime;
	}

    private static final long now() {
    	return System.currentTimeMillis();
    }
	
	public static double getElapsed() {

		long nowTime= now();

        if ( master.mLastTime > nowTime ) return 0;

        if ( master.mLastTime == 0 ) {
        	master.mLastTime= nowTime;

        	// Wenn mLastTime 0 ist, enthalten folgende Zeitvariablen
        	// relative Werte und muessen angepasst werden
    		for ( GameTime instance: instances ) instance.mLastTime += master.mLastTime;
        }
		
		double elapsed = master.mLastTime == 0 ? 0 : (nowTime - master.mLastTime) / 1000.0;
		master.mLastTime= nowTime;
        return elapsed;
	}

	public void setOffset( long offset ) {
		mLastTime= master.mLastTime + offset;
	}

	public long getOffset() {
		return mLastTime - master.mLastTime;
	}
	
	public boolean runOut() {
		return mLastTime < master.mLastTime;
	}
}
