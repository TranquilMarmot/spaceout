package entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.Runner;
import util.debug.Debug;
import util.manager.KeyboardManager;
import util.manager.MouseManager;

/**
 * A camera that tells how the scene is being looked at
 * 
 * @author TranquilMarmot
 * @see Entity
 */
public class Camera extends Entity {
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
	private float minZoom = 5.0f;

	/**
	 * If this is false, the camera rotates with whatever it's following. If
	 * it's false, the camera rotates around what it's following
	 */
	public boolean vanityMode = false;
	public boolean freeMode = false;

	// keep key from repeating
	private boolean modeKeyDown = false;

	/** how fast the camera rolls */
	float rollSpeed = 13.0f;

	/**
	 * Camera constructor
	 * 
	 * @param x
	 *            Initial X position
	 * @param y
	 *            Initial Y position
	 * @param z
	 *            Initial Z position
	 */
	public Camera(float x, float y, float z) {
		super();
		location = new Vector3f(x, y, z);
		rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.type = "camera";
		lastUpdate = Debug.getTime();
	}

	@Override
	public void update() {
		if(following == null){
			vanityMode = false;
			freeMode = true;
		}
		
		zoom();
		checkForModeSwitch();

		// If we're not in freeMode and not in vanityMode, we're following an
		// entity
		if (!freeMode && !vanityMode) {
			this.rotation.set(following.rotation);
		} else if (vanityMode || freeMode) {
			// if we're in vanity mode, apply any rotation changes
			rotateX(MouseManager.dy);
			rotateY(MouseManager.dx);
		}
		
		float delta = (float)getDelta() / 10.0f;

		// move the camera to be behind whatever it's following
		if (!freeMode) {
			this.location.set(following.location);
		} else if (freeMode && !vanityMode) {
			freeMode(delta);
		}

		lastUpdate = Debug.getTime();
	}

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

	private void zoom() {
		// change how fast the zoom is based on the zoom distance
		float zoomSensitivity = 1;
		if (zoom < 40.0f)
			zoomSensitivity = 75.0f;
		else if (zoom > 40.0f && zoom < 150.0f)
			zoomSensitivity = 50.0f;
		else if (zoom > 150.0f && zoom < 1000.0f)
			zoomSensitivity = 25.0f;
		else if (zoom > 1000.0f)
			zoomSensitivity = 10.0f;

		// handle zooming
		if (!Debug.consoleOn)
			zoom -= MouseManager.wheel / zoomSensitivity;
		if (zoom < minZoom)
			zoom = minZoom;
		else if (zoom > maxZoom)
			zoom = maxZoom;
	}

	private void freeMode(float delta) {
		if (!Runner.paused) {
			boolean forward = KeyboardManager.forward;
			boolean backward = KeyboardManager.backward;

			// control forward and backward movement
			if (forward || backward) {
				if (forward) {
					moveZ(speed * delta);
				}
				if (backward) {
					moveZ(-speed * delta);
				}
			}

			boolean left = KeyboardManager.left;
			boolean right = KeyboardManager.right;

			// control strafing left and right
			if (left || right) {
				if (left) {
					moveX(speed * delta);
				}
				if (right) {
					moveX(-speed * delta);
				}
			}

			// handle going up/down
			boolean up = KeyboardManager.ascend;
			boolean down = KeyboardManager.descend;
			if (up || down) {
				if (up)
					moveY(-speed * delta);
				if (down)
					moveY(speed * delta);
			}

			// roll left/right
			boolean rollRight = KeyboardManager.rollRight;
			boolean rollLeft = KeyboardManager.rollLeft;
			if (rollRight)
				rotateZ(-delta);
			if (rollLeft)
				rotateZ(delta);
		}
	}
	
	private int getDelta(){
		long time = Debug.getTime();
		int delta = (int)(time - lastUpdate);
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
