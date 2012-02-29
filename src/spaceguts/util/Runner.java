package spaceguts.util;

import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import spaceguts.graphics.gui.GUI;
import spaceguts.graphics.gui.menu.MainMenu;
import spaceguts.graphics.render.Graphics;
import spaceguts.input.KeyBindings;
import spaceguts.input.KeyboardManager;
import spaceguts.input.MouseManager;
import spaceguts.physics.Physics;
import spaceguts.util.console.Console;
import spaceout.resources.Models;
import spaceout.resources.ResourceLoader;
import spaceout.resources.Textures;

// Rule number 1: Tell everyone about Spaceout (ask them for ideas! We need ideas!).
// Rule number 2: Comment everything motherfucker.

/**
 * Initializes and runs the game.
 * 
 * @author TranquilMarmot
 */
public class Runner {
	/** what version of Spaceout is this? */
	public static final String VERSION = "0.0.76.3";

	/** prevents updates but still renders the scene */
	public static boolean paused = false;

	/** if this is true, it means it's time to shut down ASAP */
	public static boolean done = false;

	/** the keyboard and mouse handlers that need to be updated every frame */
	public static KeyboardManager keyboard = new KeyboardManager();
	public static MouseManager mouse = new MouseManager();

	/**
	 * @param args Can be given a home directory to use to look for natives in instead of using the default System.getProperty("user.home")
	 */
	public static void main(String[] args) {
		/*
		 *  NOTE
		 *  If there're no args, the game looks for natives in the folder the game is being run in.
		 *  This doesn't work when running in eclipse, because the game gets run from your workspace
		 *  
		 *  To run the game in eclipse, click the little arrow next to the Run arrow and
		 *  click "Run configurations..." then go to the "Arguments" tab
		 *  In "Program arguments:" put "${workspace_loc:spaceout/lib/natives/*YOUROS*}"
		 *  Then you should be able to run as usual.
		 */
		if(args.length == 0)
			System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/lib/natives");
		else{
			System.setProperty("org.lwjgl.librarypath", args[0]);
		}
		
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
				Graphics.render();
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
		Debug.printSysInfo();
		
		MainMenu mainMenu = new MainMenu();
		GUI.addGUIObject(mainMenu);
		
		Graphics.initGL();
		
		//initialize resources
		// TODO loading screen!
		ResourceLoader.addJob(Textures.MENU_BACKGROUND1);
		ResourceLoader.addJob(Textures.MENU_BACKGROUND2);
		ResourceLoader.addJob(Textures.SKYBOX);
		ResourceLoader.addJob(Textures.WHITE);
		//ResourceLoader.addJob(Textures.CHECKERS);
		ResourceLoader.addJob(Textures.WING_X);
		ResourceLoader.addJob(Textures.VENUS);
		ResourceLoader.addJob(Textures.MARS);
		ResourceLoader.addJob(Textures.MERCURY);
		ResourceLoader.addJob(Textures.EARTH);
		ResourceLoader.addJob(Textures.LASERBULLET);
		ResourceLoader.addJob(Models.LASERBULLET);
		ResourceLoader.addJob(Models.WESCOTT);
		ResourceLoader.addJob(Models.SAUCER);
		ResourceLoader.addJob(Models.SKYBOX);
		ResourceLoader.addJob(Textures.MENU_PICKER_ACTIVE);
		ResourceLoader.addJob(Textures.MENU_PICKER_MOUSEOVER);
		ResourceLoader.addJob(Textures.MENU_PICKER_SELECTED);
		ResourceLoader.addJob(Textures.MENU_PICKER_PRESSED);
		ResourceLoader.addJob(Textures.MENU_BUTTON_ACTIVE);
		ResourceLoader.addJob(Textures.MENU_BUTTON_INACTIVE);
		ResourceLoader.addJob(Textures.MENU_BUTTON_MOUSEOVER);
		ResourceLoader.addJob(Textures.MENU_BUTTON_PRESSED);
		ResourceLoader.addJob(Textures.MENU_SPACEOUT_TEXT);
		ResourceLoader.addJob(Textures.CROSSHAIR);
		ResourceLoader.addJob(Textures.BUILDER_GRABBED);
		ResourceLoader.addJob(Textures.BUILDER_OPEN);
		ResourceLoader.processJobs();
		
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
		if (!paused && Physics.dynamicsWorld != null)
			Physics.update();
		
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
		if (KeyBindings.SYS_PAUSE.pressedOnce()) {
			paused = !paused;
		}

		// release the mouse if the game's paused or the console is on or the
		// menu is up
		if (!paused && !Console.consoleOn && !GUI.menuUp)
			Mouse.setGrabbed(true);
		else
			Mouse.setGrabbed(false);
	}

	/**
	 * To be called when the game is quit
	 */
	private void shutdown() {
		Display.destroy();
		DisplayHelper.frame.dispose();
		System.out.println(goodbye());
	}
	
	/**
	 * This is a secret method that does secret things
	 * @return None of your business
	 */
	private String goodbye(){
		String[] shutdown = { "Goodbye, world...", "Goodbye, cruel world...", "See ya...", "Later...", "Buh-bye...", "Thank you, come again!...",
				"Until Next Time...", "�Adios, Amigo!...", "Game Over, Man! Game Over!!!...", "And So, I Bid You Adieu...", "So Long, And Thanks For All The Fish...",
				"Ciao...", "Y'all Come Back Now, Ya Hear?...", "Catch You Later!...", "Mahalo And Aloha...", "Sayonara...", "Thanks For Playing!...",
				"Auf Wiedersehen...", "Yo Homes, Smell Ya Later!... (Looked Up At My Kingdom, I Was Finally There, To Sit On My Throne As The Prince Of Bel-air)"};
		// FIRST FRESH PRINCE REFERENCE FOR THIS GAME, TAKE NOTE THIS IS HISTORIC
		
		return shutdown[new Random().nextInt(shutdown.length)];
	}
}
