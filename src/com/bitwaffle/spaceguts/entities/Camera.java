package com.bitwaffle.spaceguts.entities;

import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;


import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.input.KeyBindings;
import com.bitwaffle.spaceguts.input.MouseManager;
import com.bitwaffle.spaceguts.physics.Builder;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.DisplayHelper;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceguts.util.console.Console;
import com.bitwaffle.spaceout.resources.Textures;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * A camera that describes how the scene is being looked at and can move around inside of the physics world
 * There should only be one camera at a time, Entities.camera
 * 
 * @author TranquilMarmot
 */
public class Camera extends DynamicEntity {
	/** initial values for when the camera is created */
	private static final float INIT_ZOOM = 12.0f;
	private static final float INIT_XOFFSET = 0.0f;
	private static final float INIT_YOFFSET = -2.7F;

	/** the entity that the camera is following */
	public Entity following;
	
	/** handles special interactions with the game world */
	public Builder builder;

	/** how fast the camera moves in free mode */
	public float speed = 350.0f;
	private float maxSpeed = 10000.0f, minSpeed = 0.01f;

	/** Offset along Y axis */
	public float yOffset;
	/** Offset along X axis */
	public float xOffset;
	
	/** used to preserve offset when switching modes */
	private float oldYOffset, oldXOffset, oldMinZoom;

	/** zoom level */
	public float zoom;
	/** maximum and minimum zoom level */
	private float maxZoom = 3000.0f, minZoom = 10.0f;
	/** changes how fast the zoom is based on the current zoom distance */
	float zoomSensitivity = 10;
	private float oldZoom;
	
	/** for switching into/out of build mode */
	private Vector3f oldLocation = new Vector3f(0.0f, 0.0f, 0.0f);

	/**
	 * The camera has three main modes:
	 * 	Normal Mode - vanityMode = false, freeMode = false
	 * 		Camera simply follows behind whatever it's attached to
	 * 	Vanity Mode - vanityMode = true, freeMode = false
	 * 		Camera spins around whatever it's following
	 * 	Free Mode - vanityMode = false, freeMode = true
	 * 		Camera can move about freely on its own and zoom out real far
	 */
	// FIXME there's a more clever way to do this- it could easily be a short with each bit representing a mode- 00 would be normal, 01 would be vanity, 10 would be free, and 11 would be build
	public boolean vanityMode = false;
	public boolean freeMode = false;
	public boolean buildMode = false;
	
	/** how fast the camera rolls */
	float rollSpeed = 100.0f;
	
	// FIXME should the crosshair be its own class?
	public int crosshairWidth = 8, crosshairHeight = 8;
	public int handWidth = 15, handHeight = 17;
	private int currentCrosshairWidth = crosshairWidth, currentCrosshairHeight = crosshairHeight;
	private Textures currentCrosshair = Textures.CROSSHAIR;
	public Vector3f defaultCrosshairColor = new Vector3f(1.0f, 1.0f, 1.0f);

