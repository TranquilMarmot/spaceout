package graphics.render;

import java.nio.FloatBuffer;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import util.helper.DisplayHelper;
import util.helper.QuaternionHelper;
import entities.Entities;
import entities.Entity;
import entities.Light;

/**
 * Handles all 3D rendering. For the moment, also updates Entities.entities.
 * @author TranquilMarmot
 * @see Graphics
 */
public class Render3D {
	/** how far to draw objects */
	public static float drawDistance = 5000000.0f;
	
	private static FloatBuffer cameraRotBuffer = BufferUtils.createFloatBuffer(16);
	
	/**
	 * Updates and draws everything in Entities.entities
	 */
	protected static void renderAndUpdate3DScene(){
		//prepare to do 3D rendering
		setUp3DRender();
		
		//set up lights
		setUpLighting();

		// move to the camera
		GL11.glTranslatef(Entities.camera.xOffset, Entities.camera.yOffset,
				-Entities.camera.zoom);

		// push the matrix before drawing all the entities
		GL11.glPushMatrix();
		{
			// rotate based on camera rotation
			QuaternionHelper.toFloatBuffer(Entities.camera.rotation,
					cameraRotBuffer);
			GL11.glMultMatrix(cameraRotBuffer);

			// l.draw() should set up any light that the entity owns, and then
			// draw the entity itself
			for (Light l : Entities.lights) {
				l.draw();
			}
			

			// add any Entities in the addBuffer
			if(!Entities.addBuffer.isEmpty()){
				Iterator<Entity> addIterator = Entities.addBuffer.iterator();
				while(addIterator.hasNext()){
					Entity ent = addIterator.next();
					Entities.entities.add(ent);
					addIterator.remove();
				}
			}
			
			// remove any entities from the removeBuffer
			if(!Entities.removeBuffer.isEmpty()){
				Iterator<Entity> removeIterator = Entities.removeBuffer.iterator();
				while(removeIterator.hasNext()){
					Entity ent = removeIterator.next();
					Entities.entities.remove(ent);
					removeIterator.remove();
				}
			}
			
			/* BEGIN ENTITY DRAWING */
			Iterator<Entity> entityIterator = Entities.entities.iterator();
			while(entityIterator.hasNext()) {
				Entity ent = entityIterator.next();
				// update the entity if we're not paused
				ent.update();
				float transx = Entities.camera.location.x - ent.location.x;
				float transy = Entities.camera.location.y - ent.location.y;
				float transz = Entities.camera.location.z - ent.location.z;

				GL11.glPushMatrix();
				{
					// translate to the entity's location
					GL11.glTranslatef(transx, transy, transz);
					ent.draw();
				}
				GL11.glPopMatrix();
			}
			
			//draw the player (since it's not contained in Entities.entities)
			float transx = Entities.camera.location.x - Entities.player.location.x;
			float transy = Entities.camera.location.y - Entities.player.location.y;
			float transz = Entities.camera.location.z - Entities.player.location.z;
			GL11.glTranslatef(transx, transy, transz);
			Entities.player.draw();
			/* END ENTITY DRAWING */
		}
		GL11.glPopMatrix();
	}
	
	/**
	 * Sets up any lighting for the scene
	 */
	private static void setUpLighting(){
		// enable lighting and select a lighting model
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER, GL11.GL_TRUE);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
	}
	
	/**
	 * Prepares OpenGL matrices for 3D drawing
	 */
	private static void setUp3DRender(){
		// select and reset the projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		// calculate the current aspect ratio
		float aspect = (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight;
		GLU.gluPerspective(45.0f, aspect, 1.0f, drawDistance);

		// we're done setting up the Projection matrix, on to the Modelview
		// matrix
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// disable blending for now
		GL11.glDisable(GL11.GL_BLEND);
	}
}
