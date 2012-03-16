package com.bitwaffle.spaceout.entities.passive.particles;

import java.util.Random;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.particles.Emitter;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.resources.Textures;

public class ThrusterEmitter extends Emitter<ThrusterParticle>{
	private float emitSpeed, timeSinceEmission;
	private int particlesPerEmission;
	private float xOffset, yOffset, zOffset;
	public boolean active;
	private Random randy;
	
	public ThrusterEmitter(Vector3f location, Quaternion rotation, Textures particleTex, float emitSpeed, int particlesPerEmission, float xOffset, float yOffset, float zOffset) {
		super(location, particleTex);
		this.rotation = rotation;
		this.emitSpeed = emitSpeed;
		this.particlesPerEmission = particlesPerEmission;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		timeSinceEmission = 0.0f;
		randy = new Random();
		randy.setSeed(System.nanoTime());
	}
	
	@Override
	public void update(float timeStep){
		super.update(timeStep);
		if(active){
			timeSinceEmission += timeStep;
			if(timeSinceEmission >= emitSpeed){
				for(int i = 0; i < particlesPerEmission; i++)
					emitParticle();
				timeSinceEmission = 0.0f;
			}
		}
	}
	
	public void emitParticle(){
		float randXOffset, randYOffset, randZOffset;
		
		if(randy.nextBoolean()) 
			randXOffset = randy.nextFloat() * 0.15f;
		else
			randXOffset = randy.nextFloat() * -0.15f;
		
		if(randy.nextBoolean()) 
			randYOffset = randy.nextFloat() * 0.15f;
		else
			randYOffset = randy.nextFloat() * -0.15f;
		
		if(randy.nextBoolean()) 
			randZOffset = randy.nextFloat() * 0.15f;
		else
			randZOffset = randy.nextFloat() * -0.15f;
		
		Vector3f loc = new Vector3f(this.location.x + randXOffset, this.location.y + randYOffset, this.location.z + randZOffset);
		
		Vector3f behind = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(xOffset, yOffset, zOffset), this.rotation);
		
		loc.set(loc.x + behind.x, loc.y + behind.y, loc.z + behind.z);
		
		Vector3f veloc = new Vector3f(0.0f, 0.0f, -randy.nextFloat() * 2.5f);
		
		Vector3f rotVeloc = QuaternionHelper.rotateVectorByQuaternion(veloc, this.rotation);
		
		this.addParticle(new ThrusterParticle(loc, 0.4f, 0.4f, randy.nextFloat() * 0.5f, rotVeloc));
	}
}