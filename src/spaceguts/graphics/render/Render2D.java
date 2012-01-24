package spaceguts.graphics.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import spaceguts.graphics.Graphics;
import spaceguts.graphics.gui.GUI;
import spaceguts.util.DisplayHelper;

/**
 * Handles all 2D rendering
 * @author TranquilMarmot
 * @see Graphics 
 *
 */
public class Render2D {	
	/**
	 * Draws the 2D scene
	 */
	public static void draw2DScene(){
		setUp2DRender();
		GUI.draw();
	}
	
	/**
	 * Sets up OpenGL for 2D drawing
	 */
	private static void setUp2DRender(){
		GL20.glUseProgram(0);
		// reset the projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		// set up an ortho view with a near of -1 and a far of 1 (so everything
		// at 0 is drawn)
		GL11.glOrtho(0, DisplayHelper.windowWidth, DisplayHelper.windowHeight,
				0, -1, 1);

		// reset the modelview matrix
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// enable blending and disable lighting
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
}
