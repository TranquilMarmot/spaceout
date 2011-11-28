package gui.button;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import util.Runner;
import util.helper.DisplayHelper;

public class PauseButton extends RectangleButton {
	private static final String IMAGE_PATH = "res/images/gui/PauseButton/";

	/** image this button uses */
	private Texture activeImage, mouseOverImage, pressedImage;

	private Texture currentImage;

	/**
	 * Constructor for a pause button
	 * 
	 * @param x
	 *            Initial x position
	 * @param y
	 *            Initial y position
	 * @param height
	 * @param width
	 */
	public PauseButton(int height, int width) {
		super(DisplayHelper.windowWidth / 2, DisplayHelper.windowHeight / 2,
				width, height);
		initImages();
	}

	private void initImages() {
		try {
			activeImage = TextureLoader.getTexture("PNG", new FileInputStream(
					IMAGE_PATH + "active.png"), GL11.GL_NEAREST);
			mouseOverImage = TextureLoader.getTexture("PNG",
					new FileInputStream(IMAGE_PATH + "mouseOver.png"),
					GL11.GL_NEAREST);
			pressedImage = TextureLoader.getTexture("PNG", new FileInputStream(
					IMAGE_PATH + "pressed.png"), GL11.GL_NEAREST);
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentImage = activeImage;
	}

	@Override
	public void update() {
		super.update();
		
		// keep the button in the middle of the screen
		this.x = (DisplayHelper.windowWidth / 2) - (this.width / 2) + 3;
		this.y = (DisplayHelper.windowHeight / 2) - (this.height / 2) + 50;
		this.rectangle.setX(this.x);
		this.rectangle.setY(this.y);
	}

	@Override
	public void releasedEvent() {
		Runner.paused = false;
		released = false;
	}

	@Override
	public void mouseOverEvent() {
		currentImage = mouseOverImage;
	}

	@Override
	public void pressedEvent() {
		currentImage = pressedImage;
	}

	@Override
	public void activeEvent() {
		currentImage = activeImage;
	}

	@Override
	public void inactiveEvent() {

	}

	@Override
	public void draw() {
		if (Runner.paused) {
			//GL11.glDisable(GL11.GL_BLEND);
			currentImage.bind();
			GL11.glBegin(GL11.GL_QUADS);
			{
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
