package spaceguts.entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.graphics.render.Render3D;
import spaceguts.physics.Physics;
import spaceguts.util.Debug;
import spaceguts.util.DisplayHelper;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.Runner;
import spaceguts.util.console.Console;
import spaceguts.util.input.KeyBindings;
import spaceguts.util.input.MouseManager;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;

/**
 * A camera that tells how the scene is being looked at
 * 
 * @author TranquilMarmot
 */
public class Camera extends Entity {
	/** initial values for when the camera is created */
	private static final float INIT_ZOOM = 12.0f;
	private static final float INIT_XOFFSET = 0.0f;
	private static final float INIT_YOFFSET = -2.7F;

	/** the entity that the camera is following */
	public Entity following;

	/** the last time that this entity was updated */
	protected long lastUpdate;

	/** how fast the camera moves in free mode */
	public float speed = 10.0f;

	/** zoom level */
	public float zoom;
	/** Offset along Y axis */
	public float yOffset;
	/** Offset along X axis */
	public float xOffset;
	
	/** used to preserve offset when switching modes */
	private float oldYOffset, oldXOffset, oldMinZoom;

	/** maximum zoom level */
	private float maxZoom = 3000.0f;
	/** minimum zoom level */
	private float minZoom = 10.0f;

	/**
	 * If this is false, the camera rotates with whatever it's following. If
	 * it's false, the camera rotates around what it's following
	 */
	public boolean vanityMode = false;
	public boolean freeMode = false;

	/** to keep key from repeating */
	private boolean modeKeyDown = false;

	/** how fast the camera rolls */
	float rollSpeed = 13.0f;

	/**
	 * Camera constructor
	 */
	public Camera(Vector3f location) {
		super();
		this.location = location;
		rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.type = "camera";
		lastUpdate = Debug.getTime();
	}

	/**
	 * Creates a new camera in the right spot
	 */
	public static void createCamera() {
		// initialize the camera
		Entities.camera = new Camera(new Vector3f(0.0f, 0.0f, 0.0f));
		Entities.camera.zoom = INIT_ZOOM;
		Entities.camera.yOffset = INIT_YOFFSET;
		Entities.camera.xOffset = INIT_XOFFSET;
	}

	/**
	 * Update the camera. Ths handles following other things, mode switches etc.
	 */
	public void update() {
		// if we're not following anything, we're in free mode
		if (following == null) {
			vanityMode = false;
			freeMode = true;
		}

		// perform zoom logic
		zoom();

		// check for any key presses
		checkForModeSwitch();

		// If we're not in freeMode and not in vanityMode, we're rotating with
		// an
		// entity
		if (!freeMode && !vanityMode) {
			this.rotation.set(following.rotation);
		} else if (vanityMode || freeMode) {
			// if we're in vanity or free mode, apply any rotation changes
			this.rotation = QuaternionHelper.rotateX(this.rotation, MouseManager.dy);
			this.rotation = QuaternionHelper.rotateY(this.rotation, MouseManager.dx);
		}

		// to keep free mode movement framerate-independent
		float delta = (float) getDelta() / 10.0f;

		// if we're not in free mode, move the camera to be behind whatever it's
		// following
		if (!freeMode) {
			this.location.set(following.location);
		} else if (freeMode && !vanityMode) {
			// else do logic for moving around in free mode
			freeMode(delta);
		}

		// to keep free mode framerate independent
		lastUpdate = Debug.getTime();
	}

	/**
	 * Checks if the mode change button has been pressed and changes states
	 * accordingly
	 */
	private void checkForModeSwitch() {
		// check for mode key
		if (KeyBindings.SYS_CAMERA_MODE.isPressed() && !modeKeyDown) {
			// three states: normal (vanity and free false), vanity (vanity true
			// free false), free (vanity false free true)
			if (!vanityMode && !freeMode) {
				vanityMode = true;
				oldYOffset = yOffset;
				oldXOffset = xOffset;
				oldMinZoom = minZoom;
				yOffset = 0;
				xOffset = 0;
				minZoom = 0;
			} else if (vanityMode && !freeMode) {
				vanityMode = false;
				freeMode = true;
			} else if (freeMode && !vanityMode) {
				freeMode = false;
				rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
				yOffset = oldYOffset;
				xOffset = oldXOffset;
				minZoom = oldMinZoom;
			}
			modeKeyDown = true;
		}
		if (!KeyBindings.SYS_CAMERA_MODE.isPressed())
			modeKeyDown = false;
	}

	/**
	 * Zooms in and out based on mouse wheel scroll
	 */
	private void zoom() {
		// change how fast the zoom is based on the zoom distance
		float zoomSensitivity = 10;

		// only zoom when the console isn't on (otherwise the mouse wheel
		// controls console scrolling)
		if (!Console.consoleOn) {
			if (MouseManager.wheel != 0) {
				zoom -= (zoom * zoomSensitivity / MouseManager.wheel);
			}
		}

		// keep zoom in bounds
		if (zoom < minZoom)
			zoom = minZoom;
		else if (zoom > maxZoom)
			zoom = maxZoom;
	}

