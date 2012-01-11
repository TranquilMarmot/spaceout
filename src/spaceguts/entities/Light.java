package spaceguts.entities;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;


/**
 * Any object that gives off light needs to extend this. This adds a light int,
 * which should be one of the OpenGL lights, GL11.GL_LIGHT[0-7]
 * 
 * @author TranquilMarmot
 * 
 */
public abstract class Light extends Entity {
	/** buffers used to pass value to OpenGL */
	protected FloatBuffer lightPosBuffer, colorBuffer;

	/** the light to use */
	public int light;

	/**
	 * Create a new light
	 * 
	 * @param location
	 *            Location of this light
	 * @param light
	 *            Which OpenGL light to use (<code>GL11.GL_LIGHT[0-7]</code>)
	 * @param ambient
	 *            Ambient light, should be 3 floats
	 * @param diffuse
	 *            Diffuse light, should be 3 floats
	 */
	public Light(Vector3f location, int light, float[] ambient, float[] diffuse) {
		super();
		this.location = location;
		this.light = light;

		initLight(location, ambient, diffuse);
	}

	/**
	 * Sets up the light with OpenGL
	 */
	private void initLight(Vector3f location, float[] ambient, float[] diffuse) {
		// enable the relevant light
		GL11.glEnable(light);

		// create the color buffer
		colorBuffer = BufferUtils.createFloatBuffer(4);

		// set up diffuse light
		colorBuffer.put(diffuse[0]);
		colorBuffer.put(diffuse[1]);
		colorBuffer.put(diffuse[2]);
		colorBuffer.put(1.0f);
		colorBuffer.rewind();
		GL11.glLight(light, GL11.GL_DIFFUSE, colorBuffer);

		// clear the buffer and set up the ambient light
		colorBuffer.clear();
		colorBuffer.put(ambient[0]);
		colorBuffer.put(ambient[1]);
		colorBuffer.put(ambient[2]);
		colorBuffer.put(1.0f);
		colorBuffer.rewind();
		GL11.glLight(light, GL11.GL_AMBIENT, colorBuffer);

		// set up light position
		lightPosBuffer = BufferUtils.createFloatBuffer(4);
		lightPosBuffer.put(location.x);
		lightPosBuffer.put(location.y);
		lightPosBuffer.put(location.z);
		lightPosBuffer.put(1.0f);
		lightPosBuffer.rewind();
		GL11.glLight(light, GL11.GL_POSITION, lightPosBuffer);
	}

	/**
	 * This should be called every frame for every light to set up the lighting
	 * for the scene
	 */
	public void setUpLight() {
		// calculate light's position
		float transx = Entities.camera.location.x - location.x;
		float transy = Entities.camera.location.y - location.y;
		float transz = Entities.camera.location.z - location.z;

		// set the light's position
		lightPosBuffer.clear();
		lightPosBuffer.put(transx);
		lightPosBuffer.put(transy);
		lightPosBuffer.put(transz);
		lightPosBuffer.put(1.0f);
		lightPosBuffer.rewind();
		GL11.glLight(light, GL11.GL_POSITION, lightPosBuffer);
	}

	@Override
	public void cleanup() {
		GL11.glDisable(light);
	}
}
