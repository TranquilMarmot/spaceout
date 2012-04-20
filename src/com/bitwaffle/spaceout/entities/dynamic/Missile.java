package com.bitwaffle.spaceout.entities.dynamic;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.audio.SoundSource;
import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.entities.particles.Emitter;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.physics.ConvexResultCallback;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.entities.passive.particles.Explosion;
import com.bitwaffle.spaceout.interfaces.Projectile;
import com.bitwaffle.spaceout.resources.Models;
import com.bitwaffle.spaceout.resources.Sounds;
import com.bitwaffle.spaceout.resources.Textures;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * A homing missile
 * 
 * @author TranquilMarmot
 */
public class Missile extends DynamicEntity implements Projectile{
	/** How much damage a missile does when it hits something */
	private static final int DAMAGE = 50;
	
	/**  Missile's current speed */
	private float speed;
	
	/** How much faster the missile goes each update */
	private float speedIncrease;
	/** How long the missile has to live */
	private float detonationTime, timeLived;
	
	// info for particles
	private static final Vector3f FIRE_OFFSET = new Vector3f(0.0f, 0.0f, -2.0f);
	private static final Vector3f FIRE_LOC_VARIANCE = new Vector3f(1.0f, 1.0f, 1.0f);
	private static final Vector3f FIRE_VELOC_VARIANCE = new Vector3f(2.0f, 2.0f, 5.0f);
	private static final float FIRE_EMIT_SPEED = 0.05f;
	private static final int FIRE_PARTICLES_PER_EMISSION = 2;
	private static final float FIRE_PARTICLE_TTL_VARIANCE = 2.0f;
	
	private static Vector3f explosionDistance = new Vector3f(0.0f, 0.0f, 1.0f);
	private static float explosionSize = 100.0f;
	private static float explosionForce = 10.0f;
	
	/** What the missile is aiming for */
	private DynamicEntity target;
	
	/** Particle effect */
	private Emitter fire;
	
	/** The sound the missile makes */
	private SoundSource thrusterSound;

