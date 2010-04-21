package raisin.android.app.steppi;

import java.util.HashMap;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
// import android.view.WindowOrientationEventListener;
import android.view.SurfaceView;
import raisin.android.engine2.GameActivity;
import raisin.android.engine2.GameRuntime;

public class SteppiActivity extends GameActivity {

//    WindowOrientationEventListener orientationEventListener;

//    public int orientation= OrientationEventListener.ORIENTATION_UNKNOWN;
    
    public SteppiActivity() {
    	super();
    	
/*
        orientationEventListener= new OrientationEventListener(this) {

			
			@Override
			public void onOrientationChanged(int orientation_) {
				orientation= orientation_;
			}
		};
*/
    }
    
	class View2 extends SurfaceView {

		public View2(Context context) {
			super(context);

		}

	}

	// private View2 view;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
	@Override
	protected final GameRuntime createGameRuntime() {
		return new SteppiRuntime();
	}

	@Override
	protected final SurfaceView initView() {

		if ( false ) {
	        SurfaceView view= new View2(this);
	        setContentView(view);
	        
			return view;
		}
		
		// return (SurfaceView) findViewById(R.id.threeSurface);
		

		else {
	 	    GLSurfaceView mGLView;
	
	        // super.onCreate(savedInstanceState);
	        mGLView = new GLSurfaceView(this);
	        mGLView.setEGLConfigChooser(false);
	        mGLView.setRenderer(new RectangleRenderer(this));
	
	        setContentView(mGLView);
	
	        return mGLView;
		}
	}
}
