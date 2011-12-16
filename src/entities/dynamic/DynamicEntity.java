package entities.dynamic;

import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import physics.Physics;
import util.manager.ModelManager;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import entities.Entity;
import graphics.model.Model;

/**
 * And entity that also keeps track of it's own rigid body and model
 * @author TranquilMarmot
 *
 */
public class DynamicEntity extends Entity {
	public RigidBody rigidBody;
	public float mass;
	public Model model;

	/**
	 * Overloaded constructor
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
		this.mass = mass;

		Transform transform = new Transform();
		transform.setRotation(new Quat4f(rotation.x, rotation.y, rotation.z,
				rotation.w));
		transform.origin.set(location.x, location.y, location.z);
		DefaultMotionState defaultState = new DefaultMotionState(transform);

		javax.vecmath.Vector3f loca = new javax.vecmath.Vector3f(location.x,
				location.y, location.z);

		RigidBodyConstructionInfo rigidBodyCI = new RigidBodyConstructionInfo(
				mass, defaultState, model.getCollisionShape(), loca);
		rigidBodyCI.restitution = restitution;
		rigidBody = new RigidBody(rigidBodyCI);

		Physics.dynamicsWorld.addRigidBody(rigidBody);
	}

	@Override
	public void update() {
		Transform trans = new Transform();
		rigidBody.getMotionState().getWorldTransform(trans);

		javax.vecmath.Vector3f origin = trans.origin;
		this.location.set(origin.x, origin.y, origin.z);

		Quat4f rot = new Quat4f();
		trans.getRotation(rot);
		this.rotation.set(rot.x, rot.y, rot.z, rot.w);
	}

	@Override
	public void draw() {
		if(this.type.equals("sphere"))
			GL11.glDisable(GL11.GL_LIGHTING);
		model.getTexture().bind();
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		//if(Physics.drawDebug)
		//	drawPhysicsDebug();

		GL11.glCallList(model.getCallList());
	}
	
	public void drawPhysicsDebug(){
		Transform worldTransform = new Transform();
		rigidBody.getWorldTransform(worldTransform);
		
		CollisionShape shape = model.getCollisionShape();
	
		Physics.dynamicsWorld.debugDrawObject(worldTransform, shape, new javax.vecmath.Vector3f(0.0f, 0.0f, 1.0f));
	}

	@Override
	public void cleanup() {
		Physics.dynamicsWorld.removeRigidBody(rigidBody);
		rigidBody.destroy();
	}
}
