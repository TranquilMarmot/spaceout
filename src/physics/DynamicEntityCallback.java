package physics;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.vecmath.Quat4f;


import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.linearmath.Transform;

import entities.Entities;
import entities.dynamic.DynamicEntity;

class DynamicEntityCallback extends InternalTickCallback {
	@Override
	public void internalTick(DynamicsWorld world, float timeStep) {
		Iterator<CollisionObject> it = world.getCollisionObjectArray()
				.iterator();
		while (it.hasNext()) {
			CollisionObject c = null;
			try {
				c = it.next();
			} catch (NoSuchElementException e) {
				it = world.getCollisionObjectArray().iterator();
			}

			if (c != null) {
				processEntity(c, timeStep);
			}
		}
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
			Entities.dynamicRemoveBuffer.add(ent);
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
}