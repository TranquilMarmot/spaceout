package util;

import entities.Entities;
import graphics.render.Graphics;
import gui.GUI;
import gui.menu.MainMenu;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import util.debug.Debug;
import util.helper.DisplayHelper;
import util.manager.KeyboardManager;
import util.manager.MouseManager;

// Rule number 1: Tell everyone about Spaceout (ask them for ideas! We need ideas!).
// Rule number 2: Comment everything motherfucker.

/**
 * Initializes and runs the game.
 * 
 * @author TranquilMarmot
 */
public class Runner {
	/** what version of Spaceout is this? */
	public static final String VERSION = "0.0.11";

	/** prevents updates but still renders the scene */
	public static boolean paused = true;
	/** keeps the pause button from repeatedly pausing and unpausing */
	private boolean pauseDown = false;

	/** if either of this is true, it means it's time to shut down ASAP */
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
			while (!done && !DisplayHelper.closeRequested) {
				// check for window resizes
				DisplayHelper.resizeWindow();
				// update misc stuff (keyboard, mouse, etc.)
				update();
				// update all the entities
				updateEntities();
				// render the scene
				Graphics.renderAndUpdateEntities();
				// update the display (this swaps the buffers)
				Display.update();
				Display.sync(DisplayHelper.targetFPS);
			}
			shutdown();
		} catch (Exception e) {
			// if an exception is caught, destroy the display and the frame
			shutdown();
			e.printStackTrace();
		}
	}

	/**
	 * Initialize OpenGL, variables, etc.
	 */
	private void init() {
		DisplayHelper.createWindow();
		Graphics.initGL();

		/*
		 * NOTE: Most of the initializing is done on the main menu, see
		 * MainMenu.java
		 */
		MainMenu mainMenu = new MainMenu();
		GUI.guiObjects.add(mainMenu);
	}

	/**
	 * Updates everything
	 */
	private void update() {
		// System.out.println("delta: " + Debug.getDelta());
		// update the mouse and keyboard handlers
		mouse.update();
		keyboard.update();

		/* BEGIN PAUSE LOGIC */
		// only allow unpausing if there are entities
		if (Entities.entitiesExist()) {
			// if pauseDown is true, it means that the pause button is being
			// held,
			// so it avoids repeatedly flipping paused when the key is held
			if (KeyboardManager.pause && !pauseDown) {
				paused = !paused;
				pauseDown = true;
			}

			if (!KeyboardManager.pause) {
				pauseDown = false;
			}

			// release the mouse if the game's paused
			if (!paused && !Debug.consoleOn)
				Mouse.setGrabbed(true);
			else
				Mouse.setGrabbed(false);
		}
		/* END PAUSE LOGIC */

		DisplayHelper.doFullscreenLogic();
	}

	/**
	 * Updates all the entities
	 */
	private void updateEntities() {
		// these two are special so they're updated here
		if (Entities.player != null)
			Entities.player.update();

		if (Entities.camera != null)
			Entities.camera.update();

		/*
		 * the rest of the entities are updated right before they're rendered to
		 * decrease the number of loops through Entities.entities In the future,
		 * Entities.entities should have two threads that can operate on it-
		 * reading threads and a writing thread The reading threads would be
		 * used for rendering and collision detection, while the writing thread
		 * would be used for things like updating locations
		 */
	}

	/**
	 * To be called when the game is quit
	 */
	private void shutdown() {
		Display.destroy();
		DisplayHelper.frame.dispose();
	}
}
