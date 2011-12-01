package gui.button;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import util.Runner;
import util.debug.Debug;
import util.helper.DisplayHelper;

/**
 * A button that stays in the same place regardless of window size! It does this by being offset from the center.
 * Example usage:
 * <code>
 * 	MenuButton button = new MenuButton("button", imageWidth, imageHeight, xOffset, yOffset);
 *	pauseButton.addActionListener(new ActionListener(){
 *		@Override
 *		public void actionPerformed(ActionEvent e) {
 *			//DO SOMETHING HERE
 *		}
 *	});
 *	GUI.guiObjects.add(pauseButton);
 *	</code>
 * @author TranquilMarmot
 *
 */
public class MenuButton extends RectangleButton {
	private static final String IMAGE_PATH = "res/images/gui/PauseMenuButton/";
	
	/** image this button uses */
	protected Texture activeImage, mouseOverImage, pressedImage, inactiveImage;

	/** the buttons current image */
	protected Texture currentImage;
	
	/** the button's offset from the center of the screen (to keep it in the same spot regardless of window size) */
	protected int xOffset, yOffset;
	
	public String text;

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
	public MenuButton(String text, int height, int width, int xOffsetFromCenter, int yOffsetFromCenter) {
		super(0, 0, width, height);
		this.xOffset = xOffsetFromCenter;
		this.yOffset = yOffsetFromCenter;
		this.text = text;
		initImages();
	}

	/**
	 * Initialize the images for this button; <code>activeImage</code>, <code>mouseOverImage</code>, <code>pressedImage</code> and <code>inactiveImage</code>
	 */
	protected void initImages(){
		try {
			activeImage = TextureLoader.getTexture("PNG", new FileInputStream(
					IMAGE_PATH + "active.png"), GL11.GL_NEAREST);
			mouseOverImage = TextureLoader.getTexture("PNG",
					new FileInputStream(IMAGE_PATH + "mouseover.png"),
					GL11.GL_NEAREST);
			pressedImage = TextureLoader.getTexture("PNG", new FileInputStream(
					IMAGE_PATH + "pressed.png"), GL11.GL_NEAREST);
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentImage = activeImage;
	}

	@Override
	/**
	 * What to do when the button is clicked and released (what the button does)
	 */
	public void releasedEvent(){
		buttonListener.actionPerformed(new ActionEvent(this, 1, "release"));
	}

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
		
		// this button is only visible when the game is paused (FIXME this should probably be somewhere else (PauseMenuButton?))
		if(!Runner.paused)
			isVisible = false;
		else
			isVisible = true;
	}

	@Override
	/**
	 * Draw the button
	 */
	public void draw() {
		if (this.isVisible) {
			//GL11.glDisable(GL11.GL_BLEND);
			currentImage.bind();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glBegin(GL11.GL_QUADS);
			{	
				/*
				 * currentImage.getWidth() and currentImage.getHeight() return the actual height of the texture.
				 * My best guess is that the image gets put into the smallest possible texture that has dimensions that are
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
			
			int textWidth = Debug.font.getWidth(text);
			//int textHeight = Debug.font.getHeight(text);
			int textHeight = Debug.font.getAscent();
			//System.out.println(text + " " + textWidth + " " + textHeight);
			
			int textX = this.x + ((this.width - textWidth) / 2);
			int textY = this.y + ((this.height - textHeight) / 2) - 2;
			
			Debug.font.drawString(textX, textY, text, Color.cyan);
			//GL11.glEnable(GL11.GL_BLEND);
		}
	}
}
