package spaceguts.entities;

import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.physics.CollisionTypes;
import spaceguts.physics.Physics;
import spaceguts.util.manager.ModelManager;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import spaceguts.graphics.model.Model;

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

	/** if this is true, the next time the entity is updated it is removed from the world*/
	public boolean removeFlag = false;

	/**
	 * Overloaded constructor
	 */
	public DynamicEntity(Vector3f location, Quaternion rotation, int model,
			float mass, float restitution) {
		this(location, rotation, ModelManager.getModel(model), mass,
				restitution, CollisionTypes.NOTHING, CollisionTypes.NOTHING);
	}
	
	public DynamicEntity(Vector3f location, Quaternion rotation, Model model,
			float mass, float restitution) {
		this(location, rotation, model, mass, restitution, CollisionTypes.NOTHING, CollisionTypes.NOTHING);
	}
	
	public DynamicEntity(Vector3f location, Quaternion rotation, int model,
			float mass, float restitution, short collisionGroup, short collidesWith){
		this(location, rotation, ModelManager.getModel(model), mass,
				restitution, collisionGroup, collidesWith);
	}

	/**
	 * Creates the entity and adds it to the dynamics world (but NOT to Entities.dynamicEntities)
	 * @param location Initial location of the entity
	 * @param rotation Initial rotation of the entity (<i>Be careful!</i> If the quaternion isn't right, i.e. not normalized, funny things will happen)
	 * @param model The {@link Model} for the entity
	 * @param mass The mass for the entity
	 * @param restitution The restitution (bounciness) of the entity
	 * @param collisionGroup which group from {@link CollisionTypes} this entity belongs to
	 */
	public DynamicEntity(Vector3f location, Quaternion rotation, Model model,
			float mass, float restitution, short collisionGroup, short collidesWith) {
		// see Entity for location and rotation
		this.location = location;
		this.rotation = rotation;
		this.model = model;

		// the transform to use for putting the entity into the world
		Transform transform = new Transform();
		transform.setRotation(new Quat4f(rotation.x, rotation.y, rotation.z,
				rotation.w));
		transform.origin.set(location.x, location.y, location.z);
		DefaultMotionState defaultState = new DefaultMotionState(transform);

		// location to use for the entity (need a javax.vecmath Vector3f instead of the given org.lwjgl.util.vector Vector3f
		javax.vecmath.Vector3f loca = new javax.vecmath.Vector3f(location.x,
				location.y, location.z);

		// the collision shape is made when the model is made
		CollisionShape shape = model.getCollisionShape();

		// no initial fall inertia (it isn't vital to set this)
		javax.vecmath.Vector3f fallInertia = new javax.vecmath.Vector3f(0.0f,
				0.0f, 0.0f);
		shape.calculateLocalInertia(mass, fallInertia);

		// create the rigid body based on all the stuff we've grabbed
		RigidBodyConstructionInfo rigidBodyCI = new RigidBodyConstructionInfo(
				mass, defaultState, shape, loca);
		rigidBodyCI.restitution = restitution;
		rigidBody = new RigidBody(rigidBodyCI);

		// set the pointer so the entity can be updated (see DynamicEntityCallback)
		rigidBody.setUserPointer(this);

		// finally, add it to the world
		if(collisionGroup != CollisionTypes.NOTHING && collidesWith != CollisionTypes.NOTHING)
			Physics.dynamicsWorld.addRigidBody(rigidBody, collisionGroup, collidesWith);
		else
			Physics.dynamicsWorld.addRigidBody(rigidBody);
	}

	/**
	 * This update method is called at the end of every physics tick with the
	 * amount of time passed since the previous tick
	 * 
	 * @param timeStep
	 *            Amount of time passed since last tick (automatically passed by bullet)
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
