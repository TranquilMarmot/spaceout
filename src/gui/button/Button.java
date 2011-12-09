package gui.button;

import gui.GUIObject;

import java.awt.event.ActionListener;

/**
 * A clickable GUIObject
 * @author TranquilMarmot
 * @see RectangleButton
 *
 */
public abstract class Button extends GUIObject{
	/** whether or not the mouse is over the object. This should be set using the <code>update</code> method every frame. */
	public boolean mouseOver;
	
	/** whether or not the button is active */
	public boolean active;
	
	/** whether or not the button is being pressed right now */
	public boolean pressed;
	
	/** whether or not the button has just been released */
	public boolean released;
	
	protected ActionListener buttonListener;
	
	/**
	 * Button constructor
	 * @param x Initial X position
	 * @param y Initial Y position
	 */
	public Button(int x, int y) {
		super(x, y);
		mouseOver = false;
		active = true;
		pressed = false;
		released = false;
	}
	
	public void addActionListener(ActionListener listener){
		this.buttonListener = listener;
	}
}