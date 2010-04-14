/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package raisin.android.app.three;

import static android.opengl.GLES10.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;

import raisin.android.R;

/**
 * A GLSurfaceView.Renderer that uses the Android-specific
 * android.opengl.GLESXXX static OpenGL ES APIs. The static APIs
 * expose more of the OpenGL ES features than the
 * javax.microedition.khronos.opengles APIs, and also
 * provide a programming model that is closer to the C OpenGL ES APIs, which
 * may make it easier to reuse code and documentation written for the
 * C OpenGL ES APIs.
 *
 */
public class StaticRectangleRenderer implements GLSurfaceView.Renderer{

	static private float clamp(float value, float low, float high) {
		if ( value < low ) return low;
		if ( value > high ) return high;
		return value;
	}
	
	@SuppressWarnings("serial")
	static class XORShiftRandom extends Random {
		private long seed = System.nanoTime();

		protected int next(int nbits) {
			long x = seed;
			x ^= (x << 21);
			x ^= (x >>> 35);
			x ^= (x << 4);
			seed = x;
			return (int)(x & ((1L << nbits) - 1));
		}

		public float nextPlusMinus(float factor) {
			return nextFloat() * factor * 2f - factor;
		}
		
        public float plusMinusClamped(float value, float factor, float clamp) {
	        value += nextFloat() * factor * 2f - factor;
	        if ( value < -clamp ) return -clamp;
	        if ( value > clamp ) return clamp;
	        return value;
        }

	}

	static XORShiftRandom random= new XORShiftRandom();

    private Context mContext;
    private final static int SNOWFLAKE_COUNT = 3;
    private int[] mTextureIDs= new int[SNOWFLAKE_COUNT];

	static class Snowflake {
		private float posx;
//		private float posy;
		private float posz;
		private float speed;
		private long started;
		private Rectangle rectangle;
		private int textureID;
		private boolean finished;
		private float driftx;
		private float jitterx;
		private float jittery;
		private float angleSpeed;

		public Snowflake(int[] textureIDs) {
			rectangle= new Rectangle();
			textureID= textureIDs[random.nextInt(textureIDs.length)];
			restart();
		}
		
		public void restart() {
			started= SystemClock.uptimeMillis();
			posx= random.nextPlusMinus(ThreeRuntime.screenWidth / 392f);
			posz= 0.5f + random.nextPlusMinus(0.5f);
			driftx= random.nextPlusMinus(0.0001f);
			speed= 0.0003f + random.nextFloat() * 0.0003f;
			angleSpeed= random.nextPlusMinus(0.090f);
		}

//		private long rnd;
//		
//		public long randomLong() {
//			  rnd ^= (rnd << 21);
//			  rnd ^= (rnd >>> 35);
//			  rnd ^= (rnd << 4);
//			  return rnd;
//		}
//
//		public float randomFloat() {
//			  rnd ^= (rnd << 21);
//			  rnd ^= (rnd >>> 35);
//			  rnd ^= (rnd << 4);
//			  return rnd / ;
//		}

		public void draw(GL10 gl) {

            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            long diff = SystemClock.uptimeMillis() - started;

	        glPushMatrix();

	        jitterx= random.plusMinusClamped(jitterx, 0.0025f, 0.2f);
	        jittery= random.plusMinusClamped(jittery, 0.0025f, 0.1f);

	        float x= posx + diff * driftx + jitterx;
	        float y= 2f - diff * speed + jittery;
	        float z= posz;
	        
//	        glTranslatef(x, y, z);

	        float angle = angleSpeed * diff;
	        glRotatef(angle, 0, 0, 1.0f);
	        
	        float scale= 0.3f * (1 - z);
//	        glScalef(scale, scale, 1f);
	        
	        rectangle.draw(gl);

	        glEnable(GL_BLEND);
	        
	        
	        
	        
	        glPopMatrix();

	        //float y= 2f-diff*speed;
	        
	        finished = y < ThreeRuntime.screenHeight * -2f / 653f;
		}
		
		public boolean finished() {
			return finished;
		}
	}
	
	List<Snowflake> snowflakes= new ArrayList<Snowflake>();

	// Snowflake snowflake;
	
    public StaticRectangleRenderer(Context context) {
        mContext = context;
        
        // mRectangle = new Rectangle();
        // snowflake= new Snowflake();

    }
    
