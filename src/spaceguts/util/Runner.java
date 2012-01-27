package spaceguts.util;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import spaceguts.entities.Entities;
import spaceguts.entities.Entity;
import spaceguts.entities.Light;
import spaceguts.graphics.glsl.GLSLRender;
import spaceguts.graphics.gui.GUI;
import spaceguts.physics.Physics;
import spaceguts.util.debug.Debug;
import spaceguts.util.input.KeyBindings;
import spaceguts.util.input.KeyboardManager;
import spaceguts.util.input.MouseManager;
import spaceguts.util.resources.ResourceLoader;

// Rule number 1: Tell everyone about Spaceout (ask them for ideas! We need ideas!).
// Rule number 2: Comment everything motherfucker.

/**
 * Initializes and runs the game.
 * 
 * @author TranquilMarmot
 */
public class Runner {
	/** what version of Spaceout is this? */
	public static final String VERSION = "0.0.70.8";

	/** prevents updates but still renders the scene */
	public static boolean paused = false;
	/** keeps the pause button from repeatedly pausing and unpausing */
	private boolean pauseDown = false;

	/** if this is true, it means it's time to shut down ASAP */
	public static boolean done = false;

	/** the keyboard and mouse handlers that need to be updated every frame */
	public static KeyboardManager keyboard = new KeyboardManager();
	public static MouseManager mouse = new MouseManager();

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
			while (!done) {
				// update everything
				update();
				// render the scene
				//Graphics.render();
				GLSLRender.render();
				//GLSLGraphics.render();
				// update the display (this swaps the buffers)
				Display.update();
				Display.sync(DisplayHelper.targetFPS);
			}
			shutdown();
		} catch (Exception e) {
			// if an exception is caught, destroy the display and the frame
			//shutdown();
			e.printStackTrace();
		}
	}

	/**
	 * Initialize OpenGL, variables, etc.
	 */
	private void init() {
		DisplayHelper.createWindow();
		
		Debug.init();
		//Graphics.initGL();		
		
		//MainMenu mainMenu = new MainMenu();
		//GUI.addGUIObject(mainMenu);
		
		GLSLRender.initGL();
		//GLSLGraphics.initGL();
		
		//initialize resources
		/*
		ResourceLoader.addJob(Textures.MENU_BACKGROUND1);
		ResourceLoader.addJob(Textures.MENU_BACKGROUND2);
		ResourceLoader.addJob(Textures.STARS);
		ResourceLoader.addJob(Textures.WHITE);
		ResourceLoader.addJob(Textures.CHECKERS);
		ResourceLoader.addJob(Textures.SHIP1);
		ResourceLoader.addJob(Textures.VENUS);
		ResourceLoader.addJob(Textures.MARS);
		ResourceLoader.addJob(Textures.MERCURY);
		ResourceLoader.addJob(Textures.EARTH);
		ResourceLoader.addJob(Textures.LASERBULLET);
		ResourceLoader.addJob(Models.LASERBULLET);
		ResourceLoader.addJob(Models.WING_X);
		ResourceLoader.addJob(Textures.MENU_PICKER_ACTIVE);
		ResourceLoader.addJob(Textures.MENU_PICKER_MOUSEOVER);
		ResourceLoader.addJob(Textures.MENU_PICKER_SELECTED);
		ResourceLoader.addJob(Textures.MENU_PICKER_PRESSED);
		ResourceLoader.addJob(Textures.MENU_BUTTON_ACTIVE);
		ResourceLoader.addJob(Textures.MENU_BUTTON_INACTIVE);
		ResourceLoader.addJob(Textures.MENU_BUTTON_MOUSEOVER);
		ResourceLoader.addJob(Textures.MENU_BUTTON_PRESSED);
		ResourceLoader.addJob(Textures.MENU_SPACEOUT_TEXT);
		ResourceLoader.processJobs();
		*/
		
		Debug.printSysInfo();
		System.out.println("-------------------------------");
	}

	/**
	 * Updates everything
	 */
	private void update() {
		// update the mouse and keyboard handlers
		mouse.update();
		keyboard.update();

		// do pause logic
		pauseLogic();
		
		// check for window resizes
		DisplayHelper.resizeWindow();
		
		// update the GUI
		GUI.update();
		
		// update the physics engine
		if (!Runner.paused && Physics.dynamicsWorld != null)
			Physics.update();
		
		// update passive entities
		for (Entity ent : Entities.passiveEntities.values())
			ent.update();

		// update lights
		for (Light l : Entities.lights.values())
			l.update();

		// update camera
		if (Entities.camera != null)
			Entities.camera.update();

		// update skybox
		if (Entities.skybox != null)
			Entities.skybox.update();
		
		// check for any resources that need to be loaded
		if(ResourceLoader.jobsExist())
			ResourceLoader.processJobs();
	}

	/**
	 * Checks whether or not the game's paused boolean needs to be flipped
	 */
	private void pauseLogic() {
		// if pauseDown is true, it means that the pause button is being
		// held,
		// so it avoids repeatedly flipping paused when the key is held
		if (KeyBindings.SYS_PAUSE.isPressed() && !pauseDown) {
			paused = !paused;
			pauseDown = true;
		}

		if (!KeyBindings.SYS_PAUSE.isPressed()) {
			pauseDown = false;
		}

		// release the mouse if the game's paused or the console is on or the
		// menu is up
		//if (!paused && !Console.consoleOn && !GUI.menuUp)
		//	Mouse.setGrabbed(true);
		//else
			Mouse.setGrabbed(false);
	}

	/**
	 * To be called when the game is quit
	 */
	private void shutdown() {
		Display.destroy();
		DisplayHelper.frame.dispose();
	}
}
