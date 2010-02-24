/**
 * 
 */
package raisin.android.example.parallax;

import java.io.IOException;
import java.io.Serializable;

import raisin.android.engine.GameRuntime;
import raisin.android.engine.GameRuntime.GameState;
import raisin.android.example.parallax.Parallax.StageData;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
abstract class Sprite implements Comparable<Sprite>, Serializable {

	// Unserializable
	protected transient StageData mStageData;
	
	protected transient int width, height;
	protected transient int hotx, hoty;

	// Serializable
	protected double x, y, z;

	Sprite( StageData stageData ) {
		init(stageData);
	}

	public void init( StageData stageData ) {
		this.mStageData= stageData;
	}
	
	@Override
	public int compareTo( Sprite another ) {
		return (int)(y - another.y);
	}

	public abstract void update( GameRuntime.GameState state );
	public abstract void draw( Canvas canvas );

	protected void drawDrawable( Canvas canvas, Drawable drawable, int ofsx, int ofsy ) {
		int ix= (int)x - hotx + ofsx;
		int iy= (int)(y - mStageData.top) - hoty + ofsy;
		drawable.setBounds(ix, iy, ix + width, iy + height);
		drawable.draw(canvas);
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    	out.writeDouble(x);
    	out.writeDouble(y);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    	x= in.readDouble();
    	y= in.readDouble();
    }
}