	/**
	 * Performs any free mode movement
	 * 
	 * @param delta
	 *            Amount of time passed since last update
	 */
	private void freeMode(float delta) {
		// only move if we're not paused
		if (!Runner.paused && !Console.consoleOn) {
			// check for forward and backward movement
			boolean forward = KeyBindings.CONTROL_FORWARD.isPressed();
			boolean backward = KeyBindings.CONTROL_BACKWARD.isPressed();

			// control forward and backward movement
			if (forward || backward) {
				if (forward) {
					this.location = QuaternionHelper.moveZ(this.rotation, this.location, speed * delta);
				}
				if (backward) {
					this.location = QuaternionHelper.moveZ(this.rotation, this.location, -speed * delta);
				}
			}

			// check for left and right movement
			boolean left = KeyBindings.CONTROL_LEFT.isPressed();
			boolean right = KeyBindings.CONTROL_RIGHT.isPressed();

			// control strafing left and right
			if (left || right) {
				if (left) {
					this.location = QuaternionHelper.moveX(this.rotation, this.location, speed * delta);
				}
				if (right) {
					this.location = QuaternionHelper.moveX(this.rotation, this.location, -speed * delta);
				}
			}

			// handle going up/down
			boolean up = KeyBindings.CONTROL_ASCEND.isPressed();
			boolean down = KeyBindings.CONTROL_DESCEND.isPressed();
			if (up || down) {
				if (up)
					this.location = QuaternionHelper.moveY(this.rotation, this.location, -speed * delta);
				if (down)
					this.location = QuaternionHelper.moveY(this.rotation, this.location, speed * delta);
			}

			// roll left/right
			boolean rollRight = KeyBindings.CONTROL_ROLL_RIGHT.isPressed();
			boolean rollLeft = KeyBindings.CONTROL_ROLL_LEFT.isPressed();
			if (rollRight)
				this.rotation = QuaternionHelper.rotateZ(this.rotation, -delta);
			if (rollLeft)
				this.rotation = QuaternionHelper.rotateZ(this.rotation, delta);
		}
	}
	
	public DynamicEntity rayTestAtCursor(){
		/*
		 * FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME
		 */
		float halfWidth = DisplayHelper.windowWidth / 2.0f;
		float halfHeight = DisplayHelper.windowHeight / 2.0f;
		float adjustedX = MouseManager.x - halfWidth;
		float adjustedY = MouseManager.y - halfHeight;
		
		//Quaternion rev = new Quaternion();
		//rotation.negate(rev);
		
		
		Vector3f realLocation = new Vector3f(location.x + xOffset, location.y + yOffset, location.z - zoom);
		Vector3f s = new Vector3f(realLocation.x + adjustedX, realLocation.y + adjustedY, realLocation.z);
		
		Vector3f adj = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(adjustedX, adjustedY, Render3D.drawDistance), rotation);
		Vector3f e = new Vector3f();
		Vector3f.add(realLocation, adj, e);
		
		
		/*
		Vector3f adj = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(-adjustedX, adjustedY, 0.0f), rotation);
		
		Vector3f realLocation = new Vector3f(location.x + xOffset, location.y + yOffset, location.z - zoom);
		
		Vector3f s = new Vector3f();
		Vector3f.add(realLocation, adj, s);
		
		Vector3f infinity = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, Render3D.drawDistance), rotation);
		
		Vector3f e = new Vector3f();
		Vector3f.add(s, infinity, e);
		*/
		
		javax.vecmath.Vector3f start = new javax.vecmath.Vector3f(s.x, s.y, s.z);
		javax.vecmath.Vector3f end = new javax.vecmath.Vector3f(e.x, e.y, e.z);
		
		
		
		/*
		float aspect = (float)DisplayHelper.windowWidth / (float)DisplayHelper.windowHeight;
		
		//Vector3f newLocation = QuaternionHelper.moveX(rotation, location, adjustedX);
		//newLocation = QuaternionHelper.moveY(rotation, newLocation, adjustedY);
		
		Vector3f adjustedRotated = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(adjustedX, adjustedY, 0.0f), rotation);
		
		//Vector3f newLocation = new Vector3f();
		//Vector3f.add(location, new Vector3f(adjustedX, adjustedY, 0.0f), newLocation);
		
		Vector3f endOfTheGalaxy = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, Render3D.drawDistance), rotation);
		Vector3f endAdd = new Vector3f();
		Vector3f.add(location, endOfTheGalaxy, endAdd);
		
		javax.vecmath.Vector3f start = new javax.vecmath.Vector3f(location.x, location.y, location.z);
		javax.vecmath.Vector3f end = new javax.vecmath.Vector3f(endAdd.x, endAdd.y, endAdd.z);
		
		System.out.println(adjustedX + " " + adjustedY + " | " + start.x + " " + start.y + " " + start.z + " | " + end.x + " " + end.y + " " + end.z);
		*/
		
		ClosestRayResultCallback callback = new ClosestRayResultCallback(start, end);
		Physics.dynamicsWorld.rayTest(start, end, callback);
		
		if(callback.hasHit()){
			return (DynamicEntity)callback.collisionObject.getUserPointer();
		} else{
			return null;
		}
	}

	/**
	 * @return Time passed since last update
	 */
	private int getDelta() {
		long time = Debug.getTime();
		int delta = (int) (time - lastUpdate);
		lastUpdate = time;

		return delta;
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
