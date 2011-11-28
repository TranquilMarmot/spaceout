package gui.button.menu;

import gui.button.RectangleButton;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import util.Runner;
import util.helper.DisplayHelper;

/**
 * A button that stays in the same place regardless of window size!
 * To use, extend this class and implement <code>initImages</code> to create the required images,
 * <code>activeImage</code>, <code>mouseOverImage</code>, <code>pressedImage</code> and <code>inactiveImage</code>
 * @author TranquilMarmot
 *
 */
public abstract class MenuButton extends RectangleButton {
	/** image this button uses */
	protected Texture activeImage, mouseOverImage, pressedImage, inactiveImage;

	/** the buttons current image */
	protected Texture currentImage;
	
	/** the button's offset from the center of the screen (to keep it in the same spot regardless of window size) */
	protected int xOffset, yOffset;

	/**
	 * Constructor for a menu button
	 * 
	 * @param x
	 *            Initial x position
	 * @param y
	 *            Initial y position
	 * @param height
	 * @param width
	 */
	public MenuButton(int xOffsetFromCenter, int yOffsetFromCenter, int height, int width) {
		super(0, 0, width, height);
		this.xOffset = xOffsetFromCenter;
		this.yOffset = yOffsetFromCenter;
		initImages();
	}

	/**
	 * Initialize the images for this button; <code>activeImage</code>, <code>mouseOverImage</code>, <code>pressedImage</code> and <code>inactiveImage</code>
	 */
	protected abstract void initImages();

	@Override
	/**
	 * What to do when the button is clicked and released (what the button does)
	 */
	public abstract void releasedEvent();

	@Override
	/**
	 * Change the current image to the mouseover image
	 */
	public void mouseOverEvent() {
		currentImage = mouseOverImage;
	}

	@Override
	/**
	 * Change the current image to the pressed image
	 */
	public void pressedEvent() {
		currentImage = pressedImage;
	}

	@Override
	/**
	 * Change the current image to the active image
	 */
	public void activeEvent() {
		currentImage = activeImage;
	}

	@Override
	/**
	 * Change the current image to the inactive image
	 */
	public void inactiveEvent() {
		//TODO give this an inactive image!
		//currentImage = inactiveImage;
	}
	
	@Override
	/**
	 * Update the button's location so that it's always in the same spot regardless of window size
	 */
	public void update(){
		super.update();
		
		// keep the button in the middle of the screen
		this.x = (DisplayHelper.windowWidth / 2) - (this.width / 2) + xOffset;
		this.y = (DisplayHelper.windowHeight / 2) - (this.height / 2) + yOffset;
		this.rectangle.setX(this.x);
		this.rectangle.setY(this.y);
	}

	@Override
	/**
	 * Draw the button
	 */
	public void draw() {
		if (Runner.paused) {
			//GL11.glDisable(GL11.GL_BLEND);
			currentImage.bind();
			GL11.glBegin(GL11.GL_QUADS);
			{
				/*
				 * currentImage.getWidth() and currentImage.getHeight() return the actual height of the texture.
				 * My best guess is that the texture gets put into the smallest possible texture that has dimensions that are
				 * powers of 2 by Slick, because OpenGL can handle those much better.
				 */
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2i(x, y);

				GL11.glTexCoord2f(currentImage.getWidth(), 0);
				GL11.glVertex2i(x + width, y);

				GL11.glTexCoord2f(currentImage.getWidth(), currentImage.getHeight());
				GL11.glVertex2i(x + width, y + height);

				GL11.glTexCoord2f(0, currentImage.getHeight());
				GL11.glVertex2i(x, y + height);
			}
			GL11.glEnd();
			//GL11.glEnable(GL11.GL_BLEND);
		}
	}
}
