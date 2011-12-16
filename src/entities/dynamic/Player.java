package entities.dynamic;

import entities.Entities;
import gui.GUI;

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
	public float xAccel = 100.0f;
	/** how fast the player acclerates along the X axis */
	public float yAccel = 100.0f;
	/** how fast the player acclerates along the X axis */
	public float zAccel = 100.0f;

	/** how fast the player's bullet go */
	public float bulletSpeed = 250.0f;

	/** to keep the button from being held down */
	private boolean button0Down = false;

	/** how fast the player stabilizes when the stabilize key is pressed */
	public float stabilizationSpeed = 40.0f;

	/** how fast the player stops when the stop key is pressed */
	public float stopSpeed = 40.0f;

	/** how fast the player can roll */
	public float rollSpeed = 2500.0f;

	/** how fast the player can turn */
	public float turnSpeed = 5.0f;

	public Player(Vector3f location, Quaternion rotation, int model,
			float mass, float restitution) {
		super(location, rotation, model, mass, restitution);
		// make sure the rigid body doesn't deactivate
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		this.type = "player";
	}

	@Override
	public void update() {
		super.update();
		// only update if we're not paused, a menu isn't up and the camera's not
		// in free mode
		if (!Runner.paused && !GUI.menuUp && !Entities.camera.freeMode) {
			// check to make sure the rigid body is active
			if (!rigidBody.isActive())
				rigidBody.activate();

			// perform acceleration
			zLogic();
			xLogic();
			yLogic();

			// perform rotation
			rotationLogic();

			// handle bullet shooting
			if (MouseManager.button0 && !button0Down && !Debug.consoleOn) {
				button0Down = true;
				shootBullet();
			}
			if (!MouseManager.button0)
				button0Down = false;

			// handle stabilization
			if (KeyboardManager.stabilize)
				stabilize();

			// handle stopping
			if (KeyboardManager.stop)
				stop();
		}
	}

	/**
	 * Gracefully stops the player
	 */
	private void stop() {
		javax.vecmath.Vector3f linearVelocity = new javax.vecmath.Vector3f(
				0.0f, 0.0f, 0.0f);
		rigidBody.getLinearVelocity(linearVelocity);

		float stopX = linearVelocity.x - (linearVelocity.x / stopSpeed);
		float stopY = linearVelocity.y - (linearVelocity.y / stopSpeed);
		float stopZ = linearVelocity.z - (linearVelocity.z / stopSpeed);

		rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(stopX, stopY,
				stopZ));
	}

	/**
	 * Gracefullt stabilizes the player's angular velocity
	 */
	private void stabilize() {
		javax.vecmath.Vector3f angularVelocity = new javax.vecmath.Vector3f(
				0.0f, 0.0f, 0.0f);
		rigidBody.getAngularVelocity(angularVelocity);
		// TODO make this framerate independent
		float stableZ = angularVelocity.z
				- (angularVelocity.z / stabilizationSpeed);
		float stableX = angularVelocity.x
				- (angularVelocity.x / stabilizationSpeed);
		float stableY = angularVelocity.y
				- (angularVelocity.y / stabilizationSpeed);

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
		Entities.addBuffer.add(bullet);

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
	private void zLogic() {
		boolean forward = KeyboardManager.forward;
		boolean backward = KeyboardManager.backward;

		if (forward || backward) {
			if (forward) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, 0.0f, zAccel), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (backward) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, 0.0f, -zAccel), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}

	/**
	 * Accelerate/decelerate along the X axis
	 */
	private void xLogic() {
		boolean left = KeyboardManager.left;
		boolean right = KeyboardManager.right;

		if (left || right) {
			if (left) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(xAccel, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (right) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(-xAccel, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}

	/**
	 * Accelerate/decelerate along the Y axis
	 */
	private void yLogic() {
		boolean ascend = KeyboardManager.ascend;
		boolean descend = KeyboardManager.descend;

		if (ascend || descend) {
			if (ascend) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, -yAccel, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (descend) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, yAccel, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}

	/**
	 * Rotate based on mouse movement and input keys
	 */
	private void rotationLogic() {
		if (!Entities.camera.vanityMode) {
			// apply any torque along the X axis
			Vector3f xTorque = new Vector3f(-MouseManager.dy * turnSpeed, 0.0f,
					0.0f);
			xTorque = QuaternionHelper.rotateVectorByQuaternion(xTorque,
					rotation);
			rigidBody.applyTorqueImpulse(new javax.vecmath.Vector3f(xTorque.x,
					xTorque.y, xTorque.z));

			// apply any torque along the Y axis
			Vector3f yTorque = new Vector3f(0.0f, MouseManager.dx * turnSpeed,
					0.0f);
			yTorque = QuaternionHelper.rotateVectorByQuaternion(yTorque,
					rotation);
			rigidBody.applyTorqueImpulse(new javax.vecmath.Vector3f(yTorque.x,
					yTorque.y, yTorque.z));

			// check if we need to apply torque on the Z axis
			boolean rollRight = KeyboardManager.rollRight;
			boolean rollLeft = KeyboardManager.rollLeft;

			// handle applying torque on the Z axis
			if (rollRight || rollLeft) {
				Vector3f zTorque;
				if (rollRight)
					zTorque = new Vector3f(0.0f, 0.0f, -rollSpeed);
				else
					zTorque = new Vector3f(0.0f, 0.0f, rollSpeed);
				zTorque = QuaternionHelper.rotateVectorByQuaternion(zTorque,
						rotation);
				rigidBody.applyTorqueImpulse(new javax.vecmath.Vector3f(
						zTorque.x, zTorque.y, zTorque.z));
			}
		}
	}
}
