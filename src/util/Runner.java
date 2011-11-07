package util;

import java.nio.FloatBuffer;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import util.console.ConsoleManager;
import util.helper.DisplayHelper;
import util.helper.KeyboardHandler;
import util.helper.MouseHandler;
import util.helper.QuaternionHelper;
import util.xml.XMLParser;
import entities.Camera;
import entities.Entities;
import entities.Entity;
import entities.Light;

// Rule number 1: Tell everyone about Spaceout (ask them for ideas! We need ideas!).
// Rule number 2: Comment everything motherfucker.

public class Runner {
	/** prevents updates but still renders the scene */
	public static boolean paused = true;
	/** keeps the pause button from repeatedly pausing and unpausing */
	private boolean pauseDown = false;

	/** if either of this is true, it means it's time to shut down ASAP */
	public static boolean done = false;

	/** the keyboard and mouse handlers that need to be updated every frame */
	public static KeyboardHandler keyboard = new KeyboardHandler();
	public static MouseHandler mouse = new MouseHandler();

	/** how far to draw objects */
	private float drawDistance = 5000000.0f;

	private FloatBuffer cameraRotBuffer;

	public static void main(String[] args) {
		// Instantiate a runner, otherwise everything would have to be static
		Runner run = new Runner();
		run.run();
	}

	/**
	 * Runs the game
	 */
	public void run() {
		// initialize everything
		init();
		try {
			// keep going until the done flag is up or a window close is
			// requested
			while (!done && !DisplayHelper.closeRequested) {
				// check for window resizes
				DisplayHelper.resizeWindow();
				// update misc stuff (keyboard, mouse, etc.)
				update();
				// if we're not paused, update all the entities
				updateEntities();
				// render the scene
				render();
				// update the display (this swaps the buffers)
				Display.update();
				Display.sync(DisplayHelper.targetFPS);
			}
			// get rid of the display and the frame when we're done (otherwise
			// we'll be left with a zombie screen)
			Display.destroy();
			DisplayHelper.frame.dispose();
		} catch (Exception e) {
			// if an exception is caught, destroy the display ad the frame
			Display.destroy();
			DisplayHelper.frame.dispose();
			e.printStackTrace();
		}
	}

	/**
	 * Initialize OpenGL, variables, etc.
	 */
	private void init() {
		DisplayHelper.createWindow();
		initGL();

		// allocate space for the camera rotation buffer (this buffer is cleared
		// and then written to whenever it is used to conserve memory)
		cameraRotBuffer = BufferUtils.createFloatBuffer(16);

		// load the debug XML files
		XMLParser.loadEntitiesFromXmlFile("res/XML/SolarSystem.xml");

		// initialize the camera
		Entities.camera = new Camera(Entities.player.location.x,
				Entities.player.location.y, Entities.player.location.z);
		Entities.camera.zoom = 30.0f;
		Entities.camera.yOffset = -5.0f;
		Entities.camera.xOffset = 1.0f;
		Entities.camera.following = Entities.player;
	}

