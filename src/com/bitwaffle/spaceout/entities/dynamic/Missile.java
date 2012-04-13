package com.bitwaffle.spaceout.entities.dynamic;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.interfaces.Bullet;
import com.bitwaffle.spaceout.resources.Models;
import com.bulletphysics.linearmath.Transform;

public class Missile extends DynamicEntity implements Bullet{
	private static final int DAMAGE = 100;
	private static final float DETONATION_DISTACNE = 10.0f;
	private static final float SPEED = 100.0f;
	
	private DynamicEntity target;

	public Missile(Vector3f location, Quaternion rotation, DynamicEntity target) {
		// TODO these should probably be represented by cylinders instead of a convex hull
		super(location, rotation, Models.MISSILE, 100.0f, 1.0f, CollisionTypes.PROJECTILE,
				CollisionTypes.EVERYTHING);
		
		this.target = target;
	}
	
	@Override
	public void update(float timeStep){
		// find the difference between this's location and following's location then negate it to go towards it
		Vector3f subtract = new Vector3f();
		Vector3f.sub(this.location, target.location, subtract);
		subtract.normalise(subtract);
		subtract.negate(subtract);
		
		float dx = (subtract.x * SPEED);
		float dy = (subtract.y * SPEED);
		float dz = (subtract.z * SPEED);
		
		// set linear velocity to go towards following
		this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(dx, dy, dz));
		
		
		Vector3f cross = new Vector3f();
		Vector3f thisLocNorm = new Vector3f();
		this.location.normalise(thisLocNorm);
		Vector3f targetLocNorm = new Vector3f();
		target.location.normalise(targetLocNorm);
		Vector3f.cross(thisLocNorm, targetLocNorm, cross);
		//Vector3f.cross(this.location, target.location, cross);
		//cross.normalise(cross);
		float angle = Vector3f.angle(this.location, target.location);
		cross.negate(cross);
		
		
		Quaternion toTarget = QuaternionHelper.getQuaternionFromAxisAngle(cross, angle);
		
		Vector3f by90 = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(-45.0f, 0.0f, 0.0f), this.rotation);
		
		toTarget = QuaternionHelper.rotate(toTarget, by90);
		
		Quat4f targetquat = new Quat4f(toTarget.x, toTarget.y, toTarget.z, toTarget.w);
		Quat4f thisquat = new Quat4f(rotation.x, rotation.y, rotation.z, rotation.w);
		
		//Quaternion newRot = QuaternionHelper.rotate(this.rotation, new Vector3f(-5.0f, 0.0f, 0.0f));
		//Quat4f thisquat = new Quat4f(newRot.x, newRot.y, newRot.z, newRot.w);
		
		//float interpolationAmount = timeStep;
		float interpolationAmount = timeStep * 50.0f;
		
		thisquat.interpolate(targetquat, thisquat, interpolationAmount);
		
		this.rotation.set(thisquat.x, thisquat.y, thisquat.z, thisquat.w);
		
		Transform trans = new Transform();
		this.rigidBody.getWorldTransform(trans);
		trans.setRotation(thisquat);
		this.rigidBody.setWorldTransform(trans);
		
		
		//Vector3f speed = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, SPEED), this.rotation);
		//this.rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(speed.x, speed.y, speed.z));
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