    private int SnowflakeResource(int num) {
    	switch (num) {
    	default: return R.drawable.snowflake1;
    	case 1: return R.drawable.snowflake2;
    	case 2: return R.drawable.snowflake3;
    	}
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        glDisable(GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

        glClearColor(.5f, .7f, .99f, 1);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glFrontFace(GL_CCW);
        glCullFace(GL_BACK); 
        glEnable(GL_CULL_FACE);

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */

        glGenTextures(SNOWFLAKE_COUNT, mTextureIDs, 0);

        for (int i= 0; i < SNOWFLAKE_COUNT; i++) {
	        glBindTexture(GL_TEXTURE_2D, mTextureIDs[i]);
	
	        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);  // was GL_NEAREST
	        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	
	        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	
	        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
	
	        
	        
	        glEnable(GL_ALPHA_TEST);
	        glAlphaFunc(GL_GREATER, 0.5f);
	
	        InputStream is = mContext.getResources()
	                .openRawResource(SnowflakeResource(i));
	        Bitmap bitmap;
	        try {
	            bitmap = BitmapFactory.decodeStream(is);
	        } finally {
	            try {
	                is.close();
	            } catch(IOException e) {
	                // Ignore.
	            }
	        }
	
	        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
	        bitmap.recycle();
        }
        for ( int i= 0; i < 1; i++ ) snowflakes.add(new Snowflake(mTextureIDs));
    }

    public void onDrawFrame(GL10 gl) {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        // glDisable(GL_DITHER);

        // glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        
        // http://www.gamedev.net/community/forums/topic.asp?topic_id=462270
        // glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_BLEND);
        // glEnable(GL_BLEND);
        // glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        /*
         * Usually, the first thing one might want to do is to clear
         * the screen. The most efficient way of doing this is to use
         * glClear().
         */

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        /*
         * Now we're ready to draw some 3D objects
         */

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // float eyex= (float) Math.sin(SystemClock.uptimeMillis() / 1000f);
        
        float eyex= clamp(ThreeRuntime.orientationX / -10f, -3f, 3f);
        float eyey= clamp(ThreeRuntime.orientationY / -10f, -2f, 2f);
        
        GLU.gluLookAt(gl, eyex, eyey, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glActiveTexture(GL_TEXTURE0);

/*
        for ( Snowflake snowflake: snowflakes ) {
        	snowflake.draw(gl);
        	if ( snowflake.finished() ) snowflake.restart();
        } */
        
        for ( int i= 0; i < snowflakes.size(); i++ ) {
        	snowflakes.get(i).draw(gl);
        	if ( snowflakes.get(i).finished() ) snowflakes.get(i).restart();
        	
        }
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        glViewport(0, 0, w, h);

        /*
        * Set our projection matrix. This doesn't have to be done
        * each time we draw, but usually a new projection needs to
        * be set when the viewport is resized.
        */

        float ratio = (float) w / h;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustumf(-ratio, ratio, -1, 1, 3, 7);
        
        
        ThreeRuntime.horizontalOrientation= w > h;
        ThreeRuntime.screenWidth= w;
        ThreeRuntime.screenHeight= h;
    }

    // private Rectangle mRectangle;
    static class Rectangle {
        public Rectangle() {

            // Buffers to be passed to gl*Pointer() functions
            // must be direct, i.e., they must be placed on the
            // native heap where the garbage collector cannot
            // move them.
            //
            // Buffers with multi-byte datatypes (e.g., short, int, float)
            // must have their byte order set to native order

            ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
            vbb.order(ByteOrder.nativeOrder());
            mFVertexBuffer = vbb.asFloatBuffer();

            ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
            tbb.order(ByteOrder.nativeOrder());
            mTexBuffer = tbb.asFloatBuffer();

            ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
            ibb.order(ByteOrder.nativeOrder());
            mIndexBuffer = ibb.asShortBuffer();

            // A unit-sided equilateral triangle centered on the origin.
            float[] coords = {
                    // X, Y, Z
                    -0.5f,  0.5f, 0,
                     0.5f,  0.5f, 0,
                    -0.5f, -0.5f, 0,
                     0.5f, -0.5f, 0
            };

            for (int i = 0; i < VERTS; i++) {
                for(int j = 0; j < 3; j++) {
                    mFVertexBuffer.put(coords[i*3+j] * 1.0f);
                }
            }

            for (int i = 0; i < VERTS; i++) {
                for(int j = 0; j < 2; j++) {
                    mTexBuffer.put(coords[i*3+j] * 1.0f + 0.5f);
                }
            }

            for(int i = 0; i < VERTS; i++) {
                mIndexBuffer.put((short) i);
            }

            mFVertexBuffer.position(0);
            mTexBuffer.position(0);
            mIndexBuffer.position(0);
        }

        public void draw(GL10 gl) {
            glFrontFace(GL_CCW);
            glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer);
            glEnable(GL_TEXTURE_2D);
            glTexCoordPointer(2, GL_FLOAT, 0, mTexBuffer);
            glDrawElements(GL_TRIANGLE_STRIP, VERTS, GL_UNSIGNED_SHORT, mIndexBuffer);
        }

        private final static int VERTS = 4;

        private FloatBuffer mFVertexBuffer;
        private FloatBuffer mTexBuffer;
        private ShortBuffer mIndexBuffer;
    }
}
