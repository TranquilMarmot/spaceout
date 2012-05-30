package com.bitwaffle.spaceout.entities.dynamic;

import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.entities.passive.AsteroidField;
import com.bitwaffle.spaceout.interfaces.Health;
import com.bitwaffle.spaceout.interfaces.Projectile;
import com.bitwaffle.spaceout.resources.Models;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * A la Atari's 1979 classic, Asteroids.
 * @author TranquilMarmot
 */
public class Asteroid extends DynamicEntity implements Health, Projectile{
	final static short COL_GROUP = CollisionTypes.PLANET;
	final static short COL_WITH = (short)(CollisionTypes.SHIP | CollisionTypes.WALL | CollisionTypes.PLANET | CollisionTypes.PICKUP | CollisionTypes.PROJECTILE);
	
	/** Used for scaling the modelview when drawing */
	private static Matrix4f oldModelView = new Matrix4f();
	
	/** The fastest the asteroid can spin */
	final static float ANGVEC_CAP = 10.0f;
	
	/** How fast the asteroids this asteroid creates will be spinning when they're created */
	final static float SPAWN_ANGVEC_FACTOR = 25.0f;
	
	/** How much damage the asteroid does when it hits the player */
	final static int DAMAGE = 10;
	
	/** How bouncy the asteroid is */
	final static float ASTEROID_RESTITUTION = 0.5f; 
	
	/** Size at which the asteroid will drop loot instead of splitting into smaller asteroids*/
	final static float LOOT_SIZE = 25.0f;
	
	/** How many items an asteroid drops when it's destroyed and is smaller than LOOT_SIZE */
	final static int LOOT_AMOUNT = 25;
	
	/** How many asteroids are created when the asteroid is destroyed */
	final static int NUMBER_OF_DIVISIONS = 3;
	
	/** How heavy an asteroid is based on it's size (mass = size * MASS_FACTOR) */
	final static float MASS_FACTOR = 10;
	
	/** How much health the asteroid has */
	int health = 80;
	
	/** How big the asteroid is */
	private float size;
	
	/** Which asteroid field this asteroid belongs to */
	private AsteroidField field;
	
