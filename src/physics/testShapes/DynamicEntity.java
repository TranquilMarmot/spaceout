package physics.testShapes;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import physics.Physics;
import util.helper.QuaternionHelper;
import util.manager.ModelManager;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import entities.Entities;
import entities.Entity;
import graphics.model.Model;

public class DynamicEntity extends Entity {
	public RigidBody rigidBody;
	public float mass;
	public Model model;
	// FIXME change this back to using an int from ModelManager instead of an actual model

	public DynamicEntity(Vector3f location, Quaternion rotation, Model model,
			float mass) {
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
		rigidBodyCI.restitution = 0.75f;
		rigidBody = new RigidBody(rigidBodyCI);

		Physics.dynamicsWorld.addRigidBody(rigidBody);

		rotationBuffer = BufferUtils.createFloatBuffer(16);
	}

	@Override
	public void update() {
		Transform trans = new Transform();
		rigidBody.getMotionState().getWorldTransform(trans);

		javax.vecmath.Vector3f origin = trans.origin;
		this.location.set(origin.x, origin.y, origin.z);
		this.location = new Vector3f(origin.x, origin.y, origin.z);

		Quat4f rot = new Quat4f();
		trans.getRotation(rot);
		this.rotation.set(rot.x, rot.y, rot.z, rot.w);
	}

	public void draw() {
		model.getTexture().bind();
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glPushMatrix();
		{
			QuaternionHelper.toFloatBuffer(rotation, rotationBuffer);
			GL11.glMultMatrix(rotationBuffer);
			GL11.glCallList(model.getCallList());
		}
		GL11.glPopMatrix();
	}
}
