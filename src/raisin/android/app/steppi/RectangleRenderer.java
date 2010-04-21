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

package raisin.android.app.steppi;

import static android.opengl.GLES11.*;

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
 * android.opengl.GLESXXX static OpenGL ES APIs. The static APIs expose more of
 * the OpenGL ES features than the javax.microedition.khronos.opengles APIs, and
 * also provide a programming model that is closer to the C OpenGL ES APIs,
 * which may make it easier to reuse code and documentation written for the C
 * OpenGL ES APIs.
 * 
 */
public class RectangleRenderer implements GLSurfaceView.Renderer {

	static private float clamp(float value, float low, float high) {
		if (value < low)
			return low;
		if (value > high)
			return high;
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
			return (int) (x & ((1L << nbits) - 1));
		}

		public float nextPlusMinus(float factor) {
			return nextFloat() * factor * 2f - factor;
		}

		public float plusMinusClamped(float value, float factor, float clamp) {
			value += nextFloat() * factor * 2f - factor;
			if (value < -clamp)
				return -clamp;
			if (value > clamp)
				return clamp;
			return value;
		}

	}

	static XORShiftRandom random = new XORShiftRandom();

	private Context mContext;

	private final static int[] SNOWFLAKE_TEXTURE_RESOURCES = {
		R.drawable.snowflake1, R.drawable.snowflake2,
		R.drawable.snowflake3, };
	private int[] mSnowflakeTextureIDs = new int[SNOWFLAKE_TEXTURE_RESOURCES.length];

	private final static int[] MISC_TEXTURE_RESOURCES = {};
	private int[] mMiscTextureIDs = new int[MISC_TEXTURE_RESOURCES.length];

	final static int SNOWFLAKE_COUNT = 3;

	// rectangle with texture
	static class TexturedRectangle {
		private Rectangle mRectangle;
		private int mTextureID;

		public TexturedRectangle(int TextureID) {
			mRectangle = new Rectangle();
			mTextureID = TextureID;
		}

		public void draw(GL10 gl) {
			glBindTexture(GL_TEXTURE_2D, mTextureID);

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA,
					GL_ONE_MINUS_SRC_ALPHA);
			// glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			// glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			mRectangle.draw(gl);
			glDisable(GL_BLEND);
		}
	}

	static class Snowflake {
		private float posx;
		// private float posy;
		private float posz;
		private float speed;
		private long started;
		private TexturedRectangle mTexRectangle;
		private boolean finished;
		private float driftx;
		private float jitterx;
		private float jittery;
		private float angleSpeed;

		public Snowflake(TexturedRectangle[] texRectangles) {
			// mTexRectangle=
			// texRectangles[random.nextInt(texRectangles.length)];
			mTexRectangle = texRectangles[0];
			restart();
		}

		public void restart() {
			started = SystemClock.uptimeMillis();
			posx = random.nextPlusMinus(SteppiRuntime.screenWidth / 392f);
			posz = 0.5f + random.nextPlusMinus(0.5f);
			driftx = random.nextPlusMinus(0.0001f);
			speed = 0.0003f + random.nextFloat() * 0.0003f;
			angleSpeed = random.nextPlusMinus(0.090f);
		}

		// private long rnd;
		//		
		// public long randomLong() {
		// rnd ^= (rnd << 21);
		// rnd ^= (rnd >>> 35);
		// rnd ^= (rnd << 4);
		// return rnd;
		// }
		//
		// public float randomFloat() {
		// rnd ^= (rnd << 21);
		// rnd ^= (rnd >>> 35);
		// rnd ^= (rnd << 4);
		// return rnd / ;
		// }

		public void draw(GL10 gl) {
			
			long diff = SystemClock.uptimeMillis() - started;

			glEnable(GL_TEXTURE_2D);
			glPushMatrix();
//			glLoadIdentity();

//			glBlendFunc(GL_SRC_ALPHA, GL_ONE);

			jitterx = random.plusMinusClamped(jitterx, 0.0025f, 0.2f);
			jittery = random.plusMinusClamped(jittery, 0.0025f, 0.1f);

			float x = posx + diff * driftx + jitterx;
			float y = 2f - diff * speed + jittery;
			float z = posz;

			glTranslatef(x, y, z);

			float angle = angleSpeed * diff;
			glRotatef(angle, 0, 0, 1.0f);

			// float scale= 0.3f * (1 - z);
			// glScalef(scale, scale, 1f);

if (true) {
			mTexRectangle.draw(gl);
}			
if (true) {
			glBindTexture(GL_TEXTURE_2D, 0);
			glColor4f(1.00f, 1.00f, 1.0f, 0.50f);
//			glColor4f(1.00f, 1.00f, 1.0f, 0.00f);
//			glColor4f(1.00f, 1.00f, 1.0f, 1.00f);

//			glColor4f(0.50f, 0.50f, 0.50f, 0.50f);
//			glColor4f(1.00f, 0.00f, 0.00f, 0.50f);

//			glColor4f(.5f, .7f, .99f, 0.50f);
			Rectangle rect= new Rectangle();

			glEnable(GL_BLEND);
//			glBlendFunc(GL_SRC_ALPHA_SATURATE,
//					GL_DST_COLOR);
			glBlendFunc(GL_DST_COLOR,
					GL_ZERO);
			
			rect.draw(gl);
			
			glDisable(GL_BLEND);
}

if (false) {
			mTexRectangle.draw(gl);
}	
			
//			glColorMask(true, false, false, false);
//			glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			
			
			// glColorMask(false, false, false, true);
			// glClearColor(.5f, .7f, .99f, 0.5f);

			/*
			 * glEnable(GL_BLEND);
			 * 
			 * glBlendFunc(GL_DST_COLOR, GL_SRC_ALPHA);
			 * 
			 * glBindTexture(GL_TEXTURE_2D, blendID); //
			 * glBindTexture(GL_TEXTURE_2D, textureID);
			 * glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			 * glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			 * rectangle.draw(gl);
			 */
			glPopMatrix();

			// float y= 2f-diff*speed;

			finished = y < SteppiRuntime.screenHeight * -2f / 653f;
		}

		public boolean finished() {
			return finished;
		}
	}

	List<Snowflake> snowflakes = new ArrayList<Snowflake>();

	// Snowflake snowflake;

	public RectangleRenderer(Context context) {
		mContext = context;

		// mRectangle = new Rectangle();
		// snowflake= new Snowflake();

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		
		glDisable(GL_DITHER);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND); // Turn Blending On

		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		// 
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glFrontFace(GL_CCW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);

		glClearColor(.5f, .7f, .99f, 1f);
		glClearDepthf(1.0f);
