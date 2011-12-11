package physics;

import java.util.Random;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Quaternion;

import physics.debug.PhysicsDebugDrawer;
import physics.sandbox.DynamicEntity;
import physics.sandbox.Sandbox;
import util.manager.TextureManager;

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

import entities.Entities;
import graphics.model.Model;

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
		dynamicsWorld.setGravity(new Vector3f(0, -5.0f, 0));
		
		dynamicsWorld.setDebugDrawer(new PhysicsDebugDrawer());
		
		//temp();
	}
	
	public static void update(){		
		dynamicsWorld.stepSimulation(1.0f / 60.0f, 10);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_P)){
			Sandbox.addRandomSphere();
		}
	}
}