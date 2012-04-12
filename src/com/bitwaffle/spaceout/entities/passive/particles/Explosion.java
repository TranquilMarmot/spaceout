package com.bitwaffle.spaceout.entities.passive.particles;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.entities.particles.Emitter;
import com.bitwaffle.spaceout.resources.Textures;

/**
 * Basically a particle that creates particles. Ha!
 * @author TranquilMarmot
 */
public class Explosion extends Entity{
	/** Emitter for particles */
	Emitter emitter;
	
	/** Time to live (how long the explosion lasts) */
	float ttl;
	
	/** How long the explosion has been alive */
	float lived;
	
	// info for particle emitter
	private Vector3f locationVariance = new Vector3f(5.0f, 5.0f, 5.0f);
	private Vector3f velocityVariance = new Vector3f(10.0f, 10.0f, 10.0f);
	private float EMIT_SPEED = 0.05f;
	private int particlesPerEmission = 20;
	private float paritcleTTLVariance = 2.5f;
	
	/**
	 * Boom!
	 * @param location Where this explosion originates
	 * @param rotation FIXME is this even necessary?
	 */
	public Explosion(Vector3f location, Quaternion rotation, float ttl, float size){
		this.location = location;
		this.rotation = rotation;
		lived = 0.0f;
		this.ttl = ttl;
		
		locationVariance = new Vector3f(size, size, size);
		velocityVariance = new Vector3f(size * 2, size * 2, size * 2);
		particlesPerEmission = (int)size * 4;
		paritcleTTLVariance = size / 2.0f;
		
		emitter = new Emitter(this,
				Textures.FIRE,
				// no offset
				new Vector3f(0.0f, 0.0f, 0.0f),
				locationVariance, 
				velocityVariance, 
				EMIT_SPEED, 
				particlesPerEmission, 
				paritcleTTLVariance);
		emitter.active = true;
	}

	@Override
	public void update(float timeStep){
		emitter.update(timeStep);
		
		lived += timeStep;
		if(lived >= ttl){
			emitter.active = false;
			
			if(!emitter.hasParticles())
				Entities.removePassiveEntity(this);
		}
	}

	@Override
	public void draw() {
		// hooray for abstraction
		emitter.draw();
	}

	@Override
	public void cleanup(){
		
	}
}
