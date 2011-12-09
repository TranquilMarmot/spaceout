package gui.menu;

import gui.GUI;
import gui.GUIObject;
import gui.button.MenuButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.Runner;

/**
 * Main menu to show when the game runs
 * 
 * @author TranquilMarmot
 * 
 */
public class MainMenu extends GUIObject {
	/** image path */
	private static final String IMAGE_PATH = "res/images/gui/MainMenu/Button/";
	
	private static final String XML_PATH = "res/XML/";

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
		startButton = new MenuButton(IMAGE_PATH, "Load File", 238, 55, 0, -40);

		// this button does some pretty srs stuff
		startButton.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			@Override
			public void actionPerformed(ActionEvent e) {
				LoadMenu lmenu = new LoadMenu(0, 0, XML_PATH);
				
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
		
		GUI.addBuffer.add(this);
		GUI.menuUp = true;
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
