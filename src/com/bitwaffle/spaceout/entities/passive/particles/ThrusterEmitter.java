package com.bitwaffle.spaceout.entities.passive.particles;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.particles.Emitter;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.resources.Textures;

public class ThrusterEmitter extends Emitter<ThrusterParticle>{
	float emitSpeed, timeSinceEmission;
	public boolean active;
	
	public ThrusterEmitter(Vector3f location, Quaternion rotation, Textures particleTex, float emitSpeed) {
		super(location, particleTex);
		this.rotation = rotation;
		this.emitSpeed = emitSpeed;
		timeSinceEmission = 0.0f;
	}
	
	@Override
	public void update(float timeStep){
		super.update(timeStep);
		if(active){
			timeSinceEmission += timeStep;
			if(timeSinceEmission >= emitSpeed){
				emitParticle();
				timeSinceEmission = 0.0f;
			}
		}
	}
	
	public void emitParticle(){
		Vector3f loc = new Vector3f(this.location);
		
		// TODO have this be sort of random
		Vector3f veloc = new Vector3f(0.0f, 0.0f, -100.0f);
		
		Vector3f rotVeloc = QuaternionHelper.rotateVectorByQuaternion(veloc, this.rotation);
		
		this.addParticle(new ThrusterParticle(loc, 1.0f, 1.0f, 0.3f, rotVeloc));
	}
}