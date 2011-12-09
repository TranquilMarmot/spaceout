package entities;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 * Any object that gives off light needs to extend this.
 * This adds a light int, which should be one of the OpenGL lights,
 * GL11.GL_LIGHT[0-7]
 * @author TranquilMarmot
 *
 */
public abstract class Light extends Entity{
	// buffers used for lighting
	protected FloatBuffer lightPosBuffer;
	protected FloatBuffer diffuseLightBuffer;
	protected FloatBuffer ambientLightBuffer;
	
	protected int light;
	/**
	 * @return Which light is being used
	 */
	public int getLight(){ return light; }
	
	/**
	 * Create a new light
	 * @param location Location of this light
	 * @param light Which OpenGL light to use (<code>GL11.GL_LIGHT[0-7]</code>)
	 * @param ambient Ambient light, should be 3 floats
	 * @param diffuse Diffuse light, should be 3 floats
	 */
	public Light(Vector3f location, int light, float[] ambient, float[] diffuse){
		super();
		this.location = location;
		this.light = light;
		
		// set up light position
		lightPosBuffer = BufferUtils.createFloatBuffer(4);
		lightPosBuffer.put(color[0]);
		lightPosBuffer.put(color[1]);
		lightPosBuffer.put(color[2]);
		lightPosBuffer.put(1.0f);
		lightPosBuffer.rewind();

		// set up diffuse lighting
		diffuseLightBuffer = BufferUtils.createFloatBuffer(4);
		diffuseLightBuffer.put(diffuse[0]);
		diffuseLightBuffer.put(diffuse[1]);
		diffuseLightBuffer.put(diffuse[2]);
		diffuseLightBuffer.put(1.0f);
		diffuseLightBuffer.rewind();

		// set up ambient lighting
		ambientLightBuffer = BufferUtils.createFloatBuffer(4);
		ambientLightBuffer.put(ambient[0]);
		ambientLightBuffer.put(ambient[1]);
		ambientLightBuffer.put(ambient[2]);
		ambientLightBuffer.put(1.0f);
		ambientLightBuffer.rewind();
		
		setUpLight();
	}
	
	/**
	 * Sets up the light with OpenGL
	 */
	private void setUpLight(){
		// we'll use light1 for the sun
		GL11.glEnable(light);

		// set up ambient and diffuse light
		GL11.glLight(light, GL11.GL_DIFFUSE, diffuseLightBuffer);
		GL11.glLight(light, GL11.GL_AMBIENT, ambientLightBuffer);
	}
}
