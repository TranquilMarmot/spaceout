package com.bitwaffle.spaceout.entities.dynamic;

import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.interfaces.Health;
import com.bitwaffle.spaceout.resources.Models;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * Pretty much just a dynamic entity that's represented by a sphere
 * @author TranquilMarmot
 *
 */
public class Asteroid extends DynamicEntity implements Health{
	final static short COL_GROUP = CollisionTypes.PLANET;
	final static short COL_WITH = (short)(CollisionTypes.SHIP | CollisionTypes.WALL | CollisionTypes.PLANET | CollisionTypes.PICKUP | CollisionTypes.PROJECTILE);
	
	int health = 100;
	
	private float size;
	
	public Asteroid(Vector3f location, Quaternion rotation, float size,
			float mass, float restitution) {
		super(location, QuaternionHelper.rotate(rotation, new Vector3f(90.0f, 0.0f, 0.0f)), getShape(size), mass, restitution, COL_GROUP, COL_WITH);
		
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.015f, 0.0f));
		
		this.type = "Asteroid";
		this.model = Models.ASTEROID.getModel();
		this.size = size;
	}
	
	private static CollisionShape getShape(float size){
		//CollisionShape shape = Models.ASTEROID.getModel().getCollisionShape();
		//shape.setLocalScaling(new javax.vecmath.Vector3f(size, size, size));
		return new SphereShape(size);
	}
	
	@Override
	public void draw(){
		Matrix4f oldModelView = new Matrix4f();
		oldModelView.load(Render3D.modelview);
		Render3D.modelview.scale(new org.lwjgl.util.vector.Vector3f(size, size, size));
		Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);
		super.draw();
		Render3D.modelview.load(oldModelView);
		// TODO save, scale, draw, reset
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
