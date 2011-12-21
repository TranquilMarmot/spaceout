package graphics.gui.button;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import util.debug.Debug;
import util.helper.DisplayHelper;

/**
 * A button that stays in the same place regardless of window size! It does this by being offset from the center.
 * Example usage:
 * <br>
 * <code>
 * 	MenuButton button = new MenuButton("button", imageWidth, imageHeight, xOffset, yOffset);
 *	pauseButton.addActionListener(new ActionListener(){
 *		public void actionPerformed(ActionEvent e) {
 *			//DO SOMETHING HERE
 *		}
 *	});
 *	GUI.guiObjects.add(pauseButton);
 *	</code>
 *  <br>
 * This takes an imagePath string as an argument for the constructor. The given string should point to a folder that contains four images:
 * <ul>
 * 	<li>active.png</li>
 * 	<li>mouseover.png</li>
 * 	<li>pressed.png</li>
 * 	<li>disabled.png</li>
 * </ul>
 * @author TranquilMarmot
 *
 */
public class MenuButton extends RectangleButton {
	/** where the images for this button are */
	private String imagePath;
	
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
	public MenuButton(String imagePath, String text, int height, int width, int xOffsetFromCenter, int yOffsetFromCenter) {
		super(0, 0, width, height);
		this.imagePath = imagePath;
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
					imagePath + "active.png"), GL11.GL_NEAREST);
			mouseOverImage = TextureLoader.getTexture("PNG",
					new FileInputStream(imagePath + "mouseover.png"),
					GL11.GL_NEAREST);
			pressedImage = TextureLoader.getTexture("PNG", new FileInputStream(
					imagePath + "pressed.png"), GL11.GL_NEAREST);
			inactiveImage = TextureLoader.getTexture("PNG", new FileInputStream(imagePath + "inactive.png"), GL11.GL_NEAREST);
			
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
		currentImage = inactiveImage;
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
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		if (this.isVisible) {
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
			int textHeight = Debug.font.getAscent();
			
			int textX = this.x + ((this.width - textWidth) / 2);
			int textY = this.y + ((this.height - textHeight) / 2) - 2;
			
			Debug.font.drawString(textX, textY, text, Color.cyan);
		}
	}
}
