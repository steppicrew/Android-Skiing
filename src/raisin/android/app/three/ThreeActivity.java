package raisin.android.app.three;

import java.util.HashMap;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
// import android.view.WindowOrientationEventListener;
import android.view.SurfaceView;
import raisin.android.R;
import raisin.android.engine2.GameActivity;
import raisin.android.engine2.GameRuntime;

public class ThreeActivity extends GameActivity {

//    WindowOrientationEventListener orientationEventListener;

//    public int orientation= OrientationEventListener.ORIENTATION_UNKNOWN;
    
    public ThreeActivity() {
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
		return new ThreeRuntime();
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
	        mGLView.setRenderer(new StaticRectangleRenderer(this));
	
	        setContentView(mGLView);
	
	        return mGLView;
		}
	}
}
