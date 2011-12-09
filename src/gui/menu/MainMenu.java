package gui.menu;

import gui.GUI;
import gui.GUIObject;
import gui.button.MenuButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import util.Runner;
import util.helper.DisplayHelper;
import util.manager.TextureManager;

/**
 * Main menu to show when the game first runs
 * 
 * @author TranquilMarmot
 * 
 */
public class MainMenu extends GUIObject {
	/** button image path */
	private static final String BUTTON_IMAGE_PATH = "res/images/gui/Menu/Button/";

	/** XML path */
	private static final String XML_PATH = "res/XML/";

	/** whether or not we're done with the main menu */
	public static boolean done;

	/** button to press to start the game */
	private MenuButton loadMenuButton;

	/** button to quit */
	private MenuButton quitButton;
	
	private Texture background;

	/**
	 * Main menu constructor. Creates a startButton that loads from an XML file.
	 */
	public MainMenu() {
		super(0, 0);
		
		// initialize the background images
		initBackgroundImages();
		
		// grab the background
		background = TextureManager.getTexture(TextureManager.BACKGROUND1);

		done = false;

		// create the button to go to the load menu
		loadMenuButton = new MenuButton(BUTTON_IMAGE_PATH, "Load File", 238,
				55, 0, -40);
		loadMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// create the load menu
				LoadMenu lmenu = new LoadMenu(0, 0, XML_PATH);
				GUI.addBuffer.add(lmenu);

				// let the GUI know that a menu is up
				GUI.menuUp = true;

				// done with the main menu
				done = true;
			}
		});

		// create the main menu quit button
		quitButton = new MenuButton(BUTTON_IMAGE_PATH, "Exit", 238, 55, 0, 50);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// end the game
				Runner.done = true;
			}
		});

		// let the GUI know that a menu is up
		GUI.menuUp = true;
	}
	
	private void initBackgroundImages(){
		TextureManager.initTexture(TextureManager.BACKGROUND1);
		TextureManager.initTexture(TextureManager.BACKGROUND2);
	}

	@Override
	public void update() {
		// update the buttons
		loadMenuButton.update();
		quitButton.update();

		// remove the main menu if we're done with it
		if (done) {
			GUI.removeBuffer.add(this);
		}
	}

	@Override
	public void draw() {
		// draw the background
		background.bind();
		GL11.glBegin(GL11.GL_QUADS);{
			/*
			 * currentImage.getWidth() and currentImage.getHeight() return the
			 * actual height of the texture. My best guess is that the image
			 * gets put into the smallest possible texture that has dimensions
			 * that are powers of 2 by Slick, because OpenGL can handle those
			 * much better.
			 */
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(0, 0);

			GL11.glTexCoord2f(background.getWidth(), 0);
			GL11.glVertex2i(DisplayHelper.windowWidth, 0);

			GL11.glTexCoord2f(background.getWidth(), background.getHeight());
			GL11.glVertex2i(DisplayHelper.windowWidth,
					DisplayHelper.windowHeight);

			GL11.glTexCoord2f(0, background.getHeight());
			GL11.glVertex2i(0, DisplayHelper.windowHeight);
		}
		GL11.glEnd();

		// draw the buttons
		loadMenuButton.draw();
		quitButton.draw();
	}
}
