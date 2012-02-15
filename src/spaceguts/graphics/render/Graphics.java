package spaceguts.graphics.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import spaceguts.entities.Entities;

/**
 * This class contains methods for rendering the whole scene
 * and for initializing OpenGL
 * 
 * @author TranquilMarmot
 */
public class Graphics {
	/**
	 * Renders the entire scene
	 */
	public static void render(){
		// clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		// only render 3D stuff if 3D stuff exists
		if (Entities.entitiesExist() && Entities.camera != null)
			Render3D.render3DScene();
		
		// draw the 2D scene
		Render2D.draw2DScene();
		
		// handle any GL errors that might occur
		int error;
		while ((error = GL11.glGetError()) != GL11.GL_NO_ERROR)
			System.out.println("Error rendering! Error number: " + error
					+ "/String: " + GLU.gluGetString(error));
	}
	
	/**
	 * Initializes OpenGL
	 */
	public static void initGL(){
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearDepth(1.0f);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		Render3D.init();
	}
}
