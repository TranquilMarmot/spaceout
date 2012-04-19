package com.bitwaffle.spaceguts.physics;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.vecmath.Quat4f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceout.entities.dynamic.Missile;
import com.bitwaffle.spaceout.interfaces.Projectile;
import com.bitwaffle.spaceout.interfaces.Health;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.linearmath.Transform;

public class DynamicEntityCallback extends InternalTickCallback {
	@Override
	public void internalTick(DynamicsWorld world, float timeStep) {
		Iterator<CollisionObject> it = world.getCollisionObjectArray()
				.iterator();
		while (it.hasNext()) {
			CollisionObject c = null;
			try {
				c = it.next();
			} catch (NoSuchElementException e) {
				//FIXME dunno about this, will it cause entities to be update twice?
				it = world.getCollisionObjectArray().iterator();
			}

			if (c != null) {
				processEntity(c, timeStep);
			}
		}
		
		checkForCollisions();
		
		// this is a very important call! Updates the camera, skybox, and any non-dynamic entities
		Entities.updateAll(timeStep);
	}

	/**
	 * Updates an entity's location and then performs any necessary logic
	 * 
	 * @param c
	 *            Collision object for the entity being updated
	 * @param timeStep
	 *            Amount of time passed since last tick
	 */
	private void processEntity(CollisionObject c, float timeStep) {
		DynamicEntity ent = (DynamicEntity) c.getUserPointer();

		if (ent.removeFlag) {
			Physics.dynamicsWorld.removeCollisionObject(c);
			Entities.dynamicEntities.remove(ent);
		} else {
			// get the rigid body's world transform
			Transform trans = new Transform();
			ent.rigidBody.getMotionState().getWorldTransform(trans);

			// set this entity's location
			javax.vecmath.Vector3f origin = trans.origin;
			ent.location.set(origin.x, origin.y, origin.z);

			// set this entity's rotation
			Quat4f rot = new Quat4f();
			trans.getRotation(rot);
			ent.rotation.set(rot.x, rot.y, rot.z, rot.w);

			ent.update(timeStep);
		}
	}
	
	private static void checkForCollisions(){
		for(int i = 0; i < Physics.dispatcher.getNumManifolds(); i++){
			PersistentManifold contactManifold = Physics.dispatcher.getManifoldByIndexInternal(i);
			CollisionObject objA = (CollisionObject) contactManifold.getBody0();
			CollisionObject objB = (CollisionObject) contactManifold.getBody1();
			
			for(int j = 0; j < contactManifold.getNumContacts(); j++){
				ManifoldPoint point = contactManifold.getContactPoint(j);
				if(point.getDistance() < 0.0f){
					//Vector3f ptA = new Vector3f(), ptB = new Vector3f(), normalOnB;
					//point.getPositionWorldOnA(ptA);
					//point.getPositionWorldOnB(ptB);
					//normalOnB = point.normalWorldOnB;
					
					DynamicEntity entA = (DynamicEntity) objA.getUserPointer();
					DynamicEntity entB = (DynamicEntity) objB.getUserPointer();
					
					if(entA instanceof Projectile && entB instanceof Health){
						bulletHealthCollision((Projectile) entA, (Health) entB);
						if(entA instanceof Missile)
							((Missile) entA).explode();
					} else if(entB instanceof Projectile && entA instanceof Health){
						bulletHealthCollision((Projectile) entB, (Health) entA);
						if(entB instanceof Missile)
							((Missile) entB).explode();
					}
					
					/*
					System.out.println("Contact point " + j + ":\nA: " + entA.type + " " + ptA.x + " " + ptA.y + " " + ptA.z);
					System.out.println("B: " + entB.type + " " + ptB.x + " " + ptB.y + " " + ptB.z);
					System.out.println("Normal on B: " + normalOnB.x + " " + normalOnB.y + " " + normalOnB.z);
					*/
				}
			}
		}
	}
	
	private static void bulletHealthCollision(Projectile bullet, Health health){
		if(bullet.getOwner() != health)
			health.hurt(bullet.getDamage());
	}
}