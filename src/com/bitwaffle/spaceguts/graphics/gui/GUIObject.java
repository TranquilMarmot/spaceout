package com.bitwaffle.spaceguts.graphics.gui;

import com.bitwaffle.spaceguts.graphics.gui.button.Button;

/**
 * Everything that the GUI uses should extend this class.
 * @author TranquilMarmot
 * @see Button
 * @see GUI
 *
 */
public abstract class GUIObject {
	/** whether or not the object is visible */
	public boolean isVisible;
	
	/** top left of the object */
	public int x, y;
	
	/**
	 * GUIObject constructor
	 * @param x Initial X position
	 * @param y Initial Y position
	 */
	public GUIObject(int x, int y){
		this.x = x;
		this.y = y;
		isVisible = true;
	}
	
	/**
	 * Updates this object. Should set the <code>mouseOver</code> variable depending on whether or not the mouse is over the object.
	 */
	public abstract void update();
	
	/**
	 * Draws the object.
	 */
	public abstract void draw();
}