	/**
	 * Sets up OpenGL
	 */
	private void initGL() {
		// Enable 2D textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// Enable smooth shading
		GL11.glShadeModel(GL11.GL_SMOOTH);

		// Clear to a black background
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		// Setup the depth buffer
		GL11.glClearDepth(1.0f);
		// Enable depth testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// Type of depth test to do
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		// Really nice perspective calculations
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

		// Blending
		// this line is needed for drawing text, so if blending changes and
		// suddenly text won't draw try throwing this in there
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		// Select and reset the Projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(45.0f, (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight, 0.1f, drawDistance);

		// Select the Modelview matrix
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	/**
	 * Updates everything
	 */
	private void update() {
		//System.out.println("delta: " + Debug.getDelta());
		// update the mouse and keyboard handlers
		mouse.update();
		keyboard.update();

		/* BEGIN PAUSE LOGIC */
		// if pauseDown is true, it means that the pause button is being held,
		// so it avoids repeatedly flipping paused when the key is held
		if (KeyboardHandler.pause && !pauseDown) {
			paused = !paused;
			pauseDown = true;
		}

		if (!KeyboardHandler.pause) {
			pauseDown = false;
		}

		// release the mouse if the game's paused
		if (!paused && !ConsoleManager.consoleOn)
			Mouse.setGrabbed(true);
		else
			Mouse.setGrabbed(false);
		/* END PAUSE LOGIC */
		
		DisplayHelper.doFullscreenLogic();
	}

	/**
	 * Updates all the entities
	 */
	private void updateEntities() {
		// these two are special so they're updated here
		Entities.player.update();
		Entities.camera.update();

		/*
		 *  the rest of the entities are updated right before they're rendered to decrease the number of loops through Entities.entities
		 *  In the future, Entities.entities should have two threads that can operate on it- reading threads and a writing thread
		 *  The reading threads would be used for rendering and collision detection, while the writing thread would be used for things like updating locations 
		 */
	}

	/**
	 * Renders the scene
	 */
	private void render() {
		// Clear the color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		/* BEGIN 3D DRAWING */
		// select and reset the projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		// calculate the current aspect ratio
		float aspect = (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight;
		GLU.gluPerspective(45.0f, aspect, 1.0f, drawDistance);

		// we're done setting up the Projection matrix, on to the Modelview
		// matrix
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// disable blending for now
		GL11.glDisable(GL11.GL_BLEND);

		// enable lighting and select a lighting model
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER, GL11.GL_TRUE);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);

		// move to the camera
		GL11.glTranslatef(Entities.camera.xOffset, Entities.camera.yOffset,
				-Entities.camera.zoom);

		// push the matrix to draw all the entities, then we pop it when we draw
		// the
		// player
		GL11.glPushMatrix();
		{
			// rotate based on camera rotation
			QuaternionHelper.toFloatBuffer(Entities.camera.rotation,
					cameraRotBuffer);
			GL11.glMultMatrix(cameraRotBuffer);

			// l.draw() should set up any light that the entity owns, and then
			// draw the entity itself
			for (Light l : Entities.lights) {
				l.draw();
			}
			
			/* BEGIN ENTITY DRAWING */
			//for (Entity ent : Entities.entities) {
			if(!Entities.addBuffer.isEmpty()){
				Iterator<Entity> addIterator = Entities.addBuffer.iterator();
				while(addIterator.hasNext()){
					Entity ent = addIterator.next();
					Entities.entities.add(ent);
					addIterator.remove();
				}
			}
			
			Iterator<Entity> entityIterator = Entities.entities.iterator();
			while(entityIterator.hasNext()) {
				//FIXME concurrent modification exception
				Entity ent = entityIterator.next();
				// update the entity if we're not paused
				ent.update();
				float transx = Entities.camera.location.x - ent.location.x;
				float transy = Entities.camera.location.y - ent.location.y;
				float transz = Entities.camera.location.z - ent.location.z;

				GL11.glPushMatrix();
				{
					// translate to the entity's location
					GL11.glTranslatef(transx, transy, transz);
					ent.draw();
				}
				GL11.glPopMatrix();
			}
			
			float transx = Entities.camera.location.x - Entities.player.location.x;
			float transy = Entities.camera.location.y - Entities.player.location.y;
			float transz = Entities.camera.location.z - Entities.player.location.z;
			GL11.glTranslatef(transx, transy, transz);
			Entities.player.draw();
			/* END ENTITY DRAWING */
		}
		GL11.glPopMatrix();
		
		// done drawing entities, time to draw the player (*always* draw the
		// player last)
		// GL11.glEnable(GL11.GL_BLEND);
		// GL11.glDisable(GL11.GL_BLEND);

		/* END 3D DRAWING */

		/* BEGIN 2D DRAWING */
		// reset the projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		// set up an ortho view with a near of -1 and a far of 1 (so everything
		// at 0 is drawn)
		GL11.glOrtho(0, DisplayHelper.windowWidth, DisplayHelper.windowHeight,
				0, -1, 1);

		// reset the modelview matrix
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// enable blending and disable lighting
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);

		// draw debug info
		ConsoleManager.updateAndDraw();

		// draw 'PAUSED' in the middle of the screen if the game is paused
		if (paused)
			ConsoleManager.font.drawString((DisplayHelper.windowWidth / 2) - 25,
					DisplayHelper.windowHeight / 2, "PAUSED");

		/* END 2D DRAWING */
		
		// handle any GL errors that might occur
		//int error = GL11.GL_NO_ERROR;
		int error = GL11.glGetError();
		
		if(error != GL11.GL_NO_ERROR)
			System.out.println("Error rendering! Error number: " + error + " string: " + GLU.gluGetString(error));

	}
}
