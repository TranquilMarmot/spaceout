package com.bitwaffle.spaceout.entities.dynamic;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.interfaces.Projectile;
import com.bitwaffle.spaceout.resources.Models;


/**
 * Bullet that just keeps going straight
 * 
 * @author TranquilMarmot
 * 
 */
public class LaserBullet extends DynamicEntity implements Projectile{
	final static short COL_GROUP = CollisionTypes.PROJECTILE;
	final static short COL_WITH = (short)(CollisionTypes.WALL | CollisionTypes.PLANET);
	
	private int damage;
	private Entity origin;
	
	/** how long the bullet stays alive for */
	public float life = 10.0f;

	/** how long the bullet has been alive */
	public float timeAlive = 0.0f;

	public LaserBullet(Entity origin, Vector3f location, Quaternion rotation, Models model,
			float mass, float restitution, int damage, float speed) {
		super(location, rotation, model, mass, restitution);
		this.type = "Bullet";
		this.damage = damage;
		
		this.rigidBody.setCcdMotionThreshold(5.0f);
		
		this.origin = origin;
		
		// give the bullet some speed
		javax.vecmath.Vector3f currentVelocity = new javax.vecmath.Vector3f();
		rigidBody.getInterpolationLinearVelocity(currentVelocity);
		Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(
				0.0f, 0.0f, speed), rotation);
		this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(vec.x,
				vec.y, vec.z));
	}

	@Override
	public void update(float timeStep) {
		// add the delta to the time alive
		timeAlive += timeStep;

		// if the time is up, remove and delete this bullet
		if (timeAlive >= life) {
			removeFlag = true;
		}
	}

	@Override
	public int getDamage() {
		return damage;
	}
	
	@Override
	public Entity getOwner(){
		return origin;
	}
}