//		glDepthFunc(GL_LEQUAL);
//		glEnable(GL_DEPTH_TEST);
		glShadeModel(GL_SMOOTH);

//		glColor4f(0.10f, 0.10f, 0.10f, 0.10f);
//		glBlendFunc(GL_SRC_ALPHA,
//				GL_ONE_MINUS_SRC_ALPHA);

		/*
		 * glEnable(GL_ALPHA_TEST); glAlphaFunc(GL_GREATER, 0.5f);
		 */

		/*
		 * Create our texture. This has to be done each time the surface is
		 * created.
		 */

		// build lists of textureid-arrays and resources
		int[][][] TEXTURE_RESOURCES = {
				{ mSnowflakeTextureIDs, SNOWFLAKE_TEXTURE_RESOURCES },
				{ mMiscTextureIDs, MISC_TEXTURE_RESOURCES }, };

		for (int i = 0; i < TEXTURE_RESOURCES.length; i++) {
			int[] TextureIDs = TEXTURE_RESOURCES[i][0];
			int[] Resources = TEXTURE_RESOURCES[i][1];

			glGenTextures(Resources.length, TextureIDs, 0);

			for (int j = 0; j < Resources.length; j++) {
				glBindTexture(GL_TEXTURE_2D, TextureIDs[j]);

				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // was
																					// GL_NEAREST
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
						GL_CLAMP_TO_EDGE);
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
						GL_CLAMP_TO_EDGE);
				glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP,
						GL_TRUE);
				
				glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

				InputStream is = mContext.getResources().openRawResource(
						Resources[j]);
				Bitmap bitmap;
				try {
					bitmap = BitmapFactory.decodeStream(is);
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						// Ignore.
					}
				}

				GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
				bitmap.recycle();
			}
		}

		// create a textured rectangle for each snowflake type
		TexturedRectangle[] texRectangles = new TexturedRectangle[mSnowflakeTextureIDs.length];
		for (int i = 0; i < mSnowflakeTextureIDs.length; i++) {
			texRectangles[i] = new TexturedRectangle(mSnowflakeTextureIDs[i]);
		}

		// create snowflakes
		for (int i = 0; i < SNOWFLAKE_COUNT; i++) {
			snowflakes.add(new Snowflake(texRectangles));
		}
	}

	public void onDrawFrame(GL10 gl) {
		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		// glDisable(GL_DITHER);

		// glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

		// http://www.gamedev.net/community/forums/topic.asp?topic_id=462270
		// glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_BLEND);
		// glEnable(GL_BLEND);
		// glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		/*
		 * Usually, the first thing one might want to do is to clear the screen.
		 * The most efficient way of doing this is to use glClear().
		 */

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		/*
		 * Now we're ready to draw some 3D objects
		 */

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		// float eyex= (float) Math.sin(SystemClock.uptimeMillis() / 1000f);

		float eyex = clamp(SteppiRuntime.orientationX / -10f, -3f, 3f);
		float eyey = clamp(SteppiRuntime.orientationY / -20f, -1f, 1f);

		GLU.gluLookAt(gl, eyex, eyey, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glActiveTexture(GL_TEXTURE0);

		for (int i = 0; i < snowflakes.size(); i++) {
			snowflakes.get(i).draw(gl);
			if (snowflakes.get(i).finished())
				snowflakes.get(i).restart();

		}
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		glViewport(0, 0, w, h);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		float size = .01f * (float) Math.tan(Math.toRadians(45.0) / 2);
		float ratio = (float) w / (float) h;
		// perspective:
		gl.glFrustumf(-size, size, -size / ratio, size / ratio, 0.01f, 100.0f);

		SteppiRuntime.horizontalOrientation = w > h;
		SteppiRuntime.screenWidth = w;
		SteppiRuntime.screenHeight = h;
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
					-0.5f, 0.5f, 0, 0.5f, 0.5f, 0, -0.5f, -0.5f, 0, 0.5f,
					-0.5f, 0 };

			for (int i = 0; i < VERTS; i++) {
				for (int j = 0; j < 3; j++) {
					mFVertexBuffer.put(coords[i * 3 + j] * 1.0f);
				}
			}

			for (int i = 0; i < VERTS; i++) {
				for (int j = 0; j < 2; j++) {
					mTexBuffer.put(coords[i * 3 + j] * 1.0f + 0.5f);
				}
			}

			for (int i = 0; i < VERTS; i++) {
				mIndexBuffer.put((short) i);
			}

			mFVertexBuffer.position(0);
			mTexBuffer.position(0);
			mIndexBuffer.position(0);

		}

		public void draw(GL10 gl) {
			glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer);
			glTexCoordPointer(2, GL_FLOAT, 0, mTexBuffer);
			glDrawElements(GL_TRIANGLE_STRIP, VERTS, GL_UNSIGNED_SHORT,
					mIndexBuffer);
		}

		private final static int VERTS = 4;

		private FloatBuffer mFVertexBuffer;
		private FloatBuffer mTexBuffer;
		private ShortBuffer mIndexBuffer;
	}
}
