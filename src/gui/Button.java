package gui;

/**
 * A clickable GUIObject
 * @author TranquilMarmot
 * @see RectangleButton
 *
 */
public abstract class Button extends GUIObject {
	/** whether or not the mouse is over the object. This should be set using the <code>update</code> method every frame. */
	public boolean mouseOver;
	
	/** whether or not the button is active */
	public boolean active;
	
	/**
	 * Button constructor
	 * @param x Initial X position
	 * @param y Initial Y position
	 */
	public Button(int x, int y) {
		super(x, y);
		mouseOver = false;
		active = false;
	}
	
}
