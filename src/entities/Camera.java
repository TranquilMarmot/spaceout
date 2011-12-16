package entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.Runner;
import util.debug.Debug;
import util.helper.QuaternionHelper;
import util.manager.KeyboardManager;
import util.manager.MouseManager;

/**
 * A camera that tells how the scene is being looked at
 * 
 * @author TranquilMarmot
 */
public class Camera extends Entity {
	/** initial values for when the camera is created */
	private static final float INIT_ZOOM = 1.4f;
	private static final float INIT_XOFFSET = 0.0f;
	private static final float INIT_YOFFSET = -0.35F;

	/** the entity that the camera is following */
	public Entity following;

	/** the last time that this entity was updated */
	protected long lastUpdate;

	/** how fast the camera moves in free mode */
	public float speed = 5.0f;

	/** zoom level */
	public float zoom;
	/** Offset along Y axis */
	public float yOffset;
	/** Offset along X axis */
	public float xOffset;

	/** maximum zoom level */
	private float maxZoom = 3000.0f;
	/** minimum zoom level */
	private float minZoom = 1.25f;

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

	@Override
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
		if (KeyboardManager.cameraMode && !modeKeyDown) {
			// three states: normal (vanity and free false), vanity (vanity true
			// free false), free (vanity false free true)
			if (!vanityMode && !freeMode) {
				vanityMode = true;
			} else if (vanityMode && !freeMode) {
				vanityMode = false;
				freeMode = true;
			} else if (freeMode && !vanityMode) {
				freeMode = false;
			}
			modeKeyDown = true;
		}
		if (!KeyboardManager.cameraMode)
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
		if (!Debug.consoleOn) {
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
		if (!Runner.paused) {
			// check for forward and backward movement
			boolean forward = KeyboardManager.forward;
			boolean backward = KeyboardManager.backward;

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
			boolean left = KeyboardManager.left;
			boolean right = KeyboardManager.right;

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
			boolean up = KeyboardManager.ascend;
			boolean down = KeyboardManager.descend;
			if (up || down) {
				if (up)
					this.location = QuaternionHelper.moveY(this.rotation, this.location, -speed * delta);
				if (down)
					this.location = QuaternionHelper.moveY(this.rotation, this.location, speed * delta);
			}

			// roll left/right
			boolean rollRight = KeyboardManager.rollRight;
			boolean rollLeft = KeyboardManager.rollLeft;
			if (rollRight)
				this.rotation = QuaternionHelper.rotateZ(this.rotation, -delta);
			if (rollLeft)
				this.rotation = QuaternionHelper.rotateZ(this.rotation, delta);
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
