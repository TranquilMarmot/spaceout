package physics;

import java.nio.ByteBuffer;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Physics {
	public static void initPhysics(){
		//broadphase interface
		BroadphaseInterface broadphase = new DbvtBroadphase();
		
		// collisiong configuration and dispatcher
		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		
		// wut
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		// the world everything is in
		DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,broadphase,solver,collisionConfiguration);
		
		// no gravity, we're in space!
		dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
		
		BvhTriangleMeshShape shipShape = new BvhTriangleMeshShape();
		
		// TODO figure out how to get the model into this thing!
		
		/*
		 * need to make a BvhTriangleMeshShape, which requires a TriangleIndexVertexArray, which require an IndexedMesh.
		 * According to the source for IndexedMesh, the ScalarType indexType doesn't need to be set
		 * (it's set when the IndexedMesh is passed to the TriangleVertexArray)
		 * Create a ByteBuffer for all the vertices, and another one for all the indices
		 * The stride will be 4, because thats how many bytes a float takes up (maybe not, look into that)
		 */
		ByteBuffer b = BufferUtils.createByteBuffer(100);
		
		CollisionShape shipCollisionShape = new BvhTriangleMeshShape();
		
		// transformation for the ship
		Transform trans = new Transform();
		trans.setRotation(new Quat4f(0.0f, 0.0f, 0.0f, 1.0f));
		
		// motion state of the ship
		DefaultMotionState shipMotionState = new DefaultMotionState(trans);
		
		// the ship's rigid body info
		RigidBodyConstructionInfo shipRigidBodyInfo = new RigidBodyConstructionInfo(100.0f, shipMotionState, shipCollisionShape);
		
		// the ship's rigid body
		RigidBody shipRigidBody = new RigidBody(shipRigidBodyInfo);
		
		// add the rigid body to the world
		dynamicsWorld.addRigidBody(shipRigidBody);
		
		// 300 steps
		for(int i = 0; i < 300; i++){
			// pretty sure 1/60 means 60 fps
			dynamicsWorld.stepSimulation(1.0f/60.0f, 10);
			
			// get the ship's transformation
			Transform transfo = null;
			shipRigidBody.getMotionState().getWorldTransform(transfo);
			
			// this can be used to get the ship's location
			Vector3f shipLocation = transfo.origin;
			
			// this is the ship's rotation
			Quat4f shipRotation = null;
			transfo.getRotation(shipRotation);
		}
	}
}