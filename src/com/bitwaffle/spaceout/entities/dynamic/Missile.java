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
	private float speed = 100.0f;
	
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
		
		float dx1 = (subtract.x * speed);
		float dy1 = (subtract.y * speed);
		float dz1 = (subtract.z * speed);
		
		// set linear velocity to go towards following
		this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(dx1, dy1, dz1));
		
		speed += 0.5f;
		
		Quaternion wat = QuaternionHelper.quaternionBetweenVectors(this.location, new Vector3f(dx1, dy1, dz1));
		
		this.rotation.set(wat);
		
		Transform trans = new Transform();
		this.rigidBody.getWorldTransform(trans);
		trans.setRotation(new Quat4f(wat.x, wat.y, wat.z, wat.w));
		this.rigidBody.setWorldTransform(trans);
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
