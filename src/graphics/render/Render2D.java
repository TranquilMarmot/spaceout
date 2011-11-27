package graphics.render;

import org.lwjgl.opengl.GL11;

import util.Runner;
import util.debug.Debug;
import util.helper.DisplayHelper;

/**
 * Handles all 2D rendering
 * @author TranquilMarmot
 * @see Graphics 
 *
 */
public class Render2D {
	/**
	 * Updates any 2D things and renders them
	 */
	protected static void updateAndRender2DScene(){
		setUp2DRender();

		// draw debug info
		Debug.updateAndDraw();

		// draw 'PAUSED' in the middle of the screen if the game is paused
		if (Runner.paused)
			Debug.font.drawString((DisplayHelper.windowWidth / 2) - 25,
					DisplayHelper.windowHeight / 2, "PAUSED");
	}
	
	/**
	 * Sets up OpenGL matrices for 2D drawing
	 */
	private static void setUp2DRender(){
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
	}
}
