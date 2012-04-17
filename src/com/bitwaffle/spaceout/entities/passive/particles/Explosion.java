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
	final static Vector3f LOCATION_VARIANCE = new Vector3f(5.0f, 5.0f, 5.0f);
	final static Vector3f VELOCITY_VARIANCE = new Vector3f(10.0f, 10.0f, 10.0f);
	final static float EMIT_SPEED = 0.05f;
	final static int PARTICLES_PER_EMISSION = 20;
	final static float PARTICLE_TTL_VARIANCE = 2.5f;
	
	/**
	 * Boom!
	 * @param location Where this explosion originates
	 * @param rotation FIXME is this even necessary?
	 */
	public Explosion(Vector3f location, Quaternion rotation, float ttl){
		this.location = location;
		this.rotation = rotation;
		lived = 0.0f;
		this.ttl = ttl;
		
		emitter = new Emitter(this,
				Textures.FIRE,
				// no offset
				new Vector3f(0.0f, 0.0f, 0.0f),
				LOCATION_VARIANCE, 
				VELOCITY_VARIANCE, 
				EMIT_SPEED, 
				PARTICLES_PER_EMISSION, 
				PARTICLE_TTL_VARIANCE);
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
