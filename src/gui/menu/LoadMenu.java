package gui.menu;

import entities.Camera;
import entities.Entities;
import gui.GUI;
import gui.GUIObject;
import gui.button.MenuButton;
import gui.menu.filepicker.FilePicker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import physics.sandbox.Sandbox;
import util.debug.Debug;
import util.helper.DisplayHelper;
import util.manager.TextureManager;
import util.xml.XMLParser;

/**
 * Menu to load an XML file.
 * 
 * @author TranquilMarmot
 * 
 */
public class LoadMenu extends GUIObject {
	/** image path */
	private static final String BUTTON_IMAGE_PATH = "res/images/gui/Menu/Button/";

	private Texture background;

	/** The FilePicker to choose the file */
	private FilePicker picker;

	/** button to load the selected file */
	private MenuButton loadButton;

	/** button to go back to the main menu */
	private MenuButton backButton;

	/** whether or not to go back to the main menu on the next update */
	private boolean backToMainMenu;

	/**
	 * whether or not a file has been laoded (if it has, get rid of this menu
	 * and create the pause menu)
	 */
	private boolean fileLoaded;

	/**
	 * Load menu constructor
	 * 
	 * @param x
	 *            X location of the menu
	 * @param y
	 *            Y location of the menu
	 * @param path
	 *            The directory to look for files in
	 */
	public LoadMenu(int x, int y, final String path) {
		super(x, y);

		backToMainMenu = false;
		fileLoaded = false;

		// create file picker
		picker = new FilePicker(-50, -20, path);

		// create load button
		loadButton = new MenuButton(BUTTON_IMAGE_PATH, "Load", 119, 28, 115,
				-17);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String selectedFile = picker.getSelected().getFile();
					/* FIXME this if statement is only temporary!!! */
					if (selectedFile.equals("PhysicsSandbox.xml")) {
						Sandbox.createSandboxWorld();
					} else {
						// load entities from XML
						XMLParser.loadEntitiesFromXmlFile(path
								+ picker.getSelected().getFile());
					}
					// create the pause menu
					GUI.addBuffer.add(new PauseMenu());

					// initialize the camera
					Entities.camera = new Camera(Entities.player.location.x,
							Entities.player.location.y,
							Entities.player.location.z);
					Entities.camera.zoom = 10.0f;
					Entities.camera.yOffset = -2.5f;
					Entities.camera.xOffset = 0.1f;
					Entities.camera.following = Entities.player;

					// raise the file loaded flag
					fileLoaded = true;
				} catch (NullPointerException exc) {
					// if there's a NullPointerException, it means nothing has
					// been slected yet
				}
			}

		});

		// create the back to main menu button
		backButton = new MenuButton(BUTTON_IMAGE_PATH, "Back", 119, 28, 115, 17);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// raise the back to main menu flag
				backToMainMenu = true;
			}
		});

		// grab the image (NOTE: this is initialized in MainMenu's constructor)
		background = TextureManager.getTexture(TextureManager.BACKGROUND2);
	}

	@Override
	public void update() {
		picker.update();
		loadButton.update();
		backButton.update();

		if (backToMainMenu) {
			// remove the load menu
			GUI.removeBuffer.add(this);
			// add the main menu
			GUI.addBuffer.add(new MainMenu());
		}

		if (fileLoaded) {
			// remove the load menu
			GUI.removeBuffer.add(this);
			// let the GUI know that there's no menu up
			GUI.menuUp = false;
		}

	}

	@Override
	public void draw() {
		// draw the background
		background.bind();
		GL11.glBegin(GL11.GL_QUADS);
		{
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

		picker.draw();
		loadButton.draw();
		backButton.draw();

		Debug.drawVersion();
	}
}
