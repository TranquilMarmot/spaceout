package entities.celestial;

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
public class Sun extends Light {
	/** size of this sun */
	public float size;

	/** this sun's sphere */
	private Sphere sphere;

	/**
	 * Sun constructor
	 * @param location Location of this sun
	 * @param size Sun's size
	 * @param color Color of sun
	 * @param lightAmbient Ambient light
	 * @param lightDiffuse Diffuse light
	 */
	public Sun(Vector3f location, float size, int light, float[] color,
			float[] lightAmbient, float[] lightDiffuse) {
		super(location, light, lightAmbient, lightDiffuse);

		this.size = size;
		
		this.type = "sun";

		sphere = new Sphere();
		sphere.setNormals(GLU.GLU_SMOOTH);
		sphere.setTextureFlag(false);

		this.color = color;
		
		this.light = light;

		// create the rotation buffer
		rotationBuffer = BufferUtils.createFloatBuffer(16);
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
			GL11.glLight(light, GL11.GL_POSITION, lightPosBuffer);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		GL11.glPopMatrix();
	}

	@Override
	public void update() {
	}
}
