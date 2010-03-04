/**
 * 
 */
package raisin.android.app.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GameTime2 implements Serializable {
	
	public static GameTime2 master= new GameTime2(true);
	public static List<GameTime2> instances= new ArrayList<GameTime2>();

	public static void reset() {
		instances.clear();
	}
	
	public static GameTime2 newInstance() {
		GameTime2 instance= new GameTime2(false);
		return instance;
	}
	
	public static void stop() {
		for ( GameTime2 instance: instances ) instance.mLastTime -= master.mLastTime;
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
    		for ( GameTime2 instance: instances ) instance.mLastTime += master.mLastTime;
        }
		
		double elapsed = master.mLastTime == 0 ? 0 : (nowTime - master.mLastTime) / 1000.0;
		master.mLastTime= nowTime;
        return elapsed;
	}

	@SuppressWarnings("unused")
	private transient boolean isMaster;
	private long mLastTime;

	private GameTime2( boolean isMaster ) {
		this.isMaster= isMaster;
	}

    public void writeToStream(java.io.ObjectOutputStream out) throws IOException {
    	out.writeLong(mLastTime);
    }

    public void readFromStream(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    	long savedTime= in.readLong();
    	mLastTime= master.mLastTime + savedTime;
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
