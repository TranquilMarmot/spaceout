package com.bitwaffle.spaceout.entities.dynamic;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.entities.particles.Emitter;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.interfaces.Projectile;
import com.bitwaffle.spaceout.resources.Models;
import com.bitwaffle.spaceout.resources.Textures;
import com.bulletphysics.linearmath.Transform;

public class Missile extends DynamicEntity implements Projectile{
	private static final int DAMAGE = 100;
	
	private float speed;
	private float speedIncrease;
	private float detonationTime, timeLived;
	
	private static final Vector3f FIRE_OFFSET = new Vector3f(0.0f, 0.0f, -2.0f);
	private static final Vector3f FIRE_LOC_VARIANCE = new Vector3f(1.0f, 1.0f, 1.0f);
	private static final Vector3f FIRE_VELOC_VARIANCE = new Vector3f(2.0f, 2.0f, 5.0f);
	private static final float FIRE_EMIT_SPEED = 0.05f;
	private static final int FIRE_PARTICLES_PER_EMISSION = 2;
	private static final float FIRE_PARTICLE_TTL_VARIANCE = 2.0f;
	
	private DynamicEntity target;
	private Emitter fire;

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
	}
	
	@Override
	public void update(float timeStep){
		if(target == null){
			Vector3f forward = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, speed), this.rotation);
			this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(forward.x, forward.y, forward.z));
			speed += speedIncrease * timeStep;
		} else{
			if(target.removeFlag){
				target = null;
			}else{
				// find the difference between this's location and following's location then negate it to go towards it
				Vector3f subtract = new Vector3f();
				Vector3f.sub(this.location, target.location, subtract);
				subtract.negate(subtract);
				subtract.normalise(subtract);
				
				float dx1 = (subtract.x * speed);
				float dy1 = (subtract.y * speed);
				float dz1 = (subtract.z * speed);
				
				// set linear velocity to go towards following
				this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(dx1, dy1, dz1));
				
				speed += speedIncrease * timeStep;
				
				// FIXME this code doesn't work quite right!
				Quaternion wat = QuaternionHelper.quaternionBetweenVectors(this.location, subtract);
				this.rotation.set(wat);
					
				Transform trans = new Transform();
				this.rigidBody.getWorldTransform(trans);
				trans.setRotation(new Quat4f(wat.x, wat.y, wat.z, wat.w));
				this.rigidBody.setWorldTransform(trans);
			}
		}
		
		timeLived += timeStep;
		
		if(timeLived >= detonationTime){
			this.removeFlag = true;
		}
		
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
