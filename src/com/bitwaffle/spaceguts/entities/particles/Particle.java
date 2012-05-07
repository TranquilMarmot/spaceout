package com.bitwaffle.spaceguts.entities.particles;

import org.lwjgl.util.vector.Vector3f;

/**
 * A particle used for an {@link Emitter}
 * @author TranquilMarmot
 */
public class Particle{
	/** particle's location */
	public Vector3f location;
	
	/** size of the particle */
	public float width, height;
	
	/** if this is true, remove the particle on the next update */
	public boolean removeFlag;
	
	/** time to live */
	public float ttl;
	
	/** how long it's been alive */
	private float lived;
	
	/** how fast the particle is moving in each direction */
	private Vector3f velocity;
	
	/**
	 * Particle constructor
	 * @param location Location for particle to start at
	 * @param width Width of particle
	 * @param height Height of particle
	 * @param ttl How long the particle will live for
	 * @param velocity How fast the particle is moving
	 */
	public Particle(Vector3f location, float width, float height, float ttl, Vector3f velocity){
		this.location = location;
		this.width = width;
		this.height = height;
		this.ttl = ttl;
		this.velocity = velocity;
	}

	/**
	 * Update the particle
	 * @param timeStep Time passed since last update
	 */
	public void update(float timeStep){
		// check to see if this particle needs to die
		lived += timeStep;
		if(lived >= ttl){
			removeFlag = true;
		}else{
			// move by velocity
			Vector3f byTimeStep = new Vector3f(velocity.x * timeStep,
											   velocity.y * timeStep,
											   velocity.z * timeStep);
			Vector3f.add(this.location, byTimeStep, this.location);
		}
	}
}
