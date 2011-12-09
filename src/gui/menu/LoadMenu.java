package gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.xml.XMLParser;

import gui.GUI;
import gui.GUIObject;
import gui.button.MenuButton;
import gui.menu.filepicker.FilePicker;

public class LoadMenu extends GUIObject{
	/** image path */
	private static final String BUTTON_IMAGE_PATH = "res/images/gui/MainMenu/Button/";
	
	private FilePicker picker;
	private MenuButton loadButton;
	private MenuButton backButton;
	
	private boolean backToMainMenu;
	private boolean fileLoaded;
	
	public LoadMenu(int x, int y, final String path) {
		super(x, y);
		
		backToMainMenu = false;
		fileLoaded = false;
		
		picker = new FilePicker(-50, -20, path);
		
		loadButton = new MenuButton(BUTTON_IMAGE_PATH, "Load", 119, 28, 115, -17);
		loadButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// load entities from XML (TODO make a menu for selecting a
				// file)
				XMLParser
						.loadEntitiesFromXmlFile(path + picker.getSelected().getFile());
				GUI.addBuffer.add(new PauseMenu());
				fileLoaded = true;
			}
			
		});
		
		
		backButton = new MenuButton(BUTTON_IMAGE_PATH, "Back", 119, 28, 115, 17);
		backButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				backToMainMenu = true;
			}
			
		});
		
		GUI.addBuffer.add(this);
		GUI.menuUp = true;
	}

	@Override
	public void update() {
		picker.update();
		loadButton.update();
		backButton.update();
		
		if(backToMainMenu){
			GUI.removeBuffer.add(this);
			GUI.addBuffer.add(new MainMenu());
		}
		
		if(fileLoaded){
			GUI.removeBuffer.add(this);
			GUI.menuUp = false;
		}
		
	}

	@Override
	public void draw() {
		picker.draw();
		loadButton.draw();
		backButton.draw();
	}
}
