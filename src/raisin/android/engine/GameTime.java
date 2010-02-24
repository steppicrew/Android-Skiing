/**
 * 
 */
package raisin.android.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameTime {
	
	public static GameTime master= new GameTime(true);
	public static List<GameTime> instances= new ArrayList<GameTime>();

	public static void reset() {
		instances.clear();
	}
	
	public static GameTime newInstance() {
		GameTime instance= new GameTime(false);
		return instance;
	}
	
	public static void stop() {
		for ( GameTime instance: instances ) instance.mLastTime -= master.mLastTime;
		master.mLastTime= 0;
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
        	// relative Werte und m�ssen angepasst werden
    		for ( GameTime instance: instances ) instance.mLastTime += master.mLastTime;
        }
		
		double elapsed = master.mLastTime == 0 ? 0 : (nowTime - master.mLastTime) / 1000.0;
		master.mLastTime= nowTime;
        return elapsed;
	}

	private transient boolean isMaster;
	private transient long mLastTime;

	private GameTime( boolean isMaster ) {
		this.isMaster= isMaster;
	}

    public void readFromStream(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    	GameTime instance= (GameTime) in.readObject();
    	mLastTime= master.mLastTime + instance.mLastTime;
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
