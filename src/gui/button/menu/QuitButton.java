package gui.button.menu;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;

import util.Runner;

/**
 * This button quits the game
 * @author TranquilMarmot
 *
 */
public class QuitButton extends MenuButton{
	private static final String IMAGE_PATH = "res/images/gui/QuitButton/";

	/**
	 * Quit button constructor
	 * @param xOffset How far this button is from the center of the display along the X axis
	 * @param yOffset How far this button is from the center of the display along the Y axis
	 * @param height The height of the button
	 * @param width The width of the button
	 */
	public QuitButton(int xOffset, int yOffset, int height, int width) {
		super(xOffset, yOffset, height, width);
	}

	@Override
	/**
	 * Initialize all the QuitButton images
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
	 * Quit the game when the button is pressed
	 */
	public void releasedEvent() {
		Runner.done = true;
	}
	
}