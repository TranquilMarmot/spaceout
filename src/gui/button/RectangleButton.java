package gui.button;

import javax.vecmath.Color3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import util.manager.MouseManager;
import util.manager.TextureManager;

/**
 * A rectangular button! To use this class, create a class and have it extend this.
 * Implement the <code>releasedEvent</code>,<code>mouseOverEvent</code>, <code>pressedEvent</code>
 * 
 * @author TranquilMarmot
 * 
 */
public abstract class RectangleButton extends Button {
	/** rectangle representing this button */
	Rectangle rectangle;

	/** the button's size */
	public int height, width;

	private Color3f color;

	/**
	 * RectangleButton constructor
	 * @param x Initial X Position
	 * @param y Initial Y Position
	 * @param height Height of button
	 * @param width Width of button
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

		color = new Color3f(1.0f, 0.0f, 0.0f);
	}

	@Override
	/**
	 * This just changes all the state booleans (<code>mouseOver</code>, <code>pressed</code>, <code>released</code>)) according to the mouse's actions during the current frame.
	 * It then calls the appropriate methods (<code>mouseOverEvent</code>, <code>pressedEvent</code>, <code>releasedEvent</code>) based on the state of the button.
	 */
	public void update() {
		if(active){
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
			//Mouse is released after button was pressed
			} else if(pressed && !released){
				released = true;
			}
			
			if(!MouseManager.button0){
				pressed = false;
			}
			// the button was pressed, then the mouse was moved off of it but
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
			color.x = 0.0f;
			color.y = 0.0f;
			color.z = 1.0f;
		}

		if (pressed) {
			pressedEvent();
			// take care of pressed event (change image)- actual button work is done on release
			color.x = 0.0f;
			color.y = 1.0f;
			color.z = 0.0f;
		}
		} else{
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
	 * When implementing this, make sure you set <code>released</code> to <code>false</code> at the end
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
	public void draw() {
		//GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor3f(color.x, color.y, color.z);
		TextureManager.getTexture(TextureManager.CHECKERS).bind();
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x, y);

			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2i(x + width, y);

			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2i(x + width, y + height);

			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2i(x, y + height);
		}
		GL11.glEnd();
		//GL11.glEnable(GL11.GL_BLEND);
	}

}
