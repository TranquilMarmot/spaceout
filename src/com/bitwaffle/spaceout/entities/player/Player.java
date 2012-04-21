package com.bitwaffle.spaceout.entities.player;

import java.util.ArrayList;

import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.audio.SoundSource;
import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Pickup;
import com.bitwaffle.spaceguts.entities.particles.trail.Trail;
import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.graphics.shapes.Box2D;
import com.bitwaffle.spaceguts.input.KeyBindings;
import com.bitwaffle.spaceguts.input.MouseManager;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.physics.ConvexResultCallback;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceguts.util.Runner;
import com.bitwaffle.spaceguts.util.console.Console;
import com.bitwaffle.spaceout.entities.dynamic.LaserBullet;
import com.bitwaffle.spaceout.entities.dynamic.Missile;
import com.bitwaffle.spaceout.entities.dynamic.Planet;
import com.bitwaffle.spaceout.interfaces.Health;
import com.bitwaffle.spaceout.interfaces.Inventory;
import com.bitwaffle.spaceout.resources.Models;
import com.bitwaffle.spaceout.resources.Sounds;
import com.bitwaffle.spaceout.resources.Textures;
import com.bitwaffle.spaceout.ship.Ship;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * The player!
 * 
 * @author TranquilMarmot
 */
public class Player extends DynamicEntity implements Health, Inventory{
	final static short COL_GROUP = CollisionTypes.SHIP;
	final static short COL_WITH = (short)(CollisionTypes.WALL | CollisionTypes.PLANET);
	
	/** Used for drawing the lockon target thing */
	private static Box2D lockonbox = new Box2D(1.0f, 1.0f, Textures.TARGET.texture());
	
	/** Used for searching for lockon stuff*/
	private BoxShape lockonSweepBox = new BoxShape(new javax.vecmath.Vector3f(5.0f, 5.0f, 5.0f));
	
	private SoundSource pew;
	
	/** Radius for sphere that looks for pickups */
	public float pickupSweepSize = 25.0f;
	/** How far in front of the ship that sphere travels */
	public float pickupSweepDistance = 10.0f;
	/** How close a pickup has to be before it's added to the inventory */
	public float pickupDistance = 2.0f;
	
	/**
	 * Backpack, backpack. Backpack, backpack.
	 * I'm the Backpack.
	 * Loaded up with things and nick-nacks too.
	 * Anything that you might need I got inside for you.
	 * Backpack, backpack. Backpack, backpack.
	 * YEAH!
	 */
	public Backpack backpack;
	
	/** Used for info in equations */
	private Ship ship;
	
	// FIXME temp code
	private Trail trail1, trail2;

	/** to keep the button from being held down */
	private boolean button0Down = false, button1Down = false, boosting = false;
	
	public DynamicEntity lockon = null;

