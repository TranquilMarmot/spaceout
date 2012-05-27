package com.bitwaffle.spaceguts.graphics.gui.menu;

import java.util.Map;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.graphics.gui.GUIObject;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.menu.MenuPainter;
import com.bitwaffle.spaceguts.util.menu.MenuPainter.Style;
import com.bitwaffle.spaceguts.util.xml.MenuParser;
import com.bitwaffle.spaceout.Runner;



/**
 * The pause menu!
 * @author arthurdent
 *
 */
public class PauseMenu extends GUIObject {
	
	/** whether or not to go back to the main menu on the next update */
	public static boolean backToMainMenu = false;
	
	private MenuPainter menuPainter;
	
	/**
	 * Pause menu constructor. Automatically adds the pause menu to GUI.guiObjects
	 */
	public PauseMenu() {
		super(0, 0);
		
		// Build the main menu from menus.xml
		MenuParser mp = new MenuParser("pause");
		Map<String,String> rawMenu = mp.getMenu();
		
		// Paint the menu to a hashmap
		menuPainter = new MenuPainter(rawMenu, Style.HORIZONTAL_MENU, -40);

	}

	@Override
	public void update() {
		
		menuPainter.update();
		
		if(backToMainMenu){
			Entities.cleanup();
			Physics.cleanup();
			GUI.removeGUIObject(this);
			GUI.addGUIObject(new MainMenu());
			backToMainMenu = false;
		}
	}

	@Override
	public void draw() {
		if (Runner.paused) {
			menuPainter.draw();
		}
	}
}
