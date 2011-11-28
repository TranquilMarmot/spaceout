package gui.button.menu;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;

import util.Runner;

public class PauseButton extends MenuButton {
	private static final String IMAGE_PATH = "res/images/gui/PauseButton/";

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
	public PauseButton(int x, int y, int height, int width) {
		super(x, y, height, width);
	}

	@Override
	/**
	 * Initialize PuaseButton images
	 */
	protected void initImages() {
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
	/**
	 * Resume when the button is let go
	 */
	public void releasedEvent() {
		Runner.paused = false;
	}
}
