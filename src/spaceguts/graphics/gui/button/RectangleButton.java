package spaceguts.graphics.gui.button;

import org.lwjgl.util.Rectangle;

import spaceguts.util.manager.MouseManager;

/**
 * A rectangular button! To use this class, create a class and have it extend
 * this. Implement the <code>releasedEvent</code>,<code>mouseOverEvent</code>,
 * <code>pressedEvent</code>
 * 
 * @author TranquilMarmot
 * 
 */
public abstract class RectangleButton extends Button {
	/** rectangle representing this button */
	protected Rectangle rectangle;

	/** the button's size */
	public int height, width;

	/**
	 * RectangleButton constructor
	 * 
	 * @param x
	 *            Initial X Position
	 * @param y
	 *            Initial Y Position
	 * @param height
	 *            Height of button
	 * @param width
	 *            Width of button
	 */
	public RectangleButton(int x, int y, int height, int width) {
		super(x, y);
		this.height = height;
		this.width = width;
		rectangle = new Rectangle();
		rectangle.setX(x);
		rectangle.setY(y);
		rectangle.setWidth(width);
		rectangle.setHeight(height);
	}

	@Override
	/**
	 * This just changes all the state booleans (<code>mouseOver</code>, <code>pressed</code>, <code>released</code>)) according to the mouse's actions during the current frame.
	 * It then calls the appropriate methods (<code>mouseOverEvent</code>, <code>pressedEvent</code>, <code>releasedEvent</code>) based on the state of the button.
	 */
	public void update() {
		if (active && isVisible) {
			// check to see if the mouse is in the rectangle
			if (rectangle.contains(MouseManager.x, MouseManager.y)) {
				mouseOver = true;
			} else {
				mouseOver = false;
			}

			// the mouse is over the button, so it can be clicked
			if (mouseOver) {
				// test to see if the user is clicking
				if (MouseManager.button0) {
					pressed = true;
					// Mouse is released after button was pressed
				} else if (pressed && !released) {
					released = true;
				}

				if (!MouseManager.button0) {
					pressed = false;
				}
				// the button was pressed, then the mouse was moved off of it
				// but
				// the mouse button was held down
			} else if (pressed && MouseManager.button0) {
				pressed = true;
			} else {
				pressed = false;
			}

			if (released) {
				// take care of release event (changing something in-game)
				releasedEvent();
				released = false;
			}

			activeEvent();

			if (mouseOver) {
				mouseOverEvent();
				// take care of mouseover event (changing image)
			}

			if (pressed) {
				pressedEvent();
				// take care of pressed event (change image)- actual button work
				// is done on release
			}
		} else {
			inactiveEvent();
		}

	}

	/**
	 * What happens when the button is active (change the image)
	 */
	public abstract void activeEvent();

	/**
	 * What happens when the button is inactive
	 */
	public abstract void inactiveEvent();

	/**
	 * When implementing this, make sure you set <code>released</code> to
	 * <code>false</code> at the end
	 */
	public abstract void releasedEvent();

	/**
	 * This should probably just change the image/color of the button
	 */
	public abstract void mouseOverEvent();

	/**
	 * This should probably just change the image/color of the button
	 */
	public abstract void pressedEvent();

	@Override
	/**
	 * Probably a good idea to override this if you're extending RectangleButton
	 */
	public abstract void draw();

}