	/**
	 * Create a new missile
	 * @param location Where to put missile
	 * @param rotation Which way the missile should be facing
	 * @param target What the missile should try and hit (null if nothing)
	 * @param initialSpeed How fast the missile will be going when it's created
	 * @param speedIncrease How much faster the missile goes each update
	 * @param timeToDetonation How long the missile has to live
	 */
	public Missile(Vector3f location, Quaternion rotation, DynamicEntity target, float initialSpeed, float speedIncrease, float timeToDetonation) {
		// TODO missiles should probably be represented by cylinders instead of a convex hull
		super(location, rotation, Models.MISSILE, 100.0f, 1.0f, CollisionTypes.PROJECTILE,
				CollisionTypes.EVERYTHING);
		
		this.target = target;
		this.speed = initialSpeed;
		this.speedIncrease = speedIncrease;
		this.detonationTime = timeToDetonation;
		this.timeLived = 0.0f;
		
		fire = new Emitter(this, Textures.FIRE, FIRE_OFFSET, FIRE_LOC_VARIANCE, FIRE_VELOC_VARIANCE, FIRE_EMIT_SPEED, FIRE_PARTICLES_PER_EMISSION, FIRE_PARTICLE_TTL_VARIANCE);
		
		// create looping sound and play it
		Vector3f veloc = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, initialSpeed), rotation);
		thrusterSound = new SoundSource(Sounds.THRUSTER, true, this.location, veloc);
		thrusterSound.playSound();
	}
	
	/**
	 * Ker-plow boooom
	 */
	public void explode(){
		// particle effect for explosion
		Explosion splode = new Explosion(this.location, 1.0f, 5.0f);
		Entities.addPassiveEntity(splode);
		
		// stop and remove sound
		thrusterSound.stopSound();
		thrusterSound.removeFlag = true;
		
		// perform a convex sweep test to push thing outwards
		ArrayList<DynamicEntity> hits = new ArrayList<DynamicEntity>();
		ConvexResultCallback<DynamicEntity> callback = new ConvexResultCallback<DynamicEntity>(hits, CollisionTypes.EVERYTHING);
		Physics.convexSweepTest(this, explosionDistance, new SphereShape(explosionSize), callback);
		for(DynamicEntity ent : hits){
			if(ent != this){
				Vector3f subtract = new Vector3f();
				Vector3f.sub(this.location, ent.location, subtract);
				subtract.negate(subtract);
				subtract.normalise(subtract);
				
				javax.vecmath.Vector3f entSpeed = new javax.vecmath.Vector3f();
				ent.rigidBody.getLinearVelocity(entSpeed);
				
				float dx = entSpeed.x + (subtract.x * explosionForce);
				float dy = entSpeed.y + (subtract.y * explosionForce);
				float dz = entSpeed.z + (subtract.z * explosionForce);
			
				ent.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(dx, dy, dz));
			}
		}
		// get rid of ze missile
		removeFlag = true;
	}
	
	@Override
	public void update(float timeStep){
		/*
		 * If there's no target, the missile is given a random spin,
		 * so that it look's like it's going haywire.
		 */
		if(target == null){
			javax.vecmath.Vector3f angvec = new javax.vecmath.Vector3f();
			this.rigidBody.getAngularVelocity(angvec);
			if(angvec.length() == 0.0f){
				Random r = new Random();
				float x,y,z;
				if(r.nextBoolean())
					x = r.nextFloat() * speed;
				else
					x = r.nextFloat() * -speed;
				
				if(r.nextBoolean())
					y = r.nextFloat() * speed;
				else
					y = r.nextFloat() * -speed;
				
				if(r.nextBoolean())
					z = r.nextFloat() * speed;
				else
					z = r.nextFloat() * -speed;
				
				angvec.set(x,y,z);
				angvec.normalize(angvec);
				this.rigidBody.setAngularVelocity(angvec);
			}
			// give the missile some forward momentum
			Vector3f forward = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, speed), this.rotation);
			this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(forward.x, forward.y, forward.z));
			
		/*
		 * Else there is a target, and we should move towards it
		 */
		} else{
			// make sure the target still exists
			if(target.removeFlag){
				target = null;
			}else{
				// find the difference between this's location and the target's location then negate it to go towards it
				Vector3f subtract = new Vector3f();
				Vector3f.sub(this.location, target.location, subtract);
				subtract.negate(subtract);
				// since we normalize the difference, the length of the missile's linear velocity will be its speed!
				subtract.normalise(subtract);
				
				float dx1 = (subtract.x * speed);
				float dy1 = (subtract.y * speed);
				float dz1 = (subtract.z * speed);
				
				// set linear velocity to go towards following
				this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(dx1, dy1, dz1));
				
				if(dx1 != 0.0f || dy1 != 0.0f || dz1 != 0.0f){
					//FIXME this doesn't work quite right...
					Quaternion newRot = QuaternionHelper.quaternionBetweenVectors(this.location, subtract);
					this.rotation.set(newRot);
					
					Transform trans = new Transform();
					this.rigidBody.getWorldTransform(trans);
					trans.setRotation(new Quat4f(newRot.x, newRot.y, newRot.z, newRot.w));
					this.rigidBody.setWorldTransform(trans);	
				}
			}
		}
		
		// increase the missile's speed
		speed += speedIncrease * timeStep;
		
		// set sound location and velocity
		javax.vecmath.Vector3f veloc = new javax.vecmath.Vector3f();
		this.rigidBody.getLinearVelocity(veloc);
		thrusterSound.setVelocity(new Vector3f(veloc.x, veloc.y, veloc.z));
		thrusterSound.setLocation(this.location);
		
		// age the missile and explode if necessary
		timeLived += timeStep;
		if(timeLived >= detonationTime){
			explode();
		}
		
		// update particle trail
		fire.update(timeStep);
	}
	
	@Override
	public void draw(){
		super.draw();
		fire.draw();
	}

	@Override
	public int getDamage() {
		return DAMAGE;
	}

	@Override
	public Entity getOwner() {
		return null;
	}

}
