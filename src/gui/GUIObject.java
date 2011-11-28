package gui;

import gui.button.Button;

/**
 * Everything that the GUI uses should extend this class.
 * @author TranquilMarmot
 * @see Button
 * @see GUI
 *
 */
public abstract class GUIObject {
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
