package com.bitwaffle.spaceout.entities.dynamic;

import java.util.Random;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;


import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.particles.trail.Trail;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.graphics.shapes.VBOQuadric;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.entities.passive.particles.Explosion;
import com.bitwaffle.spaceout.interfaces.Health;
import com.bitwaffle.spaceout.resources.Textures;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * Pretty much just a dynamic entity that's represented by a sphere
 * @author TranquilMarmot
 *
 */
public class Planet extends DynamicEntity implements Health{
	final static short COL_GROUP = CollisionTypes.PLANET;
	final static short COL_WITH = (short)(CollisionTypes.SHIP | CollisionTypes.WALL | CollisionTypes.PLANET | CollisionTypes.PICKUP | CollisionTypes.PROJECTILE);
	
	private static SphereShape shape;
	
	private Trail trail;
	
	//FIXME planets shouldnt really have health this is for shits and giggles
	int health = 100;
	
	private Textures texture;
	
	private VBOQuadric quadric;
	
	public Planet(Vector3f location, Quaternion rotation, float size,
			float mass, float restitution, Textures texture) {
		super(location, QuaternionHelper.rotate(rotation, new Vector3f(90.0f, 0.0f, 0.0f)), getShape(size), mass, restitution, COL_GROUP, COL_WITH);
		
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.015f, 0.0f));
		
		this.type = "Planet";
		this.texture = texture;
		
		quadric = new VBOQuadric(size, 20, 20);
		
		trail = new Trail(this, 20, 0.2f, Textures.TRAIL, new Vector3f(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	public void draw(){
		Render3D.useDefaultMaterial();
		texture.texture().bind();
		quadric.draw();
		trail.draw();
	}
	
	private static SphereShape getShape(float size){
		shape = new SphereShape(size);
		return shape;
	}
	
	@Override
	/**
	 * Draws the physics debug info for this entity. Should be called before
	 * rotations are applied.
	 */
	public void drawPhysicsDebug() {
		Transform worldTransform = new Transform();
		rigidBody.getWorldTransform(worldTransform);

		Physics.dynamicsWorld.debugDrawObject(worldTransform, shape,
				new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	public void update(float timeStep){
		super.update(timeStep);
		trail.update(timeStep);
	}

	@Override
	public int getCurrentHealth() {
		return health;
	}

	@Override
	public void hurt(int amount) {
		health -= amount;
		if(health <= 0){
			explode();
		}
		
	}
	
	private void explode(){
		Explosion splode = new Explosion(this.location, this.rotation, 1.0f, 5.0f);
		Entities.addPassiveEntity(splode);
		removeFlag = true;
		
		for(int i = 0; i < 25; i++){
			addRandomDiamond();
		}
	}
	
	// TODO find a better way to do loot drops
	private void addRandomDiamond() {
		Random randy = new Random();

		float diamondX = randy.nextFloat() * 10.0f;
		float diamondY = randy.nextFloat() * 10.0f;
		float diamondZ = randy.nextFloat() * 10.0f;
		
		if(randy.nextBoolean()) diamondX = -diamondX;
		if(randy.nextBoolean()) diamondY = -diamondY;
		if(randy.nextBoolean()) diamondZ = -diamondZ;
		
		Vector3f diamondLocation = new Vector3f();

		// put the sphere right in front of the camera
		Vector3f downInFront = QuaternionHelper.rotateVectorByQuaternion(
				new Vector3f(diamondX, diamondY, diamondZ), this.rotation);

		Vector3f.add(this.location, downInFront, diamondLocation);

		Quaternion diamondRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		
		float xRot = randy.nextFloat() * 100.0f;
		float yRot = randy.nextFloat() * 100.0f;
		float zRot = randy.nextFloat() * 100.0f;
		
		diamondRotation = QuaternionHelper.rotate(diamondRotation, new Vector3f(xRot,yRot, zRot));
		
		float diamondStopSpeed = 0.3f;
		
		Diamond d = new Diamond(diamondLocation, diamondRotation, diamondStopSpeed);

		Entities.addDynamicEntity(d);
	}

	@Override
	public void heal(int amount) {
		health += amount;
	}
}
