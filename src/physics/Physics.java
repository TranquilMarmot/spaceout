package physics;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Physics {
	public static DiscreteDynamicsWorld dynamicsWorld;

	public static BroadphaseInterface broadphase;
	public static CollisionDispatcher dispatcher;
	public static SequentialImpulseConstraintSolver solver;

	public static void initPhysics() {
		// broadphase interface
		broadphase = new DbvtBroadphase();

		// collision configuration and dispatcher
		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);

		// wut
		solver = new SequentialImpulseConstraintSolver();

		// the world everything is in
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase,
				solver, collisionConfiguration);

		// no gravity, we're in space!
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
	}
	
	public static void update(){
		dynamicsWorld.stepSimulation(1.0f / 60.0f, 10);
	}
	
	private static void temp(){
		/* BEGIN TEST SHAPES */
		// CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1,
		// 0), 1);
		CollisionShape groundShape = new BoxShape(new Vector3f(50.0f, 50.0f,
				50.0f));

		CollisionShape fallShape = new SphereShape(1);

		Transform groundTransform = new Transform();
		groundTransform.setRotation(new Quat4f(0.0f, 0.0f, 0.0f, 1.0f));
		groundTransform.origin.set(0, -56, 0);

		DefaultMotionState groundMotionState = new DefaultMotionState(
				groundTransform);

		// first variable here is mass
		RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(
				0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
		RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);

		dynamicsWorld.addRigidBody(groundRigidBody);

		Transform fallTransform = new Transform();
		fallTransform.setRotation(new Quat4f(0.0f, 0.0f, 0.0f, 1.0f));
		fallTransform.origin.set(0.0f, 50.0f, 0.0f);

		DefaultMotionState fallMotionState = new DefaultMotionState(
				fallTransform);

		float mass = 1.0f;
		Vector3f fallInertia = new Vector3f(0.0f, 0.0f, 0.0f);
		fallShape.calculateLocalInertia(mass, fallInertia);

		RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(
				mass, fallMotionState, fallShape, fallInertia);
		RigidBody fallRigidBody = new RigidBody(fallRigidBodyCI);
		dynamicsWorld.addRigidBody(fallRigidBody);

		/* END TEST SHAPES */

		for (int i = 0; i < 300; i++) {
			dynamicsWorld.stepSimulation(1.0f / 60.0f, 10);

			Transform trans = new Transform();
			fallRigidBody.getMotionState().getWorldTransform(trans);

			System.out.println("Sphere height: " + trans.origin.y);
		}
	}
}