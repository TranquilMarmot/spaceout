package entities.dynamic;

import entities.Entities;
import graphics.gui.GUI;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.Runner;
import util.debug.Debug;
import util.helper.QuaternionHelper;
import util.manager.KeyboardManager;
import util.manager.ModelManager;
import util.manager.MouseManager;

import com.bulletphysics.collision.dispatch.CollisionObject;

/**
 * The player!
 * 
 * @author TranquilMarmot
 */
public class Player extends DynamicEntity {
	/** how fast the player acclerates along the X axis */
	public float xAccel = 1000.0f;
	/** how fast the player acclerates along the X axis */
	public float yAccel = 1000.0f;
	/** how fast the player acclerates along the X axis */
	public float zAccel = 1000.0f;
	
	public float maxSpeed = 200.0f;

	/** how fast the player's bullet go */
	public float bulletSpeed = 250.0f;

	/** to keep the button from being held down */
	private boolean button0Down = false;

	/** how fast the player stabilizes when the stabilize key is pressed */
	public float stabilizationSpeed = 0.5f;

	/** how fast the player stops when the stop key is pressed */
	public float stopSpeed = 1.0f;

	/** how fast the player can roll */
	public float rollSpeed = 0.5f;

	/** how fast the player can turn */
	public float turnSpeed = 0.0025f;

	public Player(Vector3f location, Quaternion rotation, int model,
			float mass, float restitution) {
		super(location, rotation, model, mass, restitution);
		// make sure the rigid body doesn't deactivate
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		this.type = "player";
	}

	@Override
	/**
	 * This is called for every dynamic entity at the end of each tick of the physics world
	 */
	public void update(float timeStep) {
		// only update if we're not paused, a menu isn't up and the camera's not
		// in free mode
		if (!Runner.paused && !GUI.menuUp && !Entities.camera.freeMode) {
			// check to make sure the rigid body is active
			if (!rigidBody.isActive())
				rigidBody.activate();

			javax.vecmath.Vector3f speed = new javax.vecmath.Vector3f();
			rigidBody.getLinearVelocity(speed);
			
			if(speed.x + speed.y + speed.z < maxSpeed){
			// perform acceleration
			zLogic(timeStep);
			xLogic(timeStep);
			yLogic(timeStep);
			}
			
			// cap the players' speed
			checkSpeed();

			// perform rotation
			if(!Entities.camera.vanityMode)
				rotationLogic(timeStep);

			// handle bullet shooting
			if (MouseManager.button0 && !button0Down && !Debug.consoleOn) {
				button0Down = true;
				shootBullet();
			}
			if (!MouseManager.button0)
				button0Down = false;

			// handle stabilization
			if (KeyboardManager.stabilize)
				stabilize(timeStep);

			// handle stopping
			if (KeyboardManager.stop)
				stop(timeStep);
		}
	}

