package spaceguts.graphics.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import spaceguts.graphics.gui.GUI;
import spaceguts.graphics.gui.GUIObject;
import spaceguts.graphics.gui.button.MenuButton;
import spaceguts.graphics.gui.menu.picker.Picker;
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
	private Picker<XMLFile> picker;

	/** button to load the selected file */
	private MenuButton loadButton;

	/** button to go back to the main menu */
	private MenuButton backButton;

	/** whether or not to go back to the main menu on the next update */
	private boolean backToMainMenu;

	/**
	 * whether or not a file has been loaded (if it has, get rid of this menu
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
		picker = new Picker<XMLFile>(-50, -20, 20, 200, new XMLFile(path).listFiles(), Textures.MENU_PICKER_ACTIVE, Textures.MENU_PICKER_MOUSEOVER, Textures.MENU_PICKER_PRESSED, Textures.MENU_PICKER_SELECTED);

		// create load button
		loadButton = new MenuButton("Load", 119, 28, 115,
				-17);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(picker.itemHasBeenSelected()){
					Physics.initPhysics();
					
					// load entities from XML
					XMLParser.loadEntitiesFromXmlFile(picker.getSelectedItem().getPath());
					
					// create the pause menu
					GUI.addGUIObject(new PauseMenu());

					// raise the file loaded flag
					fileLoaded = true;
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
			GUI.removeGUIObject(this);
			
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
	
	@SuppressWarnings("serial")
	/**
	 * Since the Picker class uses toString to decide what to print out on each ListItem,
	 * but File's toString returns the whole path and not just the file name like we want.
	 * This class extends File and basically just overrides its toString to return just the
	 * name of the file.
	 * 
	 * For convenience, there's another inner class in this inner class that filters the files
	 * so that only files with ".xml" are listed.
	 * @author TranquilMarmot
	 *
	 */
	class XMLFile extends File{
		/**
		 * @param pathname Path to file
		 */
		public XMLFile(String pathname) {
			super(pathname);
		}
		
		/**
		 * @param file File to create XMLFile from
		 */
		public XMLFile(File file){
			this(file.getAbsolutePath());
		}
		
		@Override
		/**
		 * @return Just the file name! That is, everything between the last \ and the last .
		 */
		public String toString(){
			String path = this.getPath();
			
			int lastSlash = 0, dotIndex = 0;
			
			char[] chars = path.toCharArray();
			// find the last .
			for(int i = chars.length - 1; i >= 0; i--){
				if(chars[i] == '.'){
					dotIndex = i;
					break;
				}
			}
			
			// slash changes depending on operating system
			char slash;
			String opSys = System.getProperty("os.name").toLowerCase();
			if(opSys.contains("windows"))
				slash = '\\';
			else
				slash = '/';
			
			
			// find the last \
			for(int i = chars.length - 1; i >= 0; i--){
				if(chars[i] == slash){
					lastSlash = i;
					break;
				}
			}
			
			String fileName = path.substring(lastSlash + 1, dotIndex);
			
			return fileName;
		}
		
		@Override
		/**
		 * Lists all files ending with ".xml"
		 */
		public XMLFile[] listFiles(){
			File[] files = this.listFiles(new XMLFilter());
			
			XMLFile[] cast = new XMLFile[files.length];
			
			for(int i = 0; i < files.length; i++){
				cast[i] = new XMLFile(files[i]);
			}
			
			return cast;
		}
		
		/**
		 * Filters out classes not ending in ".xml"
		 * @author TranquilMarmot
		 *
		 */
		class XMLFilter implements FilenameFilter{
	        @Override
	        public boolean accept(File directory, String filename) {
	                CharSequence xml = ".xml";
	                if(filename.contains(xml))
	                        return true;
	                else
	                        return false;
	        }
		}
	}
}