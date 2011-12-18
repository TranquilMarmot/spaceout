package graphics.render;

import java.nio.FloatBuffer;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Quaternion;

import physics.Physics;
import util.helper.DisplayHelper;
import util.helper.QuaternionHelper;
import entities.Entities;
import entities.Entity;
import entities.Light;
import entities.dynamic.DynamicEntity;
import entities.dynamic.LaserBullet;

/**
 * Handles all 3D rendering. For the moment, also updates Entities.entities.
 * 
 * @author TranquilMarmot
 * @see Graphics
 */
public class Render3D {
	/** how far to draw objects */
	public static float drawDistance = 3000000.0f;

	private static FloatBuffer cameraRotBuffer = BufferUtils
			.createFloatBuffer(16);

	/**
	 * Updates and draws everything in Entities.entities
	 */
	protected static void renderAndUpdate3DScene() {
		// add or remove any entities
		Entities.checkBuffers();
		
		// update the camera
		Entities.camera.update();
		
		// update the skybox
		Entities.skybox.update();

		// prepare to do 3D rendering
		setUp3DRender();

		// set up lights
		setUpLighting();
		
		transformToCamera();
		
		// draw the skybox first
		Entities.skybox.draw();
		
		// set up lights before we render our scene
		for (Light l : Entities.lights) {
			l.setUpLight();
		}

		// draw any lights
		drawLights();
		
		//draw all dynamic entities
		drawDynamicEntities();
		
		// draw all entities
		drawStaticEntities();
		
		// draw the player
		drawPlayer();
	}
	
	/**
	 * Transforms the ModelView matrix to the current camera position and rotation
	 */
	private static void transformToCamera(){
		// handle any camera offset
		GL11.glTranslatef(Entities.camera.xOffset, Entities.camera.yOffset,
				-Entities.camera.zoom);

		// rotate based on camera rotation
		QuaternionHelper.toFloatBuffer(Entities.camera.rotation,
				cameraRotBuffer);
		GL11.glMultMatrix(cameraRotBuffer);
	}
	
	/**
	 * Draws all entities
	 */
	private static void drawDynamicEntities(){
		Iterator<DynamicEntity> entityIterator = Entities.dynamicEntities.iterator();
		while (entityIterator.hasNext()) {
			Entity ent = entityIterator.next();
			
			// figure out where to translate to
			float transX = Entities.camera.location.x - ent.location.x;
			float transY = Entities.camera.location.y - ent.location.y;
			float transZ = Entities.camera.location.z - ent.location.z;

			GL11.glPushMatrix();{
				GL11.glTranslatef(transX, transY, transZ);
				
				if(Physics.drawDebug && (ent.getClass().equals(DynamicEntity.class) || ent.getClass().equals(LaserBullet.class))){
					((DynamicEntity) ent).drawPhysicsDebug();
				}
				
				Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
				Quaternion.negate(ent.rotation, reverse);
				QuaternionHelper.toFloatBuffer(reverse, cameraRotBuffer);
				GL11.glMultMatrix(cameraRotBuffer);
				
				ent.draw();
			}GL11.glPopMatrix();
		}
	}
	
	/**
	 * Draws all entities
	 */
	private static void drawStaticEntities(){
		Iterator<Entity> entityIterator = Entities.staticEntities.iterator();
		while (entityIterator.hasNext()) {
			Entity ent = entityIterator.next();

			// update the entity. This grabs it's most recent position and
			// rotation.
			ent.update();
			
			// figure out where to translate to
			float transX = Entities.camera.location.x - ent.location.x;
			float transY = Entities.camera.location.y - ent.location.y;
			float transZ = Entities.camera.location.z - ent.location.z;

			GL11.glPushMatrix();{
				GL11.glTranslatef(transX, transY, transZ);
				
				if(Physics.drawDebug && (ent.getClass().equals(DynamicEntity.class) || ent.getClass().equals(LaserBullet.class))){
					((DynamicEntity) ent).drawPhysicsDebug();
				}
				
				Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
				Quaternion.negate(ent.rotation, reverse);
				QuaternionHelper.toFloatBuffer(reverse, cameraRotBuffer);
				GL11.glMultMatrix(cameraRotBuffer);
				
				ent.draw();
			}GL11.glPopMatrix();
		}
	}
	
	/**
	 * Draws all lights
	 */
	private static void drawLights(){
		Iterator<Light> entityIterator = Entities.lights.iterator();
		while (entityIterator.hasNext()) {
			Light ent = entityIterator.next();
			
			// figure out where to translate to
			float transX = Entities.camera.location.x - ent.location.x;
			float transY = Entities.camera.location.y - ent.location.y;
			float transZ = Entities.camera.location.z - ent.location.z;

			GL11.glPushMatrix();{
				GL11.glTranslatef(transX, transY, transZ);
				
				ent.draw();
			}GL11.glPopMatrix();
		}
	}
	
	/**
	 * Draws the player
	 */
	private static void drawPlayer(){
		float transX = Entities.camera.location.x - Entities.player.location.x;
		float transY = Entities.camera.location.y - Entities.player.location.y;
		float transZ = Entities.camera.location.z - Entities.player.location.z;

		GL11.glPushMatrix();{
			GL11.glTranslatef(transX, transY, transZ);
			
			Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
			Quaternion.negate(Entities.player.rotation, reverse);
			QuaternionHelper.toFloatBuffer(reverse, cameraRotBuffer);
			GL11.glMultMatrix(cameraRotBuffer);
			
			Entities.player.draw();
		}GL11.glPopMatrix();
	}

	/**
	 * Sets up any lighting for the scene
	 */
	private static void setUpLighting() {
		// enable lighting and select a lighting model
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER, GL11.GL_TRUE);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
		GL11.glShadeModel(GL11.GL_SMOOTH);
	}

	/**
	 * Prepares OpenGL matrices for 3D drawing
	 */
	private static void setUp3DRender() {
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
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