	/**
	 * Gracefully stops the player
	 */
	private void stop(float timeStep) {
		javax.vecmath.Vector3f linearVelocity = new javax.vecmath.Vector3f(
				0.0f, 0.0f, 0.0f);
		rigidBody.getLinearVelocity(linearVelocity);

		float stopX = linearVelocity.x - ((linearVelocity.x / stopSpeed) * timeStep);
		float stopY = linearVelocity.y - ((linearVelocity.y / stopSpeed) * timeStep);
		float stopZ = linearVelocity.z - ((linearVelocity.z / stopSpeed) * timeStep);

		rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(stopX, stopY,
				stopZ));
	}

	/**
	 * Gracefullt stabilizes the player's angular velocity
	 */
	private void stabilize(float timeStep) {
		javax.vecmath.Vector3f angularVelocity = new javax.vecmath.Vector3f(
				0.0f, 0.0f, 0.0f);
		rigidBody.getAngularVelocity(angularVelocity);
		// TODO make this framerate independent
		float stableZ = angularVelocity.z
				- ((angularVelocity.z / stabilizationSpeed) * timeStep);
		float stableX = angularVelocity.x
				- ((angularVelocity.x / stabilizationSpeed) * timeStep);
		float stableY = angularVelocity.y
				- ((angularVelocity.y / stabilizationSpeed) * timeStep);

		rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(stableX,
				stableY, stableZ));
	}

	/**
	 * Pew pew
	 */
	private void shootBullet() {
		Vector3f bulletLocation = new Vector3f(this.location.x,
				this.location.y, this.location.z);
		Quaternion bulletRotation = new Quaternion(this.rotation.x,
				this.rotation.y, this.rotation.z, this.rotation.w);

		// move the bullet to in front of the player so it doesn't it the player
		Vector3f bulletMoveAmount = new Vector3f(0.0f, 0.0f, 10.0f);
		bulletMoveAmount = QuaternionHelper.rotateVectorByQuaternion(
				bulletMoveAmount, bulletRotation);
		Vector3f.add(bulletLocation, bulletMoveAmount, bulletLocation);

		int bulletModel = ModelManager.LASERBULLET;
		float bulletMass = 10.0f;
		float bulletRestitution = 1.0f;

		LaserBullet bullet = new LaserBullet(bulletLocation, bulletRotation,
				bulletModel, bulletMass, bulletRestitution);
		bullet.type = "bullet";
		Entities.dynamicAddBuffer.add(bullet);

		// give the bullet some speed
		javax.vecmath.Vector3f currentVelocity = new javax.vecmath.Vector3f();
		rigidBody.getInterpolationLinearVelocity(currentVelocity);
		Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(
				0.0f, 0.0f, bulletSpeed), rotation);
		bullet.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(vec.x,
				vec.y, vec.z));
	}

	/**
	 * Accelerate/decelerate along the Z axis
	 */
	private void zLogic(float timeStep) {
		boolean forward = KeyboardManager.forward;
		boolean backward = KeyboardManager.backward;

		if (forward || backward) {
			if (forward) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, 0.0f, zAccel * timeStep), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (backward) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, 0.0f, -zAccel * timeStep), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}

	/**
	 * Accelerate/decelerate along the X axis
	 */
	private void xLogic(float timeStep) {
		boolean left = KeyboardManager.left;
		boolean right = KeyboardManager.right;

		if (left || right) {
			if (left) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(xAccel * timeStep, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (right) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(-xAccel * timeStep, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}

	/**
	 * Accelerate/decelerate along the Y axis
	 */
	private void yLogic(float timeStep) {
		boolean ascend = KeyboardManager.ascend;
		boolean descend = KeyboardManager.descend;

		if (ascend || descend) {
			if (ascend) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, -yAccel * timeStep, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (descend) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, yAccel * timeStep, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}
	
	private void checkSpeed(){
		javax.vecmath.Vector3f speed = new javax.vecmath.Vector3f();
		rigidBody.getLinearVelocity(speed);
		
		javax.vecmath.Vector3f result = new javax.vecmath.Vector3f(speed.x, speed.y, speed.z);
		
		/*
		if(speed.x > maxX)
			result.x = maxX;
		else if(speed.x < -maxX)
			result.x = -maxX;
		
		if(speed.y > maxY)
			result.y = maxY;
		else if(speed.y < -maxY)
			result.y = -maxY;
		
		if(speed.z > maxZ)
			result.z = maxZ;
		else if(speed.z < -maxZ)
			result.z = -maxZ;
			*/
		
		
			
		
		if((result.x != speed.x) || (result.y != speed.y) || (result.z != speed.z))
			rigidBody.setLinearVelocity(result);
	}
	
	private void rotationLogic(float timeStep){
		// TODO very impotant!!! make this framerate independent
		javax.vecmath.Vector3f angularVelocity = new javax.vecmath.Vector3f();
		rigidBody.getAngularVelocity(angularVelocity);
		
		float xRot = MouseManager.dy * turnSpeed;
		float yRot = MouseManager.dx * turnSpeed;
		
		float zRot = 0.0f;
		// check if we need to apply torque on the Z axis
		boolean rollRight = KeyboardManager.rollRight;
		boolean rollLeft = KeyboardManager.rollLeft;

		// handle applying torque on the Z axis
		if (rollRight || rollLeft) {
			if (rollRight)
				zRot = -rollSpeed * timeStep;
			else
				zRot = rollSpeed * timeStep;
		}
		
		Vector3f torque = new Vector3f(xRot, yRot, zRot);
		
		torque = QuaternionHelper.rotateVectorByQuaternion(torque, rotation);
		
		angularVelocity.add(new javax.vecmath.Vector3f(torque.x, torque.y, torque.z));
		
		rigidBody.setAngularVelocity(angularVelocity);
	}
}
