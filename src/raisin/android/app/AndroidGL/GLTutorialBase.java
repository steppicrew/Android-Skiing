package raisin.android.app.AndroidGL;

import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES10.glBindTexture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;


public abstract class GLTutorialBase extends GLSurfaceView implements GLSurfaceView.Renderer {
	protected Context context;
	int width;
	int height;
	int fps;
	
	float box[] = new float[] {
			// FRONT
			 0.5f, -0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			// BACK
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// LEFT
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			// RIGHT
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			// TOP
			-0.5f,  0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			 -0.5f,  0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// BOTTOM
			-0.5f, -0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f, -0.5f, -0.5f,
		};
	
	float texCoords[] = new float[] {
			// FRONT
			 0.0f, 1.0f,
			 0.0f, 0.0f,
			 1.0f, 1.0f,
			 1.0f, 0.0f,
			// BACK
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// LEFT
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// RIGHT
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// TOP
			 0.0f, 0.0f,
			 1.0f, 0.0f,
			 0.0f, 1.0f,
			 1.0f, 1.0f,
			// BOTTOM
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f
		};

	/** The initial indices definition */
	byte indices[] = {
						// Faces definition
						0, 1, 3, 0, 3, 2, 		// Face front
						4, 5, 7, 4, 7, 6, 		// Face right
						8, 9, 11, 8, 11, 10, 	// ...
						12, 13, 15, 12, 15, 14, 
						16, 17, 19, 16, 19, 18, 
						20, 21, 23, 20, 23, 22, 
												};

	FloatBuffer cubeBuff;
	FloatBuffer texBuff;
	
	/**
	 * Make a direct NIO FloatBuffer from an array of floats
	 * @param arr The array
	 * @return The newly created FloatBuffer
	 */
	protected static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	/**
	 * Make a direct NIO IntBuffer from an array of ints
	 * @param arr The array
	 * @return The newly created IntBuffer
	 */
	protected static IntBuffer makeFloatBuffer(int[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();
		ib.put(arr);
		ib.position(0);
		return ib;
	}
	
	protected static ByteBuffer makeByteBuffer(Bitmap bmp) {
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight()*bmp.getWidth()*4);
		bb.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = bb.asIntBuffer();

		for (int y = 0; y < bmp.getHeight(); y++)
			for (int x=0;x<bmp.getWidth();x++) {
				int pix = bmp.getPixel(x, bmp.getHeight()-y-1);
				// Convert ARGB -> RGBA
				byte alpha = (byte)((pix >> 24)&0xFF);
				byte red = (byte)((pix >> 16)&0xFF);
				byte green = (byte)((pix >> 8)&0xFF);
				byte blue = (byte)((pix)&0xFF);
								
				ib.put(((red&0xFF) << 24) | 
					   ((green&0xFF) << 16) |
					   ((blue&0xFF) << 8) |
					   ((alpha&0xFF)));
			}
		ib.position(0);
		bb.position(0);
		return bb;
	}
	
	protected int loadTexture(GL10 gl, int resource) {
		InputStream is = context.getResources().openRawResource(
				resource);
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
//		return loadTexture(gl, BitmapFactory.decodeResource(context.getResources(), resource));
		return loadTexture(gl, bitmap);
	}
	
