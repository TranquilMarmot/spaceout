package com.bitwaffle.spaceout.entities.dynamic;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.entities.particles.Emitter;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.interfaces.Bullet;
import com.bitwaffle.spaceout.resources.Models;
import com.bitwaffle.spaceout.resources.Textures;
import com.bulletphysics.linearmath.Transform;

public class Missile extends DynamicEntity implements Bullet{
	private static final int DAMAGE = 100;
	//private static final float DETONATION_DISTACNE = 10.0f;
	private float speed = 25.0f;
	private float speedIncrease = 100.0f;
	private static Vector3f fireOffset = new Vector3f(0.0f, 0.0f, -2.0f);
	private static Vector3f locationVariance = new Vector3f(1.0f, 1.0f, 1.0f);
	private static Vector3f velocityVariance = new Vector3f(2.0f, 2.0f, 5.0f);
	private static float emitSpeed = 0.05f;
	private static int particlesPerEmission = 2;
	private static float particleTTLVariance = 2.0f;
	
	private DynamicEntity target;
	private Emitter fire;

	public Missile(Vector3f location, Quaternion rotation, DynamicEntity target) {
		// TODO these should probably be represented by cylinders instead of a convex hull
		super(location, rotation, Models.MISSILE, 100.0f, 1.0f, CollisionTypes.PROJECTILE,
				CollisionTypes.EVERYTHING);
		
		this.target = target;
		
		fire = new Emitter(this, Textures.FIRE, fireOffset, locationVariance, velocityVariance, emitSpeed, particlesPerEmission, particleTTLVariance);
	}
	
	@Override
	public void update(float timeStep){
		// FIXME missiles probably shouldn't just disappear if their target gets destroyed...
		if(target.removeFlag){
			this.removeFlag = true;
		} else{
			// find the difference between this's location and following's location then negate it to go towards it
			Vector3f subtract = new Vector3f();
			Vector3f.sub(this.location, target.location, subtract);
			subtract.normalise(subtract);
			subtract.negate(subtract);
			
			float dx1 = (subtract.x * speed);
			float dy1 = (subtract.y * speed);
			float dz1 = (subtract.z * speed);
			
			// set linear velocity to go towards following
			this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(dx1, dy1, dz1));
			
			speed += speedIncrease * timeStep;
			
			// FIXME this code doesn't work quite right!
			Quaternion wat = QuaternionHelper.quaternionBetweenVectors(this.location, subtract);
			if(Quaternion.dot(this.rotation, wat) != 1.0f){
				Quat4f thisQuat = new Quat4f(rotation.x, rotation.y, rotation.z, rotation.w);
				Quat4f destQuat = new Quat4f(wat.x, wat.y, wat.z, wat.w);
				
				float interpAmnt = timeStep * 50.0f;
				
				thisQuat.interpolate(destQuat, thisQuat, interpAmnt);
				
				this.rotation.set(thisQuat.x, thisQuat.y, thisQuat.z, thisQuat.w);
				
				Transform trans = new Transform();
				this.rigidBody.getWorldTransform(trans);
				trans.setRotation(thisQuat);
				this.rigidBody.setWorldTransform(trans);
			}
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
