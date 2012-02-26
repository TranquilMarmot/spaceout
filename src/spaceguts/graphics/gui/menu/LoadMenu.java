package spaceguts.graphics.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import spaceguts.graphics.gui.GUI;
import spaceguts.graphics.gui.GUIObject;
import spaceguts.graphics.gui.button.MenuButton;
import spaceguts.graphics.gui.menu.filepicker.FilePicker;
import spaceguts.physics.Physics;
import spaceguts.util.Debug;
import spaceguts.util.DisplayHelper;
import spaceguts.util.xml.XMLParser;
import spaceout.resources.Textures;

/**
 * Menu to load an XML file.
 * 
 * @author TranquilMarmot
 * 
 */
public class LoadMenu extends GUIObject {
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
		loadButton = new MenuButton("Load", 119, 28, 115,
				-17);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Physics.initPhysics();
					
					String selectedFile = picker.getSelected().getFile();
						// load entities from XML
						XMLParser.loadEntitiesFromXmlFile(path + selectedFile);
					// create the pause menu
					GUI.addGUIObject(new PauseMenu());

					// raise the file loaded flag
					fileLoaded = true;
				} catch (NullPointerException exc) {
					// if there's a NullPointerException, it means nothing has
					// been slected yet
				}
			}

		});

		// create the back to main menu button
		backButton = new MenuButton("Back", 119, 28, 115, 17);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// raise the back to main menu flag
				backToMainMenu = true;
			}
		});

		// grab the image (NOTE: this is initialized in MainMenu's constructor)
		background = Textures.MENU_BACKGROUND2.texture();
	}

	@Override
	public void update() {
		picker.update();
		loadButton.update();
		backButton.update();

		if (backToMainMenu) {
			// remove the load menu
			GUI.guiObjects.remove(this.hashCode());
			
			// add the main menu
			GUI.addGUIObject(new MainMenu());
		}

		if (fileLoaded) {
			// remove the load menu
			GUI.removeGUIObject(this);
			
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
