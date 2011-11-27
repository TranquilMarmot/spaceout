package entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.Runner;
import util.debug.Debug;
import util.helper.KeyboardHandler;
import util.helper.MouseHandler;

public class Camera extends Entity {
	// the entity that the camera is following
	public Entity following;

	public float zoom;
	public float yOffset;
	public float xOffset;
	
	private float maxZoom = 3000.0f;
	private float minZoom = 20.0f;

	/**
	 * If this is false, the camera rotates with whatever it's following. If
	 * it's false, the camera rotates around what it's following
	 */
	public boolean vanityMode = false;
	private boolean vanityKeyDown = false;

	/** how fast the camera moves */
	float rollSpeed = 13.0f;

	public Camera(float x, float y, float z) {
		super();
		location = new Vector3f(x, y, z);
		rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.type = "camera";
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
			zoom -= MouseHandler.wheel / zoomSensitivity;
		if (zoom < minZoom)
			zoom = minZoom;
		else if (zoom > maxZoom)
			zoom = maxZoom;

		// move the camera to be behind whatever it's following
		location.x = following.location.x;
		location.y = following.location.y;
		location.z = following.location.z;
		
		//check for vanity mode key
		if(KeyboardHandler.vanityMode && !vanityKeyDown){
			vanityMode = !vanityMode;
			// if we're switching into vanity mode, the camera gets its own rotation (instead of being tied to what it's following)
			if(vanityMode)
				rotation = new Quaternion(following.rotation.x, following.rotation.y, following.rotation.z, following.rotation.w);
			vanityKeyDown = true;
		}
		if(!KeyboardHandler.vanityMode)
			vanityKeyDown = false;

		if(vanityMode && !Runner.paused){
			// if we're in vanity mode, apply any rotation changes
			rotateX(MouseHandler.dy);
			rotateY(MouseHandler.dx);
		} else {
			// if we're not in vanity mode, the camera rotates with what it's following
			rotation = following.rotation;
		}
	}

	@Override
	public void draw() {
		// camera dont need no drawin camera better than that
	}

}
