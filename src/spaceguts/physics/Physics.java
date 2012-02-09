//http://www.aorensoftware.com/blog/2011/06/01/when-bullets-move-too-fast/
package spaceguts.physics;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Mouse;

import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entities;
import spaceguts.util.console.Console;
import spaceguts.util.input.KeyBindings;
import spaceout.DynamicEntityCallback;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;


/**
 * This class handles the dynamics world and everything related
 * 
 * @author TranquilMarmot
 * 
 */
public class Physics {
	/** maximum number of substeps to do on each tick */
	private final static int SUBSTEPS = 10;

	/** the discrete dynamics world */
	public static DiscreteDynamicsWorld dynamicsWorld;

	/** broadphase interface */
	public static BroadphaseInterface broadphase;

	/** collision dispatcher */
	public static CollisionDispatcher dispatcher;

	/** impulse constraint solver */
	public static SequentialImpulseConstraintSolver solver;

	/** clock to use for framerate independence */
	private static Clock clock = new Clock();

	/** to keep the debug key from being held */
	private static boolean debugDown = false;

	/** whether or not to draw physics debug info */
	public static boolean drawDebug = false;
	
	// FIXME TEMP
	private static int counter = 0;

	/**
	 * Initializes the physics engine
	 */
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

		// see the PhysicsDebugDrawer class
		dynamicsWorld.setDebugDrawer(new PhysicsDebugDrawer());
		
		Physics.dynamicsWorld.setInternalTickCallback(new DynamicEntityCallback(), null);
	}

	/**
	 * Updates the dynamics world
	 */
	public static void update() {
		dynamicsWorld.stepSimulation(getDeltaTimeMicroseconds() / 1000000.0f,
				SUBSTEPS);

		// handle the physics debug key
		if (KeyBindings.SYS_DEBUG_PHYSICS.isPressed() && !debugDown) {
			drawDebug = !drawDebug;
			debugDown = true;
		}
		if (!KeyBindings.SYS_DEBUG_PHYSICS.isPressed()) {
			debugDown = false;
		}
		
		
		
		/* FIXME TEMP CODE */
		/*
		ClosestRayResultCallback callback = Entities.player.rayTest(new org.lwjgl.util.vector.Vector3f(0.0f, 0.0f, Render3D.drawDistance));
		if(callback.hasHit()){
			CollisionObject obj = callback.collisionObject;
			DynamicEntity ent = (DynamicEntity)obj.getUserPointer();
			Console.console.print(ent.type + " " + counter);
			counter++;
		}*/
		
		if(!Mouse.isGrabbed()){
			DynamicEntity underCursor = Entities.camera.rayTestAtCursor();
			if(underCursor != null){
				Console.console.print(underCursor.type + " " + counter);
				counter++;
			}
		}
		/* FIXME TEMP CODE */
	}

	/**
	 * Cleans up all the physics stuff
	 */
	public static void cleanup() {
		dynamicsWorld.destroy();
		dynamicsWorld = null;
		broadphase = null;
		dispatcher = null;
		solver = null;

	}

	/**
	 * @return Change in time in microseconds
	 */
	private static float getDeltaTimeMicroseconds() {
		float delta = clock.getTimeMicroseconds();
		clock.reset();
		return delta;
	}
}