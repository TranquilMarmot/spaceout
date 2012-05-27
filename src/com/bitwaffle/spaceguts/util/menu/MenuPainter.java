package com.bitwaffle.spaceguts.util.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Map;

import com.bitwaffle.spaceguts.graphics.gui.button.MenuButton;

/**
 * Creates a Map of MenuButtons and ActionListeners based on the output of the MenuParser class.
 * @author arthurdent
 *
 */
public class MenuPainter {
	
	/*
	 * LinkedHashMap is used so that the order of the buttons can be maintained!
	 */
	
	private LinkedList<MenuButton> menuList;
	
	/* TODO: If we instantiate this somewhere else,
	 * it' won't get created for every menu in the game.
	 */
	private static MenuCommandMap menuCommandMap = new MenuCommandMap();
	
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
	 * Styles of menu. Vertical or Horizontal.
	 */
	public enum Style {
	    VERTICAL_MENU, HORIZONTAL_MENU
	}
	
	/**
	 * Update buttons
	 */
	public void update() {
		for (MenuButton button : menuList) {
			button.update();
		}
	}
	
	/**
	 * Draw buttons
	 */
	public void draw() {
		for (MenuButton button : menuList) {
			button.draw();
		}
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
			x = -1*((numButtons-1)*((HORIZ_MENU_BUTTON_WIDTH+HORIZ_MENU_SPACE)/2));
			y = startingHeight;
			heightModifier = 0;
			widthModifier = HORIZ_MENU_SPACE + HORIZ_MENU_BUTTON_WIDTH;
			break;
			
		}
	}
	
	/**
	 * Returns the menu. This might be useful for drawing/rendering loops,
	 * if you do them wrong...
	 * @return
	 */
	public LinkedList<MenuButton> getMenu() {
		return menuList;	
	}
	
	private void buildMenu(Map<String,String> rawMenu) {
		
		menuList = new LinkedList<MenuButton>();
		
		if (style == Style.HORIZONTAL_MENU ) {
			
		}
		
		for (Map.Entry<String, String> entry : rawMenu.entrySet()) {
			
			/* This should theoretically create a MenuCommand
			 * which can be accessed from the Action Listener
			 */
			final MenuCommand mc = menuCommandMap.getCommand(entry.getValue());
			
			MenuButton m;
			m = new MenuButton(entry.getKey(),w,h,x,y);
			m.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					mc.doCommand();
					
				}
			});
			
			y += heightModifier;
			x += widthModifier;
			
			menuList.add(m);
		}
	}
}