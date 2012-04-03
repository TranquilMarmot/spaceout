package com.bitwaffle.spaceout.entities.dynamic;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.particles.trail.Trail;
import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.input.KeyBindings;
import com.bitwaffle.spaceguts.input.MouseManager;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceguts.util.Runner;
import com.bitwaffle.spaceguts.util.console.Console;
import com.bitwaffle.spaceout.interfaces.Health;
import com.bitwaffle.spaceout.resources.Models;
import com.bitwaffle.spaceout.resources.Textures;
import com.bitwaffle.spaceout.ship.Ship;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.Transform;

/**
 * The player!
 * 
 * @author TranquilMarmot
 */
public class Player extends DynamicEntity implements Health {
	final static short COL_GROUP = CollisionTypes.SHIP;
	final static short COL_WITH = (short)(CollisionTypes.WALL | CollisionTypes.PLANET);
	
	private Ship ship;
	
	// FIXME temp code
	private Trail trail1, trail2;

	/** to keep the button from being held down */
	private boolean button0Down = false, boosting = false;

	public Player(Vector3f location, Quaternion rotation, Ship ship,
			float mass, float restitution) {
		super(location, rotation, ship.getModel(), mass, restitution, COL_GROUP, COL_WITH);
		// make sure the rigid body doesn't deactivate
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		this.ship = ship;
		this.type = "Player";

		// FIXME temp code
		trail1 = new Trail(this, 15, 0.6f, Textures.TRAIL, new Vector3f(0.9f, 0.13f, 2.34f));
		trail2 = new Trail(this, 15, 0.6f, Textures.TRAIL, new Vector3f(-0.8f, 0.13f, 2.34f));
	}

	@Override
	/**
	 * This is called for every dynamic entity at the end of each tick of the physics world
	 */
	public void update(float timeStep) {
		// only update if we're not paused
		if(!Runner.paused){
			//FIXME temp code
			trail1.update(timeStep);
			trail2.update(timeStep);
			
			// only update if a menu isn't up and we're not in free mode
			if(!GUI.menuUp && !Entities.camera.freeMode){
				// check to make sure the rigid body is active
				if (!rigidBody.isActive())
					rigidBody.activate();
				
				if(KeyBindings.CONTROL_BOOST.isPressed())
					boosting = true;
				else
					boosting = false;

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

				// handle stopping
				if (KeyBindings.CONTROL_STOP.isPressed())
					stop(timeStep);
			}
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
	 * Pew pew
	 */
	private void shootBullet() {
		Vector3f bulletLocation = new Vector3f(this.location.x,
				this.location.y, this.location.z);
		Quaternion bulletRotation = new Quaternion(this.rotation.x,
				this.rotation.y, this.rotation.z, this.rotation.w);

		// move the bullet to in front of the player
		// FIXME this should be an offset from the center to represent the location of a gun or something
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
				Vector3f vec;
				if(boosting)
					vec = QuaternionHelper.rotateVectorByQuaternion(
							new Vector3f(0.0f, 0.0f, ship.getBoostSpeed().z * timeStep), rotation);
				else
					vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, 0.0f, ship.getAccelerationSpeed().z * timeStep), rotation);
				
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (backward) {
				Vector3f vec;
				if(boosting)
					vec = QuaternionHelper.rotateVectorByQuaternion(
							new Vector3f(0.0f, 0.0f, -ship.getBoostSpeed().z * timeStep), rotation);
				else
					vec = QuaternionHelper.rotateVectorByQuaternion(
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
				Vector3f vec;
				if(boosting)
					vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(ship.getBoostSpeed().x * timeStep, 0.0f, 0.0f), rotation);
				else
					vec = QuaternionHelper.rotateVectorByQuaternion(
							new Vector3f(ship.getAccelerationSpeed().x * timeStep, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (right) {
				Vector3f vec;
				if(boosting)
					vec = QuaternionHelper.rotateVectorByQuaternion(
							new Vector3f(-ship.getBoostSpeed().x * timeStep, 0.0f, 0.0f), rotation);
				else
					vec = QuaternionHelper.rotateVectorByQuaternion(
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
				Vector3f vec;
				if(boosting)
					vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, -ship.getBoostSpeed().y * timeStep, 0.0f), rotation);
				else
					vec = QuaternionHelper.rotateVectorByQuaternion(
							new Vector3f(0.0f, -ship.getAccelerationSpeed().y * timeStep, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x,
						vec.y, vec.z));
			}
			if (descend) {
				Vector3f vec;
				if(boosting)
					vec = QuaternionHelper.rotateVectorByQuaternion(
						new Vector3f(0.0f, ship.getBoostSpeed().y * timeStep, 0.0f), rotation);
				else
					vec = QuaternionHelper.rotateVectorByQuaternion(
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
	
	/**
	 * Uses spherical linear interpolation to rotate the ship towards where the camera is looking
	 * @param timeStep
	 */
	private void rotationLogic(float timeStep){
		javax.vecmath.Vector3f angVec = new javax.vecmath.Vector3f();
		this.rigidBody.getAngularVelocity(angVec);
		float currentAngularVelocity = angVec.length();
		
		// if the mouse has moved, set the angular velocity to zero (to prevent spinning out of control)
		if(MouseManager.dx != 0.0f && MouseManager.dy != 0.0f && currentAngularVelocity != 0)
			this.rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
		
		// only interpolate values if the angular velocity is 0 (we're NOT spinning out of control) and the two rotations aren't already equal (dot product == 1 if the rotations are the same)
		if(currentAngularVelocity == 0 && Quaternion.dot(this.rotation, Entities.camera.rotation) != 1.0f){
			Quat4f camquat = new Quat4f(Entities.camera.rotation.x, Entities.camera.rotation.y, Entities.camera.rotation.z, Entities.camera.rotation.w);
			Quat4f thisquat = new Quat4f(rotation.x, rotation.y, rotation.z, rotation.w);
			
			float interpolationAmount = timeStep * ship.getTurnSpeed();
			
			// Slerp magix!
			thisquat.interpolate(camquat, thisquat, interpolationAmount);
			
			// set current rotation
			this.rotation.set(thisquat.x, thisquat.y, thisquat.z, thisquat.w);
			
			// set world transform to represent interpolated value
			Transform trans = new Transform();
			this.rigidBody.getWorldTransform(trans);
			trans.setRotation(thisquat);
			this.rigidBody.setWorldTransform(trans);
		}
	}
	
	@Override
	public void draw(){
		super.draw();
		//FIXME temp code
		trail1.draw();
		trail2.draw();
	}

	@Override
	public int getCurrentHealth() {
		return 0;
	}

	@Override
	public void hurt(int amount) {
		
	}

	@Override
	public void heal(int amount) {
		
	}
}
