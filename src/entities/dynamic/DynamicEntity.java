package entities.dynamic;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import physics.Physics;
import util.manager.ModelManager;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import entities.Entities;
import entities.Entity;
import graphics.model.Model;

/**
 * And entity that also keeps track of it's own rigid body and model
 * 
 * @author TranquilMarmot
 * 
 */
public class DynamicEntity extends Entity {
	/** the rigid body for this entity */
	public RigidBody rigidBody;

	/** the model to use for this entity */
	public Model model;

	/** if this is true, the next time the entity is update it is removed from the world*/
	public boolean removeFlag = false;

	/**
	 * Overloaded constructor
	 * 
	 * @param location
	 * @param rotation
	 * @param model
	 * @param mass
	 * @param restitution
	 */
	public DynamicEntity(Vector3f location, Quaternion rotation, int model,
			float mass, float restitution) {
		this(location, rotation, ModelManager.getModel(model), mass,
				restitution);
	}

	public DynamicEntity(Vector3f location, Quaternion rotation, Model model,
			float mass, float restitution) {
		this.location = location;
		this.rotation = rotation;
		this.model = model;

		/* BEGIN RIGID BODY CREATION */
		Transform transform = new Transform();
		transform.setRotation(new Quat4f(rotation.x, rotation.y, rotation.z,
				rotation.w));
		transform.origin.set(location.x, location.y, location.z);
		DefaultMotionState defaultState = new DefaultMotionState(transform);

		javax.vecmath.Vector3f loca = new javax.vecmath.Vector3f(location.x,
				location.y, location.z);

		CollisionShape shape = model.getCollisionShape();

		javax.vecmath.Vector3f fallInertia = new javax.vecmath.Vector3f(0.0f,
				0.0f, 0.0f);
		shape.calculateLocalInertia(mass, fallInertia);

		RigidBodyConstructionInfo rigidBodyCI = new RigidBodyConstructionInfo(
				mass, defaultState, shape, loca);
		rigidBodyCI.restitution = restitution;
		rigidBody = new RigidBody(rigidBodyCI);

		rigidBody.setUserPointer(this);

		Physics.dynamicsWorld.setInternalTickCallback(
				new DynamicEntityCallback(), null);
		Physics.dynamicsWorld.addRigidBody(rigidBody);
		/* END RIGID BODY CREATION */
	}

	/**
	 * This update method is called at the end of every physics tick with the
	 * amount of time passed since the previous tick
	 * 
	 * @param timeStep
	 *            Amount of time passed since last tick (automatically passed
	 */
	public void update(float timeStep) {
	}

	@Override
	/**
	 * This method never gets used for a dynamic entity, don't use it!!!
	 */
	public void update() {
		System.out
				.println("Don't use update() on dynamic entities! update(float timeStep) is called at the end of every physics tick, use that instead! ("
						+ type + ")");
	}

	@Override
	/**
	 * Simple as possible drawing call. This assumes that it's called when the entity's location and rotation have already been applied to the modelview matrix.
	 */
	public void draw() {
		model.getTexture().bind();
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glCallList(model.getCallList());
	}

	/**
	 * Draws the physics debug info for this entity. Should be called before
	 * rotations are applied.
	 */
	public void drawPhysicsDebug() {
		Transform worldTransform = new Transform();
		rigidBody.getWorldTransform(worldTransform);

		CollisionShape shape = model.getCollisionShape();

		Physics.dynamicsWorld.debugDrawObject(worldTransform, shape,
				new javax.vecmath.Vector3f(0.0f, 0.0f, 1.0f));
	}

	@Override
	/**
	 * Removes the rigid body from the dynamics world it's in
	 */
	public void cleanup() {
		removeFlag = true;
	}
}

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
	 * @param c Collision object for the entity being updated
	 * @param timeStep Amount of time passed since last tick
	 */
	private void processEntity(CollisionObject c, float timeStep){
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
