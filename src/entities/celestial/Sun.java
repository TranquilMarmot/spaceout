package entities.celestial;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;

import util.manager.TextureManager;
import entities.Entities;
import entities.Entity;
import entities.Light;

/**
 * A sun that gives off light.
 * @author TranquilMarmot
 * @see Entity
 * @see Light
 *
 */
public class Sun extends Entity implements Light {
	// buffers used for lighting
	FloatBuffer lightPosBuffer;
	FloatBuffer diffuseLightBuffer;
	FloatBuffer ambientLightBuffer;

	// size of this sun
	public float size;

	// this sun's sphere
	private Sphere sphere;

	/**
	 * Sun constructor
	 * @param x X location
	 * @param y Y location
	 * @param z Z location
	 * @param size Sun's size
	 * @param color Color of sun
	 * @param lightAmbient Ambient light
	 * @param lightDiffuse Diffuse light
	 */
	public Sun(float x, float y, float z, float size, float[] color,
			float[] lightAmbient, float[] lightDiffuse) {
		super();

		this.size = size;

		this.location = new Vector3f(x, y, z);
		
		this.type = "sun";

		sphere = new Sphere();
		sphere.setNormals(GLU.GLU_SMOOTH);
		sphere.setTextureFlag(false);

		this.color = color;

		// set up light position
		lightPosBuffer = BufferUtils.createFloatBuffer(4);
		lightPosBuffer.put(color[0]);
		lightPosBuffer.put(color[1]);
		lightPosBuffer.put(color[2]);
		lightPosBuffer.put(1.0f);
		lightPosBuffer.rewind();

		// set up diffuse lighting
		diffuseLightBuffer = BufferUtils.createFloatBuffer(4);
		diffuseLightBuffer.put(lightDiffuse[0]);
		diffuseLightBuffer.put(lightDiffuse[1]);
		diffuseLightBuffer.put(lightDiffuse[2]);
		diffuseLightBuffer.put(1.0f);
		diffuseLightBuffer.rewind();

		// set up ambient lighting
		ambientLightBuffer = BufferUtils.createFloatBuffer(4);
		ambientLightBuffer.put(lightAmbient[0]);
		ambientLightBuffer.put(lightAmbient[1]);
		ambientLightBuffer.put(lightAmbient[2]);
		ambientLightBuffer.put(1.0f);
		ambientLightBuffer.rewind();
		
		setUpLight();

		// create the rotation buffer
		rotationBuffer = BufferUtils.createFloatBuffer(16);
	}
	
	private void setUpLight(){
		/* BEGIN LIGHT SET-UP */
		// we'll use light1 for the sun
		GL11.glEnable(GL11.GL_LIGHT1);

		// set up ambient and diffuse light
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, diffuseLightBuffer);
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, ambientLightBuffer);
	}

	@Override
	public void draw() {
		// calculate sun's position
		float transx = Entities.camera.location.x - location.x;
		float transy = Entities.camera.location.y - location.y;
		float transz = Entities.camera.location.z - location.z;

		// create a FloatBuffer for the light's position
		lightPosBuffer.clear();
		lightPosBuffer.put(transx);
		lightPosBuffer.put(transy);
		lightPosBuffer.put(transz);
		lightPosBuffer.put(1.0f);
		lightPosBuffer.rewind();

		GL11.glPushMatrix();
		{
			// translate to the sun's location
			GL11.glTranslatef(transx, transy, transz);
			// disable lighting to draw the sun, oh the irony
			GL11.glDisable(GL11.GL_LIGHTING);
			TextureManager.getTexture(TextureManager.WHITE).bind();

			GL11.glColor3f(color[0], color[1], color[2]);
			sphere.draw(size, 30, 30);

			// put the sun's light in position
			GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosBuffer);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		GL11.glPopMatrix();
	}

	@Override
	public void update() {
	}
}
