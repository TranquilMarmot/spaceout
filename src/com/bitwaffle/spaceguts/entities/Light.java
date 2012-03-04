package com.bitwaffle.spaceguts.entities;

import org.lwjgl.util.vector.Vector3f;


/**
 * Any object that gives off light needs to extend this. This adds a light int,
 * which should be one of the OpenGL lights, GL11.GL_LIGHT[0-7]
 * 
 * @author TranquilMarmot
 * 
 */
public abstract class Light extends Entity {
	public Vector3f intensity;

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
	public Light(Vector3f location, Vector3f intensity) {
		super();
		this.location = location;
		this.intensity = intensity;
	}

	@Override
	public void cleanup() {
		
	}
}
