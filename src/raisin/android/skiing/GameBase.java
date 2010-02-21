package raisin.android.skiing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.View;

@SuppressWarnings("serial")
public class GameBase implements Serializable {

	private static ByteArrayOutputStream baos= new ByteArrayOutputStream();

	private static Game instance;
	protected static Context mContext;

	static public GameBase instance( Context context ) {
		if ( instance == null ) {
			instance= new Game();
			instance.init(context);
		}
		return instance;
	}

	public static void freeze() {
		try {
    		baos.reset();
    		if ( instance == null ) return;

    		ObjectOutputStream oos= new ObjectOutputStream(baos);
    		oos.writeObject(instance);
    		oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public static void thaw( Context context ) {
		if ( baos.size() == 0 ) return;
		try {
    		ByteArrayInputStream bais= new ByteArrayInputStream(baos.toByteArray());
    		ObjectInputStream ois= new ObjectInputStream(bais);
    		instance= (Game) ois.readObject();
    		instance.init(context);
    		ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    public void init( Context context ) {
    	mContext= context;
    }
    
    public void restart() {
    	// Empty
    }
    
    public void refresh( Canvas canvas ) {
    	// Empty
    }

    public void destroy() {
    	// Empty
    }
    
    public void setSurfaceSize( int width, int height ) {
    	// Empty
    }

	public boolean handleKeyEvent(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void unpause() {
		// TODO Auto-generated method stub
		
	}

}
