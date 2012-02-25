package spaceguts.entities;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.graphics.render.Render3D;
import spaceguts.input.KeyBindings;
import spaceguts.input.MouseManager;
import spaceguts.physics.Builder;
import spaceguts.physics.Physics;
import spaceguts.util.Debug;
import spaceguts.util.DisplayHelper;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.Runner;
import spaceguts.util.console.Console;
import spaceout.resources.Textures;

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
	
	/** handles special interactions with the game world */
	public Builder builder;

	/** the last time that this entity was updated */
	protected long lastUpdate;

	/** how fast the camera moves in free mode */
	public float speed = 10.0f;
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
	float rollSpeed = 13.0f;
	
	// FIXME should the crosshair be its own class?
	public int crosshairWidth = 8, crosshairHeight = 8;
	public int handWidth = 15, handHeight = 17;
	private int currentCrosshairWidth = crosshairWidth, currentCrosshairHeight = crosshairHeight;
	private Textures currentCrosshair = Textures.CROSSHAIR;
	public Vector3f defaultCrosshairColor = new Vector3f(1.0f, 1.0f, 1.0f);

	/**
	 * Camera constructor
	 */
	public Camera(Vector3f location) {
		super();
		this.location = location;
		rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.type = "camera";
		lastUpdate = Debug.getTime();
		builder = new Builder(this);
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
		if(buildMode)
			builder.update();
		
		// if we're not following anything, we're in free mode
		if (following == null) {
			vanityMode = false;
			freeMode = true;
		}

		// perform zoom logic
		mousewheelLogic();

		// check for any key presses
		checkForModeSwitch();

		// If we're not in freeMode and not in vanityMode, we're rotating with
		// an
		// entity
		if (!freeMode && !vanityMode) {
			this.rotation.set(following.rotation);
		} else if ((vanityMode || freeMode) && !builder.rightGrabbed) {
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
		if(!Console.consoleOn){
			if(!buildMode){
				// swap the camera mode
				if (KeyBindings.SYS_CAMERA_MODE.pressedOnce()) {
					// three states: normal (vanity and free false), vanity (vanity true
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
			if (forward) {
				this.location = QuaternionHelper.moveZ(this.rotation, this.location, speed * delta);
			}
			if (backward) {
				this.location = QuaternionHelper.moveZ(this.rotation, this.location, -speed * delta);
			}

			// check for left and right movement
			boolean left = KeyBindings.CONTROL_LEFT.isPressed();
			boolean right = KeyBindings.CONTROL_RIGHT.isPressed();

			// control strafing left and right
			if (left) {
				this.location = QuaternionHelper.moveX(this.rotation, this.location, speed * delta);
			}
			if (right) {
				this.location = QuaternionHelper.moveX(this.rotation, this.location, -speed * delta);
			}

			// handle going up/down
			boolean up = KeyBindings.CONTROL_ASCEND.isPressed();
			boolean down = KeyBindings.CONTROL_DESCEND.isPressed();
				if (up)
					this.location = QuaternionHelper.moveY(this.rotation, this.location, -speed * delta);
				if (down)
					this.location = QuaternionHelper.moveY(this.rotation, this.location, speed * delta);

			// roll left/right
			boolean rollRight = KeyBindings.CONTROL_ROLL_RIGHT.isPressed();
			boolean rollLeft = KeyBindings.CONTROL_ROLL_LEFT.isPressed();
			if (rollRight)
				this.rotation = QuaternionHelper.rotateZ(this.rotation, -delta);
			if (rollLeft)
				this.rotation = QuaternionHelper.rotateZ(this.rotation, delta);
		}
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

	/**
	 * @return Time passed since last update
	 */
	private int getDelta() {
		long time = Debug.getTime();
		int delta = (int) (time - lastUpdate);
		lastUpdate = time;

		return delta;
	}
	
	public void drawCrosshair(){
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
