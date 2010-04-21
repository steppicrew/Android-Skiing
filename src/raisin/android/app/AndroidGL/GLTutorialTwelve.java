package raisin.android.app.AndroidGL;

import static android.opengl.GLES10.GL_BLEND;
import static android.opengl.GLES10.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES10.GL_SRC_ALPHA;
import static android.opengl.GLES10.glBlendFunc;
import static android.opengl.GLES10.glEnable;

import javax.microedition.khronos.opengles.GL10;

import raisin.android.R;

import android.content.Context;
import android.opengl.GLU;

public class GLTutorialTwelve extends GLTutorialBase {
	float lightAmbient[] = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
	float lightDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };
	float[] lightPos = new float[] {0,0,3,1};
	
	float matAmbient[] = new float[] { 1f, 1f, 1f, 1.0f };
	float matDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };
	
	int light_tex;
	int block_tex;
	
	public GLTutorialTwelve(Context c) {
		super(c, 20);
	}
	
	protected void init(GL10 gl) {
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glClearColor(0.0f, 0.3f, 0.3f, 0.0f);
		gl.glClearDepthf(1.0f);
		
//		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA,
				GL_ONE_MINUS_SRC_ALPHA);

//		light_tex = loadTexture(gl, R.drawable.light);
		light_tex = loadTexture(gl, R.drawable.snowflake1);
		block_tex = loadTexture(gl, R.drawable.block);
	}
	
	float xrot = 0.0f;
	float yrot = 0.0f;
	
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		setupCube(gl);
		
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
	
		gl.glRotatef(xrot, 1, 0, 0);
		gl.glRotatef(yrot, 0, 1, 0);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		gl.glActiveTexture(GL10.GL_TEXTURE0); 
		gl.glClientActiveTexture(GL10.GL_TEXTURE0); 
		gl.glEnable(GL10.GL_TEXTURE_2D); 
		gl.glBindTexture(GL10.GL_TEXTURE_2D, light_tex); 
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuff);
		
		gl.glActiveTexture(GL10.GL_TEXTURE1); 
		gl.glClientActiveTexture(GL10.GL_TEXTURE1); 
		gl.glEnable(GL10.GL_TEXTURE_2D); 
		gl.glBindTexture(GL10.GL_TEXTURE_2D, block_tex); 
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuff);
		
		gl.glTexEnvx(GL10.GL_TEXTURE_ENV , GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

	
		drawCube(gl);
		
//		gl.glActiveTexture(GL10.GL_TEXTURE0); 
//		gl.glClientActiveTexture(GL10.GL_TEXTURE0); 
		
		xrot += 1.0f;
		yrot += 0.5f;
	}
}