	public Player(Vector3f location, Quaternion rotation, Ship ship,
			float mass, float restitution) {
		super(location, rotation, ship.getModel(), mass, restitution, COL_GROUP, COL_WITH);
		// make sure the rigid body doesn't deactivate
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		this.ship = ship;
		this.type = "Player";
		
		backpack = new Backpack();

		// FIXME temp code
		trail1 = new Trail(this, 15, 0.6f, Textures.TRAIL, new Vector3f(0.9f, 0.13f, 2.34f));
		trail2 = new Trail(this, 15, 0.6f, Textures.TRAIL, new Vector3f(-0.8f, 0.13f, 2.34f));
		
		pew = new SoundSource(Sounds.PEW, false, this.location, new Vector3f(0.0f,0.0f,0.0f));
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
				
				if(MouseManager.button1 && !button1Down && !Console.consoleOn){
					button1Down = true;
					shootMissile();
				}
				if(!MouseManager.button1)
					button1Down = false;

				// handle stopping
				if (KeyBindings.CONTROL_STOP.isPressed())
					brake(timeStep);
				
				checkForPickups();
				lockOn();
			}
		}
	}

	/**
	 * Gracefully stops the player
	 */
	private void brake(float timeStep) {
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
				bulletModel, bulletMass, bulletRestitution, bulletDamage, bulletSpeed);
		Entities.addDynamicEntity(bullet);
		
		pew.setLocation(Entities.camera.location);
		javax.vecmath.Vector3f linvec = new javax.vecmath.Vector3f();
		this.rigidBody.getLinearVelocity(linvec);
		pew.setVelocity(new Vector3f(linvec.x, linvec.y, linvec.z));
		pew.playSound();
	}
	
	/**
	 * Ker-pow
	 */
	private void shootMissile() {
		Vector3f missileLocation = new Vector3f(this.location.x,
				this.location.y, this.location.z);
		Quaternion missileRotation = new Quaternion(this.rotation.x,
				this.rotation.y, this.rotation.z, this.rotation.w);

		// move the missile to in front of the player
		// FIXME this should be an offset from the center to represent the location of a gun or something
		Vector3f missileMoveAmount = new Vector3f(0.0f, 0.0f, 10.0f);
		missileMoveAmount = QuaternionHelper.rotateVectorByQuaternion(
				missileMoveAmount, missileRotation);
		Vector3f.add(missileLocation, missileMoveAmount, missileLocation);
		
		Missile miss = new Missile(missileLocation, missileRotation, lockon, 75.0f, 50.0f, 12.0f);

		Entities.addDynamicEntity(miss);
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
	 * Uses spherical linear interpolation (slerp) to rotate the ship towards where the camera is looking
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
	
	/**
	 * Searches for things to lock on to
	 */
	private void lockOn(){
		ArrayList<Planet> hits = new ArrayList<Planet>();
		ConvexResultCallback<Planet> callback = new ConvexResultCallback<Planet>(hits, CollisionTypes.PLANET);
		
		Physics.convexSweepTest(this, new Vector3f(0.0f, 0.0f, 500.0f), lockonSweepBox, callback);
		
		if(hits.size() > 0)
			this.lockon = hits.get(0);
		
		// un-lock on to something if it's being removed
		if(lockon != null && lockon.removeFlag)
			lockon = null;
	}
	
	/**
	 * Performs a convex sweep test in front of the player and sets any found pickups
	 * to follow the player.
	 */
	private void checkForPickups(){
		// we'll use a sphere for simplicity
		SphereShape shape = new SphereShape(pickupSweepSize);
		
		/*
		 *  See ConvexResultCallback class
		 *  Any pickups found from the sweep test are added to hits
		 *  It is possible to do multiple tests and have them all add to the same list,
		 *  but there's no guarantee that there won't be duplicates so be careful
		 *  (Would be possible to get a list containing no duplicates if, say, a 
		 *  hash map is used)
		 */
		ArrayList<Pickup> hits = new ArrayList<Pickup>();
		ConvexResultCallback<Pickup> callback = new ConvexResultCallback<Pickup>(hits, CollisionTypes.PICKUP);
		
		Physics.convexSweepTest(this, new Vector3f(0.0f, 0.0f, pickupSweepDistance), shape, callback);
		
		// make found pickups follow player
		for(Pickup item : hits)
				item.setFollowing(this, pickupDistance, backpack);
	}
	
	/**
	 * I'M RICH, BIATCH
	 * @return how many diamonds the player has in inventory
	 */
	// FIXME this is probably temporary
	public int howManyDiamonds(){
		int num = 0;
		
		for(Pickup p : backpack.getItems()){
			if(p.type.equals("Diamond"))
				num++;
		}
		
		return num;
	}
	
	@Override
	public void draw(){
		super.draw();
		//FIXME temp code
		trail1.draw();
		trail2.draw();
		if(lockon != null)
			drawTarget();
	}
	
	/**
	 * Draws a target over what the player is locked on to
	 */
	private void drawTarget(){
		// enable blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		// otherwise, we wouldn't be able to see the target
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Render3D.program.setUniform("Light.LightEnabled", false);
		
		Textures.TARGET.texture().bind();
		
		// save the modelview matrix so we can muck wit it
		Matrix4f oldModelView = new Matrix4f();
		oldModelView.load(Render3D.modelview);
		
		// undo rotation (modelview is currently rotated to draw player)
		Quaternion revQuat = new Quaternion();
		this.rotation.negate(revQuat);
		Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(revQuat), Render3D.modelview);
		
		// new translation
		float transx = location.x - lockon.location.x;
		float transy = location.y - lockon.location.y;
		float transz = location.z - lockon.location.z;
		
		// translate and scale the modelview
		Render3D.modelview.translate(new Vector3f(transx, transy, transz));
		// billboard the target
		Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(Entities.camera.rotation), Render3D.modelview);
		
		// make it bigger!
		Render3D.modelview.scale(new Vector3f(10.0f, 10.0f, 1.0f));
		
		// don't forget to set the modelview before drawing (d'oh!)
		Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);
		
		lockonbox.draw();
		
		// reset everything to the way it was
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		Render3D.modelview.load(oldModelView);
		GL11.glDisable(GL11.GL_BLEND);
		Render3D.program.setUniform("Light.LightEnabled", true);
		Render3D.modelview.load(oldModelView);
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

	@Override
	public void addInventoryItem(Pickup item) {
		backpack.addInventoryItem(item);
	}

	@Override
	public void removeInventoryItem(Pickup item) {
		backpack.removeInventoryItem(item);
	}

	@Override
	public ArrayList<Pickup> getItems() {
		return backpack.getItems();
	}
}
