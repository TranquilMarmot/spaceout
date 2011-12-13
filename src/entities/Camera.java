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
 * @see Entity
 */
public class Camera extends Entity {
	/** the entity that the camera is following */
	public Entity following;

	/** the last time that this entity was updated */
	protected long lastUpdate;

	/** how fast this entity is moving */
	public float xSpeed, ySpeed, zSpeed;

	/** maximum speed */
	public float maxXSpeed, maxYSpeed, maxZSpeed;

	/** acceleration speed */
	public float xAccel, yAccel, zAccel;

	/** deceleration speed */
	public float xDecel, yDecel, zDecel;

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
	// keep key from repeating
	private boolean vanityKeyDown = false;

	public boolean freeMode = false;

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
		
		xSpeed = 0.0f;
		ySpeed = 0.0f;
		zSpeed = 0.0f;

		maxZSpeed = 10.0f;
		zAccel = 0.0175f;
		zDecel = 0.01f;

		maxXSpeed = 10.0f;
		xAccel = 0.0175f;
		xDecel = 0.01f;

		maxYSpeed = 15.0f;
		yAccel = 0.0175f;
		yDecel = 0.01f;
	}

	@Override
	public void update() {
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

		// move the camera to be behind whatever it's following
		if (!freeMode) {
			location.x = following.location.x;
			location.y = following.location.y;
			location.z = following.location.z;
		}

		// check for vanity mode key
		if (KeyboardManager.vanityMode && !vanityKeyDown) {
			// three states: normal (vanity and free false), vanity (vanity true free false), free (vanity false free true)
			if(!vanityMode && !freeMode){
				vanityMode = true;
			}
			else if(vanityMode && !freeMode){
				vanityMode = false;
				freeMode = true;
			}
			else if(freeMode && !vanityMode){
				freeMode = false;
			}
			// if we're switching into vanity mode, the camera gets its own
			// rotation (instead of being tied to what it's following)
			if (vanityMode || freeMode)
				rotation = new Quaternion(following.rotation.x,
						following.rotation.y, following.rotation.z,
						following.rotation.w);
			if(freeMode){
				location = new Vector3f(following.location.x, following.location.y, following.location.z);
			}
			vanityKeyDown = true;
		}
		if (!KeyboardManager.vanityMode)
			vanityKeyDown = false;

		if (vanityMode || freeMode) {
			// if we're in vanity mode, apply any rotation changes
			rotateX(MouseManager.dy);
			rotateY(MouseManager.dx);
		} else {
			// if we're not in vanity mode, the camera rotates with what it's
			// following
			rotation = following.rotation;
		}
		
		lastUpdate = Debug.getTime();
		
		if (freeMode) {
			freeMode();
		}
	}

	private void freeMode() {
		int delta = 80;
		if (!Runner.paused) {
			boolean forward = KeyboardManager.forward;
			boolean backward = KeyboardManager.backward;

			boolean left = KeyboardManager.left;
			boolean right = KeyboardManager.right;

			// control forward and backward movement
			if (forward || backward) {
				if (forward) {
					accelerateZPos(delta);
				}
				if (backward) {
					accelerateZNeg(delta);
				}
			} else if (zSpeed != 0) {
				decelerateZ(delta);
			}

			moveZ(zSpeed);

			// control strafing left and right
			if (left || right) {
				if (left) {
					accelerateXPos(delta);
				}
				if (right) {
					accelerateXNeg(delta);
				}
			} else if (xSpeed != 0) {
				decelerateX(delta);
			}

			moveX(xSpeed);

			// handle going up/down
			boolean up = KeyboardManager.ascend;
			boolean down = KeyboardManager.descend;
			if (up || down) {
				if (up)
					accelerateYNeg(delta);
				if (down)
					accelerateYPos(delta);
			} else if (ySpeed != 0) {
				decelerateY(delta);
			}

			moveY(ySpeed);
			
			//System.out.println(xSpeed + " " + ySpeed + " " + zSpeed);

			// roll left/right
			boolean rollRight = KeyboardManager.rollRight;
			boolean rollLeft = KeyboardManager.rollLeft;
			if (rollRight)
				rotateZ(-delta / 20.0f);
			if (rollLeft)
				rotateZ(delta / 20.0f);

		System.out.println(rotation.x + " " + rotation.y + " " + rotation.z);
		}
	}

	private int getDelta() {
		long time = Debug.getTime();
		int delta = (int) (time - lastUpdate);
		lastUpdate = time;

		return delta;
	}

	/**
	 * Move this entity along the X axis
	 * 
	 * @param amount
	 *            Amount to move this entity
	 */
	public void moveX(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(1.0f, 0.0f, 0.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	/**
	 * Move this entity along the Y axis
	 * 
	 * @param amount
	 *            Amount to move this entity
	 */
	public void moveY(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(0.0f, 1.0f, 0.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	/**
	 * Move this entity along the Z axis
	 * 
	 * @param amount
	 *            Amount to move this entity
	 */
	public void moveZ(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(0.0f, 0.0f, 1.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	/**
	 * Changes the entity's z movement speed going positive
	 * 
	 * @param delta
	 *            The amount of time since the last update (acquired from
	 *            getDelta every update)
	 */
	protected void accelerateZPos(int delta) {
		if (zSpeed < maxZSpeed) {
			float newSpeed = zSpeed + (delta * zAccel);
			if (newSpeed > maxZSpeed)
				zSpeed = maxZSpeed;
			else
				zSpeed = newSpeed;
		}
	}

	/**
	 * Changes the entity's z movement speed going negative
	 * 
	 * @param delta
	 *            The amount of time since the last update (acquired from
	 *            getDelta every update)
	 */
	protected void accelerateZNeg(int delta) {
		if (zSpeed > -maxZSpeed) {
			float newSpeed = zSpeed - (delta * zAccel);
			if (newSpeed < -maxZSpeed)
				zSpeed = -maxZSpeed;
			else
				zSpeed = newSpeed;
		}
	}

	/**
	 * Decelerates the entity in the opposite direction that it's going until it
	 * reaches 0
	 * 
	 * @param delta
	 */
	protected void decelerateZ(int delta) {
		if (zSpeed > 0) {
			float newSpeed = zSpeed - (delta * zDecel);
			if (newSpeed < 0)
				zSpeed = 0;
			else
				zSpeed = newSpeed;
		} else if (zSpeed < 0) {
			float newSpeed = zSpeed + (delta * zDecel);
			if (newSpeed > 0)
				zSpeed = 0;
			else
				zSpeed = newSpeed;
		}
		// else zSpeed is zero and nothing needs to be done
	}

	/**
	 * Changes the entity's z movement speed going positive
	 * 
	 * @param delta
	 *            The amount of time since the last update (acquired from
	 *            getDelta every update)
	 */
	protected void accelerateXPos(int delta) {
		if (xSpeed < maxXSpeed) {
			float newSpeed = xSpeed + (delta * xAccel);
			if (newSpeed > maxXSpeed)
				xSpeed = maxXSpeed;
			else
				xSpeed = newSpeed;
		}
	}

	/**
	 * Changes the entity's z movement speed going negative
	 * 
	 * @param delta
	 *            The amount of time since the last update (acquired from
	 *            getDelta every update)
	 */
	protected void accelerateXNeg(int delta) {
		if (xSpeed > -maxXSpeed) {
			float newSpeed = xSpeed - (delta * xAccel);
			if (newSpeed < -maxXSpeed)
				xSpeed = -maxXSpeed;
			else
				xSpeed = newSpeed;
		}
	}

	/**
	 * Decelerates the entity in the opposite direction that it's going until it
	 * reaches 0
	 * 
	 * @param delta
	 */
	protected void decelerateX(int delta) {
		if (xSpeed > 0) {
			float newSpeed = xSpeed - (delta * xDecel);
			if (newSpeed < 0)
				xSpeed = 0;
			else
				xSpeed = newSpeed;
		} else if (xSpeed < 0) {
			float newSpeed = xSpeed + (delta * xDecel);
			if (newSpeed > 0)
				xSpeed = 0;
			else
				xSpeed = newSpeed;
		}
		// else zSpeed is zero and nothing needs to be done
	}

	/**
	 * Changes the entity's z movement speed going positive
	 * 
	 * @param delta
	 *            The amount of time since the last update (acquired from
	 *            getDelta every update)
	 */
	protected void accelerateYPos(int delta) {
		if (ySpeed < maxYSpeed) {
			float newSpeed = ySpeed + (delta * yAccel);
			if (newSpeed > maxYSpeed)
				ySpeed = maxYSpeed;
			else
				ySpeed = newSpeed;
		}
	}

	/**
	 * Changes the entity's z movement speed going negative
	 * 
	 * @param delta
	 *            The amount of time since the last update (acquired from
	 *            getDelta every update)
	 */
	protected void accelerateYNeg(int delta) {
		if (ySpeed > -maxYSpeed) {
			float newSpeed = ySpeed - (delta * yAccel);
			if (newSpeed < -maxYSpeed)
				ySpeed = -maxYSpeed;
			else
				ySpeed = newSpeed;
		}
	}

	/**
	 * Decelerates the entity in the opposite direction that it's going until it
	 * reaches 0
	 * 
	 * @param delta
	 */
	protected void decelerateY(int delta) {
		if (ySpeed > 0) {
			float newSpeed = ySpeed - (delta * yDecel);
			if (newSpeed < 0)
				ySpeed = 0;
			else
				ySpeed = newSpeed;
		} else if (ySpeed < 0) {
			float newSpeed = ySpeed + (delta * yDecel);
			if (newSpeed > 0)
				ySpeed = 0;
			else
				ySpeed = newSpeed;
		}
		// else zSpeed is zero and nothing needs to be done
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
