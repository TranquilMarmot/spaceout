package spaceout.entities.dynamic;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entities;
import spaceguts.graphics.gui.GUI;
import spaceguts.physics.CollisionTypes;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.Runner;
import spaceguts.util.console.Console;
import spaceguts.util.input.KeyBindings;
import spaceguts.util.input.MouseManager;
import spaceguts.util.resources.Models;
import spaceout.interfaces.Health;
import spaceout.ship.Ship;

import com.bulletphysics.collision.dispatch.CollisionObject;

/**
 * The player!
 * 
 * @author TranquilMarmot
 */
public class Player extends DynamicEntity implements Health {
	final static short COL_GROUP = CollisionTypes.SHIP;
	final static short COL_WITH = (short)(CollisionTypes.WALL | CollisionTypes.PLANET);
	
	private Ship ship;

	/** to keep the button from being held down */
	private boolean button0Down = false;

	public Player(Vector3f location, Quaternion rotation, Ship ship,
			float mass, float restitution) {
		super(location, rotation, ship.getModel(), mass, restitution, COL_GROUP, COL_WITH);
		// make sure the rigid body doesn't deactivate
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		this.ship = ship;
		this.type = "Player";
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
			
			//FIXME this if statement necessary?

			// perform acceleration
			zLogic(timeStep);
			xLogic(timeStep);
			yLogic(timeStep);
			
			
			// cap the players' speed
			checkSpeed();

			// perform rotation
			if(!Entities.camera.vanityMode)
				rotationLogic(timeStep);

			// handle bullet shooting
			if (MouseManager.button0 && !button0Down && !Console.consoleOn) {
				button0Down = true;
				shootBullet();
			}
			if (!MouseManager.button0)
				button0Down = false;

			// handle stabilization
			if (KeyBindings.CONTROL_STABILIZE.isPressed())
				stabilize(timeStep);

			// handle stopping
			if (KeyBindings.CONTROL_STOP.isPressed())
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

		float stopX = linearVelocity.x - ((linearVelocity.x / ship.getStopSpeed()) * timeStep);
		float stopY = linearVelocity.y - ((linearVelocity.y / ship.getStopSpeed()) * timeStep);
		float stopZ = linearVelocity.z - ((linearVelocity.z / ship.getStopSpeed()) * timeStep);

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
				- ((angularVelocity.z / ship.getStabilizationSpeed()) * timeStep);
		float stableX = angularVelocity.x
				- ((angularVelocity.x / ship.getStabilizationSpeed()) * timeStep);
		float stableY = angularVelocity.y
				- ((angularVelocity.y / ship.getStabilizationSpeed()) * timeStep);

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

		Models bulletModel = Models.LASERBULLET;
		float bulletMass = 0.25f;
		float bulletRestitution = 1.0f;
		int bulletDamage = 10;
		float bulletSpeed = 2500.0f;

		LaserBullet bullet = new LaserBullet(this, bulletLocation, bulletRotation,
				bulletModel, bulletMass, bulletRestitution, bulletDamage);
		Entities.addDynamicEntity(bullet);

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
		boolean forward = KeyBindings.CONTROL_FORWARD.isPressed();
		boolean backward = KeyBindings.CONTROL_BACKWARD.isPressed();

		if (forward || backward) {
			if (forward) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, 0.0f, ship.getAccelerationSpeed().z * timeStep), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (backward) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, 0.0f, -ship.getAccelerationSpeed().z * timeStep), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}

	/**
	 * Accelerate/decelerate along the X axis
	 */
	private void xLogic(float timeStep) {
		boolean left = KeyBindings.CONTROL_LEFT.isPressed();
		boolean right = KeyBindings.CONTROL_RIGHT.isPressed();

		if (left || right) {
			if (left) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(ship.getAccelerationSpeed().x * timeStep, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (right) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(-ship.getAccelerationSpeed().x * timeStep, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}

	/**
	 * Accelerate/decelerate along the Y axis
	 */
	private void yLogic(float timeStep) {
		boolean ascend = KeyBindings.CONTROL_ASCEND.isPressed();
		boolean descend = KeyBindings.CONTROL_DESCEND.isPressed();

		if (ascend || descend) {
			if (ascend) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, -ship.getAccelerationSpeed().y * timeStep, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (descend) {
				Vector3f vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, ship.getAccelerationSpeed().y * timeStep, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
		}
	}
	
	/**
	 * Keep the speed in range of this ship's top speed
	 */
	private void checkSpeed(){
	    javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f();
		rigidBody.getLinearVelocity(velocity);
		float speed = velocity.length();
		if(speed > ship.getTopSpeed()){
			velocity.x *= ship.getTopSpeed() / speed;
			velocity.y *= ship.getTopSpeed() / speed;
			velocity.z *= ship.getTopSpeed() / speed;
			rigidBody.setLinearVelocity(velocity);
		}
	}
	
	private void rotationLogic(float timeStep){
		// TODO very impotant!!! make this framerate independent
		javax.vecmath.Vector3f angularVelocity = new javax.vecmath.Vector3f();
		rigidBody.getAngularVelocity(angularVelocity);
		
		float xRot = MouseManager.dy * ship.getXTurnSpeed();
		float yRot = MouseManager.dx * ship.getYTurnSpeed();
		
		float zRot = 0.0f;
		// check if we need to apply torque on the Z axis
		boolean rollRight = KeyBindings.CONTROL_ROLL_RIGHT.isPressed();
		boolean rollLeft = KeyBindings.CONTROL_ROLL_LEFT.isPressed();

		// handle applying torque on the Z axis
		if (rollRight || rollLeft) {
			if (rollRight)
				zRot = -ship.getRollSpeed() * timeStep;
			else
				zRot = ship.getRollSpeed() * timeStep;
		}
		
		Vector3f torque = new Vector3f(xRot, yRot, zRot);
		
		torque = QuaternionHelper.rotateVectorByQuaternion(torque, rotation);
		
		angularVelocity.add(new javax.vecmath.Vector3f(torque.x, torque.y, torque.z));
		
		rigidBody.setAngularVelocity(angularVelocity);
	}

	@Override
	public int getCurrentHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void hurt(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heal(int amount) {
		// TODO Auto-generated method stub
		
	}
}
