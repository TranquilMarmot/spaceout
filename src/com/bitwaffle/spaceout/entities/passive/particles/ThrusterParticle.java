package com.bitwaffle.spaceout.entities.passive.particles;

import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.particles.Particle;

public class ThrusterParticle extends Particle{
	/** time to live */
	public float ttl;
	
	/** how long it's been alive */
	private float lived;
	
	/** how fast the particle is moving in each direction */
	private Vector3f velocity;
	
	//TODO implement fading
	
	public ThrusterParticle(Vector3f location, float width, float height, Vector3f velocity) {
		super(location, width, height);
		this.velocity = velocity;
	}
	
	@Override
	public void update(float timeStep){
		lived += timeStep;
		if(lived >= ttl)
			removeFlag = true;
		else{
			Vector3f byTimeStep = new Vector3f(velocity.x * timeStep,
											   velocity.y * timeStep,
											   velocity.z * timeStep);
			Vector3f.add(this.location, byTimeStep, this.location);
		}
	}
}