	/**
	 * Camera constructor
	 */
	public Camera() {
		// FIXME The camera can interfere with stuff, which shouldn't be the case (ideally, it would ignore everything except what it's bumping into)
		super(new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), new SphereShape(10.0f), 0.0001f, 0.01f, CollisionTypes.NOTHING, (short)-1);
		this.zoom = INIT_ZOOM;
		this.yOffset = INIT_YOFFSET;
		this.xOffset = INIT_XOFFSET;
		rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.type = "camera";
		builder = new Builder(this);
	}

	/**
	 * Update the camera. This handles following other things, mode switches etc.
	 */
	public void update(float timeStep) {
		// make sure the rigid body is active
		if(!this.rigidBody.isActive())
			this.rigidBody.activate();
		
		// get the rigid body's tranform
		Transform trans = new Transform();
		this.rigidBody.getWorldTransform(trans);
		
		// set location to be with rigid body
		this.location.set(trans.origin.x, trans.origin.y, trans.origin.z);
		
		// set rotation to be with rigid body
		Quat4f rot = new Quat4f();
		trans.getRotation(rot);
		this.rotation.set(rot.x, rot.y, rot.z, rot.w);
		
		if(buildMode)
			builder.update(timeStep);
		
		// if we're not following anything, we're in free mode
		if (following == null) {
			vanityMode = false;
			freeMode = true;
		}

		// perform zoom logic
		mousewheelLogic();

		// check for any key presses
		checkForModeSwitch();
		
		// only look around if the builder isn't rotating something
		if(!builder.rightGrabbed){
			lookLogic(timeStep, trans);
		}

		// if we're not in free mode, move the camera to be behind whatever it's
		// following
		if (!freeMode) {
			this.location.set(following.location);
			
			trans.origin.set(location.x, location.y, location.z);
		} else if (freeMode && !vanityMode) {
			// else do logic for moving around in free mode
			freeMode(timeStep, trans);
		}
		
		this.rigidBody.setWorldTransform(trans);
	}
	
	/**
	 * Changes where the camera is looking based on mouse movement/keyboard input
	 * @param timeStep Time since last update
	 * @param trans Transform to modify
	 */
	private void lookLogic(float timeStep, Transform trans){
		float dz = 0.0f;
		// roll left/right
		boolean rollRight = KeyBindings.CONTROL_ROLL_RIGHT.isPressed();
		boolean rollLeft = KeyBindings.CONTROL_ROLL_LEFT.isPressed();
		if (rollRight)
			dz = timeStep * rollSpeed;
		if (rollLeft)
			dz = timeStep * -rollSpeed;
		
		// apply any rotation changes
		this.rotation = QuaternionHelper.rotate(this.rotation, new Vector3f(MouseManager.dy, MouseManager.dx, dz));
		// update rigid body transform
		trans.setRotation(new Quat4f(rotation.x, rotation.y, rotation.z, rotation.w));
	}

	/**
	 * Checks if the mode change button has been pressed and changes states
	 * accordingly
	 */
	private void checkForModeSwitch() {
		// check for mode key
		if(!Console.consoleOn){
			if(!buildMode){
				// swap the camera mode
				if (KeyBindings.SYS_CAMERA_MODE.pressedOnce()) {
					// three states: normal (vanity false free false), vanity (vanity true
					// free false), free (vanity false free true)
					if (!vanityMode && !freeMode) {
						vanityMode = true;
						oldYOffset = yOffset;
						oldXOffset = xOffset;
						//oldMinZoom = minZoom;
						yOffset = 0;
						xOffset = 0;
						//minZoom = 0;
					} else if (vanityMode && !freeMode) {
						vanityMode = false;
						freeMode = true;
						oldMinZoom = minZoom;
						minZoom = 0;
					} else if (freeMode && !vanityMode) {
						freeMode = false;
						rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
						yOffset = oldYOffset;
						xOffset = oldXOffset;
						minZoom = oldMinZoom;
					}
				}
			}
			
			// go into/out of build mode
			if(KeyBindings.SYS_BUILD_MODE.pressedOnce()){
				buildMode = !buildMode;
				
				// going in to build mode
				if(buildMode){
					// save the zoom and set it to 0
					oldZoom = zoom;
					zoom = 0.0f;
					
					// go into free mode
					vanityMode = false;
					freeMode = true;
					
					// only save variables if they aren't 0 (otherwise they don't need to be changed)
					if(yOffset != 0){
						oldYOffset = yOffset;
						yOffset = 0;
					}
					
					if(xOffset != 0){
						oldXOffset = xOffset;
						xOffset = 0;
					}
					
					if(minZoom > 0){
						oldMinZoom = minZoom;
						minZoom = 0;
					}
					
					// save the camera's location and move it back a bit
					oldLocation.set(location);
					Vector3f.add(this.location, QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, -25.0f), this.rotation), this.location);
					
					Transform trans = new Transform();
					this.rigidBody.getWorldTransform(trans);
					trans.origin.set(location.x, location.y, location.z);
					this.rigidBody.setWorldTransform(trans);
				} else{
					// re-enter normal mode
					vanityMode = false;
					freeMode = false;
					
					// reset variables to what they once were
					yOffset = oldYOffset;
					xOffset = oldXOffset;
					zoom = oldZoom;
					minZoom = oldMinZoom;
					
					// check for zoom bounds
					if(zoom < minZoom)
						zoom = minZoom;
					
					// re-set the location
					location.set(oldLocation);
				}
			}
		} else{
			// so that they don't get pressed if they're being typed to the console
			KeyBindings.SYS_CAMERA_MODE.pressedOnce();
			KeyBindings.SYS_BUILD_MODE.pressedOnce();
		}
	}

	/**
	 * Zooms in and out or changes speed based on the current camera mode and how much the mouse wheel has been moved
	 */
	private void mousewheelLogic() {
		// only zoom when the console isn't on (otherwise the mouse wheel
		// controls console scrolling)
		if (!Console.consoleOn) {
			if (MouseManager.wheel != 0) {
				if(buildMode && !builder.leftGrabbed && !builder.rightGrabbed){
					speed += (speed * zoomSensitivity / MouseManager.wheel);
					
					// keep zoom in bounds
					if (speed < minSpeed)
						speed = minSpeed;
					else if (speed > maxSpeed)
						speed = maxSpeed;
				}
				else{
					zoom -= (zoom * zoomSensitivity / MouseManager.wheel);
					
					// keep zoom in bounds
					if (zoom < minZoom)
						zoom = minZoom;
					else if (zoom > maxZoom)
						zoom = maxZoom;
				}
			}
		}
	}
	
	/**
	 * Performs any free mode movement
	 * 
	 * @param timeStep
	 *            Amount of time passed since last update
	 * @param trans Transform to modify
	 */
	private void freeMode(float timeStep, Transform trans) {
		timeStep *= 100;
		// check for forward and backward movement
		boolean forward = KeyBindings.CONTROL_FORWARD.isPressed();
		boolean backward = KeyBindings.CONTROL_BACKWARD.isPressed();
		
		float dx = 0, dy = 0, dz = 0;

		// control forward and backward movement
		if(!(forward && backward)){
			if (forward)
				dz = speed * timeStep;
			else if (backward)
				dz = -speed * timeStep;
		}
		
		// check for left and right movement
		boolean left = KeyBindings.CONTROL_LEFT.isPressed();
		boolean right = KeyBindings.CONTROL_RIGHT.isPressed();

		// control strafing left and right
		if(!(left && right)){
			if (left)
				dx = speed * timeStep;
			else if (right)
				dx = -speed * timeStep;
		}
		
		
		// handle going up/down
		boolean up = KeyBindings.CONTROL_ASCEND.isPressed();
		boolean down = KeyBindings.CONTROL_DESCEND.isPressed();
		if(!(up && down)){
			if (up)
				dy = -speed * timeStep;
			else if (down)
				dy = speed * timeStep;
		}
		
		Vector3f veloc = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(dx, dy, dz), this.rotation);
		this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(veloc.x, veloc.y, veloc.z));
		
		trans.setRotation(new Quat4f(rotation.x, rotation.y, rotation.z, rotation.w));
	}

	/**
	 * Performs a ray test at the center of the camera into the depths of space.
	 * @return A RayResultCallback that says whether or not something was hit
	 */
	public ClosestRayResultCallback rayTestAtCenter(){
		// rotate the camera's offsets by its current rotation to get the offsets on the right plane
		Vector3f offsets = new Vector3f(xOffset, yOffset, -zoom);
		Vector3f actualLocation = QuaternionHelper.rotateVectorByQuaternion(offsets, rotation);
		// add location to rotated offsets to get actual camera position
		Vector3f.add(location, actualLocation, actualLocation);
		
		// create a vector far at as out we can see, then rotate it by the camera's current rotation to get it on the right plane
		Vector3f endOfTheGalaxy = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, Render3D.drawDistance), rotation);
		Vector3f endAdd = new Vector3f();
		// add the vector at the end to the camera's location to get a straight line going to the end
		Vector3f.add(actualLocation, endOfTheGalaxy, endAdd);
		
		javax.vecmath.Vector3f start = new javax.vecmath.Vector3f(actualLocation.x, actualLocation.y, actualLocation.z);
		javax.vecmath.Vector3f end = new javax.vecmath.Vector3f(endAdd.x, endAdd.y, endAdd.z);
		
		// perform test
		ClosestRayResultCallback callback = new ClosestRayResultCallback(start, end);
		Physics.dynamicsWorld.rayTest(start, end, callback);
		
		return callback;
	}
	
	/**
	 * @return The camera's position with its xOffset, yOffset and zoom taken into consideration
	 */
	public Vector3f getLocationWithOffset(){
		float x = location.x + xOffset;
		float y = location.y + yOffset;
		float z = location.z - zoom;
		return new Vector3f(x, y, z);
	}
	
	public void draw2D(){
		GL11.glColor3f(defaultCrosshairColor.x, defaultCrosshairColor.y, defaultCrosshairColor.z);
		
		// if we're not in build mode, use the crosshair
		if(!buildMode){
			currentCrosshairWidth = crosshairWidth;
			currentCrosshairHeight = crosshairHeight;
			currentCrosshair = Textures.CROSSHAIR;
		}else{
			// else if in build mode and grabbed, use grabbed image
			if(builder.leftGrabbed || builder.rightGrabbed){
				currentCrosshairWidth = handWidth;
				currentCrosshairHeight = handHeight;
				currentCrosshair = Textures.BUILDER_GRABBED;
			// else if not grabbed and we're looking at something, use the open image
			}else if(builder.lookingAt != null){
				currentCrosshairWidth = handWidth;
				currentCrosshairHeight = handHeight;
				currentCrosshair = Textures.BUILDER_OPEN;
			// else just the crosshair
			}else{
				currentCrosshairWidth = crosshairWidth;
				currentCrosshairHeight = crosshairHeight;
				currentCrosshair = Textures.CROSSHAIR;
			}
		}
		
		currentCrosshair.texture().bind();
		
		// draw the crosshair
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f((DisplayHelper.windowWidth / 2.0f) - currentCrosshairWidth, (DisplayHelper.windowHeight / 2.0f) + currentCrosshairHeight);

		GL11.glTexCoord2f(currentCrosshair.texture().getWidth(), 0);
		GL11.glVertex2f((DisplayHelper.windowWidth / 2.0f) + currentCrosshairWidth, (DisplayHelper.windowHeight / 2.0f) + currentCrosshairHeight);

		GL11.glTexCoord2f(currentCrosshair.texture().getWidth(), currentCrosshair.texture().getHeight());
		GL11.glVertex2f((DisplayHelper.windowWidth / 2.0f) + currentCrosshairWidth, (DisplayHelper.windowHeight / 2.0f) - currentCrosshairHeight);

		GL11.glTexCoord2f(0, currentCrosshair.texture().getHeight());
		GL11.glVertex2f((DisplayHelper.windowWidth / 2.0f) - currentCrosshairWidth, (DisplayHelper.windowHeight / 2.0f) - currentCrosshairHeight);
		GL11.glEnd();
		
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
	}

	@Override
	public void draw() {
		// camera dont need no drawin camera better than that
	}

	@Override
	public void cleanup() {
		// camera is clean, man
	}
}
