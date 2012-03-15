package com.bitwaffle.spaceguts.entities.particles;

import org.lwjgl.util.vector.Vector3f;

public abstract class Particle{
	/** particle's location */
	public Vector3f location;
	
	/** size of the particle */
	public float width, height;
	
	/** if this is true, remove the particle on the next update */
	public boolean removeFlag;
	
	public Particle(Vector3f location, float width, float height){
		this.location = location;
		this.width = width;
		this.height = height;
	}

	public abstract void update(float timeStep);
}