	/**
	 * Create a texture and send it to the graphics system
	 * @param gl The GL object
	 * @param bmp The bitmap of the texture
	 * @param reverseRGB Should the RGB values be reversed?  (necessary workaround for loading .pngs...)
	 * @return The newly created identifier for the texture.
	 */
	protected static int loadTexture(GL10 gl, Bitmap bmp) {
		int[] tmp_tex = new int[1];

		gl.glGenTextures(1, tmp_tex, 0);
		int tex = tmp_tex[0];

		int type= GL_TEXTURE_2D;
		int width= bmp.getWidth();
		int height= bmp.getHeight();
		
//		loadTexture(tex, GL10.GL_TEXTURE_2D, bmp, gl);
		gl.glBindTexture(type, tex);
		gl.glTexImage2D(type, 0, GL10.GL_RGBA, width, height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
//		gl.glTexSubImage2D(type, 0, 0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
		gl.glTexParameterf(type, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(type, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0);
		return tex;
	}
	
	public void loadTexture(int texture, int type,  int resource, GL10 gl) {
		loadTexture(texture, type, BitmapFactory.decodeResource(context.getResources(), resource), gl);
	}
	
	static public void loadTexture(int texture, int type, Bitmap bmp, GL10 gl) {
		loadTexture(texture, type, bmp.getWidth(), bmp.getHeight(), makeByteBuffer(bmp), gl);
	}
	
	static public void loadTexture(int texture, int type, int width, int height, ByteBuffer bb, GL10 gl) {
		gl.glBindTexture(type, texture);
		gl.glTexImage2D(type, 0, GL10.GL_RGBA, width, height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
		gl.glTexSubImage2D(type, 0, 0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
		gl.glTexParameterf(type, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(type, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	}

	/**
	 * Constructor
	 */
	public GLTutorialBase(Context context) {
		this(context, -1);
	}

	/**
	 * Constructor for animated views
	 * @param c The View's context
	 * @param fps The frames per second for the animation.
	 */
	public GLTutorialBase(Context context, int fps) {
		super(context);
		this.context = context;
		this.fps = fps;
		
		cubeBuff = makeFloatBuffer(box);
		texBuff = makeFloatBuffer(texCoords);
	}

	public void setupCube(GL10 gl) {
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuff);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	public void drawCube(GL10 gl) {
		drawCube(gl, 0, 0);
	}

	public void drawCube(GL10 gl, double xrot, double yrot) {
		if (false) {
			ByteBuffer indexBuffer = ByteBuffer.allocateDirect(indices.length);
			indexBuffer.put(indices);
			indexBuffer.position(0);
	
			
			//Draw the vertices as triangles, based on the Index Buffer information
			gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		}
		else {
			class Center implements Comparable<Center> {
				int index;
				double coords[]= new double[3];
				public Center(int index) {
					this.index= index;
				}

				@Override
				public int compareTo(Center another) {
					if (coords[2] < another.coords[2]) return -1;
					if (coords[2] > another.coords[2]) return  1;
					return 0;
				}
			}
			Center centers[]= new Center[6];

			int coords_per_side= box.length / 6 / 3;
			for (int i= 0; i < 6; i++) {
				centers[i]= new Center(i);
				for (int j= 0; j < coords_per_side; j++) {
					for (int k= 0; k < 3; k++) {
						centers[i].coords[k]+= box[(i * coords_per_side + j) * 3 + k] / coords_per_side;
					}
				}
			}

			double sin_xrot= Math.sin(Math.toRadians(xrot));
			double cos_xrot= Math.cos(Math.toRadians(xrot));
			double sin_yrot= Math.sin(Math.toRadians(yrot));
			double cos_yrot= Math.cos(Math.toRadians(yrot));
			
			for (int i= 0; i < 6; i++) {
				final int x= 0;
				final int y= 1;
				final int z= 2;
				centers[i].coords[y]= centers[i].coords[y] * cos_xrot + centers[i].coords[z] * sin_xrot;
				centers[i].coords[z]= centers[i].coords[z] * cos_xrot - centers[i].coords[y] * sin_xrot;
				centers[i].coords[x]= centers[i].coords[x] * cos_yrot + centers[i].coords[z] * sin_yrot;
				centers[i].coords[z]= centers[i].coords[z] * cos_yrot - centers[i].coords[x] * sin_yrot;
			}
			
			int len= indices.length / 6;
			ByteBuffer indexBuffer = ByteBuffer.allocateDirect(indices.length);
			indexBuffer.put(indices);
			
			Arrays.sort(centers);
			
			centers[0].coords[0]= 1;
			for (int i= 0; i < 6; i++) {
				indexBuffer.position(centers[i].index * len);
				
				//Draw the vertices as triangles, based on the Index Buffer information
				gl.glDrawElements(GL10.GL_TRIANGLES, len, GL10.GL_UNSIGNED_BYTE, indexBuffer);
			}
		}
		if (false) {
			gl.glColor4f(1.0f, 1, 1, 1.0f);
			gl.glNormal3f(0,0,1);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			gl.glNormal3f(0,0,-1);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		
			gl.glColor4f(1, 1.0f, 1, 1.0f);
			gl.glNormal3f(-1,0,0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
			gl.glNormal3f(1,0,0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
			
			gl.glColor4f(1, 1, 1.0f, 1.0f);
			gl.glNormal3f(0,1,0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
			gl.glNormal3f(0,-1,0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
		}
	}
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {    
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
    	this.width = w;
    	this.height = h;
    	gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,w,h);
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 1f, 100f);
		init(gl);
	}
	
	protected void init(GL10 gl) {}	
}