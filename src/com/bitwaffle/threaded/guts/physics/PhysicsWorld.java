package com.bitwaffle.threaded.guts.physics;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;


public class PhysicsWorld {

	private Vector3f gravity;
	private int substeps;

	private DiscreteDynamicsWorld dynamicsWorld;
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private SequentialImpulseConstraintSolver solver;
	private Clock clock;

	public PhysicsWorld(Vector3f gravity, InternalTickCallback callback){
		this(gravity, 10, callback);
	}


	public PhysicsWorld(Vector3f gravity, int substeps, InternalTickCallback callback){
		this.gravity = gravity;
		this.substeps = substeps;

		clock = new Clock();

		broadphase = new DbvtBroadphase();
		
		DefaultCollisionConfiguration collisionConfig = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfig);

		solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);

		dynamicsWorld.setGravity(gravity);

		// TODO debug drawer

		dynamicsWorld.setInternalTickCallback(callback, null);
	}

	public void update(){
		// TODO have this done during the tick, might be a good idea to have a boolean that says whether or not there's gravity so that the length doesn't have to be computed every tick
		if(gravity.length() != 0)
			dynamicsWorld.applyGravity();
		
		dynamicsWorld.stepSimulation(getDeltaTimeMicroseconds() / 1000000.0f, substeps);
	}

	private float getDeltaTimeMicroseconds(){
		float delta = clock.getTimeMicroseconds();
		clock.reset();
		return delta;
	}

	public Vector3f getGravity(){
		return gravity;
	}

	public void setGravity(Vector3f newGravity){
		dynamicsWorld.setGravity(newGravity);
		this.gravity = newGravity;
	}

	
	public int numSubsteps(){
		return substeps;
	}

	public void setNumSubsteps(int newNum){
		this.substeps = newNum;
	}

	public void cleanup(){
		dynamicsWorld.destroy();
		dynamicsWorld = null;
		broadphase = null;
		dispatcher = null;
		solver = null;
	}
} 