	/**
	 * ASS-teroid heh heh heh
	 * @param location Location of asteroid
	 * @param rotation Rotation of asteroid
	 * @param size How big the asteroid is
	 */
	public Asteroid(Vector3f location, Quaternion rotation, float size, AsteroidField field) {
		super(location, rotation, new SphereShape(size), size * MASS_FACTOR, ASTEROID_RESTITUTION, COL_GROUP, COL_WITH);
		
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.015f, 0.0f));
		
		this.type = "Asteroid (size " + size + " health " + health + ")";
		this.model = Models.ASTEROID.getModel();
		this.size = size;
		
		if(field != null){
			this.field = field;
			field.addAsteroidToField(this);
		}
	}
	
	@Override
	public void draw(){
		// scale the modelview before drawing
		oldModelView.load(Render3D.modelview);
		Render3D.modelview.scale(new org.lwjgl.util.vector.Vector3f(size, size, size));
		Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);
		super.draw();
		Render3D.modelview.load(oldModelView);
	}
	
	@Override
	/**
	 * Draws the physics debug info for this entity. Should be called before
	 * rotations are applied.
	 */
	public void drawPhysicsDebug() {
		Transform worldTransform = new Transform();
		rigidBody.getWorldTransform(worldTransform);

		Physics.dynamicsWorld.debugDrawObject(worldTransform, Models.ASTEROID.getModel().getCollisionShape(),
				new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	public void update(float timeStep){
		super.update(timeStep);
		
		capAngularVelocity();
	}
	
	/**
	 * Keep the asteroid from spinning too fast
	 */
	private void capAngularVelocity(){
		javax.vecmath.Vector3f angVec = new javax.vecmath.Vector3f();
		rigidBody.getAngularVelocity(angVec);
		float speed = angVec.length();
		if(speed > ANGVEC_CAP){
			angVec.x *= ANGVEC_CAP / speed;
			angVec.y *= ANGVEC_CAP / speed;
			angVec.z *= ANGVEC_CAP / speed;
			rigidBody.setAngularVelocity(angVec);
		}
	}

	@Override
	public int getCurrentHealth() {
		return health;
	}

	@Override
	public void hurt(int amount) {
		health -= amount;
		this.type = "Asteroid (size " + size + " health " + health + ")";
		if(health <= 0){
			explode();
		} else {
			size -= amount / 2.0f;
			if(size <= LOOT_SIZE)
				explode();
		}
	}
	
	/**
	 * Cause the asteroid to explode, either leaving behind more asteroids or some diamonds
	 */
	private void explode(){
		removeFlag = true;
		
		if(size <= LOOT_SIZE){
			for(int i = 0; i < 25; i++){
				addRandomDiamond();
			}
		} else{
			for(int i = 0; i < NUMBER_OF_DIVISIONS; i++){
				addRandomAsteroid(this.size / NUMBER_OF_DIVISIONS);
			}
		}
	}
	
	/**
	 * Adds a random asteroid at this asteroid's location
	 * @param newSize New size of asteroid
	 */
	private void addRandomAsteroid(float newSize){
		Random randy = new Random();

		float asteroidX = randy.nextFloat() * 10.0f;
		float asteroidY = randy.nextFloat() * 10.0f;
		float asteroidZ = randy.nextFloat() * 10.0f;
		
		// randomly go positive or negative on each axis (+/- size of parent asteroid)
		asteroidX = randy.nextBoolean() ? -asteroidX - size : asteroidX + size;
		asteroidY = randy.nextBoolean() ? -asteroidY - size : asteroidY + size;
		asteroidZ = randy.nextBoolean() ? -asteroidZ - size : asteroidZ + size;
		
		Vector3f asteroidLocation = new Vector3f(asteroidX, asteroidY, asteroidZ);
		Vector3f.add(this.location, asteroidLocation, asteroidLocation);
		
		// make the new asteroid face a random direction
		Quaternion asteroidRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		float xRot = randy.nextFloat() * 100.0f;
		float yRot = randy.nextFloat() * 100.0f;
		float zRot = randy.nextFloat() * 100.0f;
		asteroidRotation = QuaternionHelper.rotate(asteroidRotation, new Vector3f(xRot,yRot, zRot));
		
		Asteroid a = new Asteroid(asteroidLocation, asteroidRotation, newSize, field);
		
		// randomly go +/- the parent asteroid's speed
		javax.vecmath.Vector3f linVec = new javax.vecmath.Vector3f();
		this.rigidBody.getLinearVelocity(linVec);
		if(randy.nextBoolean()) linVec.x = -linVec.x;
		if(randy.nextBoolean()) linVec.y = -linVec.y;
		if(randy.nextBoolean()) linVec.z = -linVec.z;
		a.rigidBody.setLinearVelocity(linVec);

		// randomly go +/- the parent asteroid's angular speed
		javax.vecmath.Vector3f angVec = new javax.vecmath.Vector3f();
		this.rigidBody.getAngularVelocity(angVec);
		if(randy.nextBoolean()) angVec.x = -angVec.x;
		if(randy.nextBoolean()) angVec.y = -angVec.y;
		if(randy.nextBoolean()) angVec.y = -angVec.y;
		a.rigidBody.setAngularVelocity(angVec);
		
		Entities.addDynamicEntity(a);
	}
	
	// TODO find a better way to do loot drops
	/**
	 * Add some random diamonds
	 */
	private void addRandomDiamond() {
		Random randy = new Random();

		float diamondX = randy.nextFloat() * 10.0f;
		float diamondY = randy.nextFloat() * 10.0f;
		float diamondZ = randy.nextFloat() * 10.0f;
		
		if(randy.nextBoolean()) diamondX = -diamondX;
		if(randy.nextBoolean()) diamondY = -diamondY;
		if(randy.nextBoolean()) diamondZ = -diamondZ;
		
		Vector3f diamondLocation = new Vector3f();

		Vector3f downInFront = QuaternionHelper.rotateVectorByQuaternion(
				new Vector3f(diamondX, diamondY, diamondZ), this.rotation);

		Vector3f.add(this.location, downInFront, diamondLocation);

		Quaternion diamondRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		
		float xRot = randy.nextFloat() * 100.0f;
		float yRot = randy.nextFloat() * 100.0f;
		float zRot = randy.nextFloat() * 100.0f;
		
		diamondRotation = QuaternionHelper.rotate(diamondRotation, new Vector3f(xRot,yRot, zRot));
		
		float diamondStopSpeed = 0.1f;
		
		Diamond d = new Diamond(diamondLocation, diamondRotation, diamondStopSpeed);

		Entities.addDynamicEntity(d);
	}

	@Override
	public void heal(int amount) {
		health += amount;
	}

	@Override
	public int getDamage() {
		return DAMAGE;
	}

	@Override
	public Entity getOwner() {
		return this;
	}
}
