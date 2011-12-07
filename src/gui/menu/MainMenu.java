package gui.menu;

import entities.Camera;
import entities.Entities;
import gui.GUI;
import gui.GUIObject;
import gui.button.MenuButton;
import gui.button.PauseMenuButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.Runner;
import util.xml.XMLParser;

/**
 * Main menu to show when the game runs
 * 
 * @author TranquilMarmot
 * 
 */
public class MainMenu extends GUIObject {
	/** image path */
	private static final String IMAGE_PATH = "res/images/gui/MainMenu/Button/";

	/** whether or not we're done with the main menu */
	public static boolean done;

	/** button to press to start the game */
	private MenuButton startButton;

	/** button to quit */
	private MenuButton quitButton;

	/**
	 * Main menu constructor. Creates a startButton that loads from an XML file.
	 */
	public MainMenu() {
		super(0, 0);

		done = false;

		// create the start button
		startButton = new MenuButton(IMAGE_PATH, "Start", 238, 55, 0, -40);

		// this button does some pretty srs stuff
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// load entities from XML (TODO make a menu for selecting a
				// file)
				XMLParser
						.loadEntitiesFromXmlFile("res/XML/SolarSystemHalved.xml");

				// initialize the camera
				Entities.camera = new Camera(Entities.player.location.x,
						Entities.player.location.y, Entities.player.location.z);
				Entities.camera.zoom = 10.0f;
				Entities.camera.yOffset = -2.5f;
				Entities.camera.xOffset = 0.1f;
				Entities.camera.following = Entities.player;

				/* BEGIN PAUSE MENU BUILDING */
				// button to resume the game if it's paused
				PauseMenuButton pauseButton = new PauseMenuButton("resume",
						115, 39, -75, 50);
				pauseButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Runner.paused = false;
					}
				});
				GUI.addBuffer.add(pauseButton);

				// button to quit the game if it's paused
				PauseMenuButton quitButton = new PauseMenuButton("quit", 115,
						39, 75, 50);
				quitButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Runner.done = true;
					}
				});
				GUI.addBuffer.add(quitButton);
				/* END PAUSE MENU BUILDING */

				// we're done with the main menu if this button is pressed
				done = true;
			}
		});

		// create the main menu quit button
		quitButton = new MenuButton(IMAGE_PATH, "Exit", 238, 55, 0, 50);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runner.done = true;
			}
		});
	}

	@Override
	public void update() {
		// remove the main menu if we're done with it
		if (done) {
			GUI.removeBuffer.add(this);
		}

		// update the buttons
		startButton.update();
		quitButton.update();
	}

	@Override
	public void draw() {
		// draw the buttons
		startButton.draw();
		quitButton.draw();
	}
}
