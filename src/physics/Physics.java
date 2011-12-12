package physics;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;

import physics.debug.PhysicsDebugDrawer;
import physics.sandbox.Sandbox;
import util.manager.KeyboardManager;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;

public class Physics {
	private final static int SUBSTEPS = 7;
	
	public static DiscreteDynamicsWorld dynamicsWorld;

	public static BroadphaseInterface broadphase;
	public static CollisionDispatcher dispatcher;
	public static SequentialImpulseConstraintSolver solver;
	
	private static Clock clock = new Clock();
	
	private static boolean debugDown = false;
	public static boolean drawDebug = true;

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
		dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
		
		dynamicsWorld.setDebugDrawer(new PhysicsDebugDrawer());
	}
	
	public static void update(){
		//dynamicsWorld.stepSimulation(1.0f / 60.0f, 10);
		dynamicsWorld.stepSimulation(getDeltaTimeMicroseconds() / 1000000.0f, SUBSTEPS);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_P)){
			Sandbox.addRandomSphere();
		}
		
		// handle the physics debug key
		if(KeyboardManager.physicsDebug && !debugDown){
			drawDebug = !drawDebug;
			debugDown = true;
		}
		if(!KeyboardManager.physicsDebug){
			debugDown = false;
		}
	}
	
	private static float getDeltaTimeMicroseconds(){
		float delta = clock.getTimeMicroseconds();
		clock.reset();
		return delta;
	}
}