package raisin.android.engine2;

import java.io.Serializable;

import raisin.android.engine.math.Point3d;
import android.graphics.Canvas;

@SuppressWarnings("serial")
public class StageData implements Serializable {
	
	public transient int mCanvasHeight = 1;
	public transient int mCanvasWidth = 1;
	
	public Point3d origin= new Point3d();
	
	private transient double width;
	
	// getSlopeWidth()
	public void setWidth( double width ) {
		this.width= width;
	}
	
	public Point3d getPointOfView() {
		return new Point3d(width / 2, 0, 100);
	}
	
	public double getProjection(Canvas canvas) {
		return canvas.getWidth() / width;
	}

}
