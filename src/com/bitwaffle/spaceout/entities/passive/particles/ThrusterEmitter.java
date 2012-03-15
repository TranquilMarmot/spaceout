package com.bitwaffle.spaceout.entities.passive.particles;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.particles.Emitter;
import com.bitwaffle.spaceout.resources.Textures;

public class ThrusterEmitter extends Emitter<ThrusterParticle>{
	float emitSpeed, timeSinceEmission;
	
	public ThrusterEmitter(Vector3f location, Quaternion rotation, Textures particleTex) {
		super(location, particleTex);
		this.rotation = rotation;
	}
	
	@Override
	public void update(float timeStep){
		super.update(timeStep);
		
		timeSinceEmission += timeStep;
		if(timeSinceEmission >= emitSpeed)
			emitParticle();
	}
	
	public void emitParticle(){
		/*
		 * Create a new particle and add it
		 * Set the velocity to some initial speed,
		 * rotate it by the thruster's rotation
		 * The Thruster object will rotate the emitter
		 * whenever it is moved (which is whenever the ship is
		 * moved)
		 */
	}
}
