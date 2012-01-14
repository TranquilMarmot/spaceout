package spaceguts.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import spaceguts.entities.Entities;
import spaceguts.graphics.render.Render2D;
import spaceguts.graphics.render.Render3D;
import spaceguts.util.DisplayHelper;

/**
 * Handles all the calls for OpenGL. This includes initializing OpenGL and
 * calling {@link Render2D} and {@link Render3D}.
 * 
 * @author TranquilMarmot
 * @see Render2D
 * @see Render3D
 * 
 */
public class Graphics {

	public static void render() {
		// Clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// can only render 3D stuff if there are entities and a camera
		if (Entities.entitiesExist() && Entities.camera != null)
			Render3D.render3DScene();

		Render2D.draw2DScene();

		// handle any GL errors that might occur
		int error = GL11.glGetError();

		if (error != GL11.GL_NO_ERROR)
			System.out.println("Error rendering! Error number: " + error
					+ "/String: " + GLU.gluGetString(error));
	}

	/**
	 * Initializes OpenGL
	 */
	public static void initGL() {
		// Enable 2D textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// Enable smooth shading
		GL11.glShadeModel(GL11.GL_SMOOTH);

		// Clear to a black background
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		// Setup the depth buffer
		GL11.glClearDepth(1.0f);
		// Enable depth testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// Type of depth test to do
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		// Really nice perspective calculations
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

		// Blending
		// this line is needed for drawing text, so if blending changes and
		// suddenly text won't draw try throwing this in there
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		// Select and reset the Projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(45.0f, (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight, 0.1f,
				Render3D.drawDistance);

		// Select the Modelview matrix
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// Select 2D textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
