package com.bitwaffle.spaceguts.util.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.graphics.gui.button.MenuButton;
import com.bitwaffle.spaceguts.graphics.gui.menu.LoadMenu;
import com.bitwaffle.spaceguts.graphics.gui.menu.MenuTester;

/**
 * Creates a Map of MenuButtons and ActionListeners based on the output of the MenuParser class.
 * @author arthurdent
 *
 */
public class MenuPainter {
	
	/*
	 * LinkedHashMap is used so that the order of the buttons can be maintained!
	 */
	
	private Map<MenuButton, ActionListener> menuMap;
	
	private static final String XML_PATH = "res/XML/";
	
	/* Height and width of vertical menu buttons */
	private static final int VERT_MENU_BUTTON_WIDTH = 238;
	private static final int VERT_MENU_BUTTON_HEIGHT = 55;
	private static final int VERT_MENU_SPACE = 35;
	
	/* Height and width of horizontal menu buttons */
	private static final int HORIZ_MENU_BUTTON_WIDTH = 119;
	private static final int HORIZ_MENU_BUTTON_HEIGHT = 28;
	private static final int HORIZ_MENU_SPACE = 30;
	
	private int w, h, x, y, heightModifier, widthModifier, startingHeight, numButtons;
	private Style style;

	/**
	 * Create a new map.
	 * @param rawMenu The raw Map<String,String> given by the MenuParser
	 * @param style Horizontal menu layout or Vertical menu layout.
	 * @param startingHeight How high you want the buttons to be on the screen.
	 */
	public MenuPainter(Map<String,String> rawMenu, Style style, int startingHeight) {
		this.startingHeight = startingHeight;
		this.style = style;
		this.numButtons = rawMenu.size();
		this.setStyle(style);
		y = startingHeight;
		this.buildMenu(rawMenu);
	}
	
	/**
	 * Returns the menu. This is useful for drawing/rendering loops.
	 * @return
	 */
	public Map<MenuButton, ActionListener> getMenu() {
		return menuMap;	
	}
	
	/**
	 * Styles of menu. Vertical or Horizontal.
	 */
	public enum Style {
	    VERTICAL_MENU, HORIZONTAL_MENU
	}
	
	/**
	 * Sets the way the buttons are layed out. Horizontal buttons are auto-centered.
	 * @param style Vertical or Horizontal menu layout
	 */
	private void setStyle(Style style) {
		switch (style) {
		case VERTICAL_MENU:
			w = VERT_MENU_BUTTON_WIDTH;
			h = VERT_MENU_BUTTON_HEIGHT;
			x = 0;
			y = startingHeight;
			heightModifier = VERT_MENU_SPACE + VERT_MENU_BUTTON_HEIGHT;
			widthModifier = 0;
			break;
		
		case HORIZONTAL_MENU: default:
			w = HORIZ_MENU_BUTTON_WIDTH;
			h = HORIZ_MENU_BUTTON_HEIGHT;
			x = ((numButtons-1)*(HORIZ_MENU_BUTTON_WIDTH/2+HORIZ_MENU_SPACE/2))*-1;
			y = startingHeight;
			heightModifier = 0;
			widthModifier = HORIZ_MENU_SPACE + HORIZ_MENU_BUTTON_WIDTH;
			break;
			
        }
	}
	
	private void buildMenu(Map<String,String> rawMenu) {
		
		menuMap = new LinkedHashMap<MenuButton, ActionListener>();
		
		if (style == Style.HORIZONTAL_MENU ) {
			
		}
		
		for (Map.Entry<String, String> entry : rawMenu.entrySet()) {
			MenuButton m;
			m = new MenuButton(entry.getKey(),w,h,x,y);
			
			y += heightModifier;
			x += widthModifier;
			
			menuMap.put(m, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Welcome to Spaceout!");
					// create the load menu
					LoadMenu lmenu = new LoadMenu(0, 0, XML_PATH);
					GUI.addGUIObject(lmenu);
	
					// let the GUI know that a menu is up
					GUI.menuUp = true;
	
					// done with the main menu
					MenuTester.done = true;
				}
			});

		}
	}
}