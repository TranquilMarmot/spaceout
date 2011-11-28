package util.manager;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * Manages textures. This makes it so only one texture instance needs to be around but multiple entities can use it.
 * @author TranquilMarmot
 *
 */
public class TextureManager {
	private static final String TEXTURE_PATH = "res/images/textures/";
	
	private static Texture earth;
	private static Texture stars;
	private static Texture white;
	private static Texture checkers;
	private static Texture mercury;
	private static Texture venus;
	private static Texture mars;
	private static Texture ship1;
	private static Texture laserbullet;

	public static final int EARTH = 0;
	public static final int STARS = 1;
	public static final int WHITE = 2;
	public static final int CHECKERS = 3;
	public static final int MERCURY = 4;
	public static final int VENUS = 5;
	public static final int MARS = 6;
	public static final int SHIP1 = 7;
	public static final int LASERBULLET = 8;

	/**
	 * Returns the requested texture and initializes it if need be
	 * @param texture Integer reffering to the texture
	 * @return The requested texture
	 */
	public static Texture getTexture(int texture) {
		switch (texture) {
		case EARTH:
			if (earth == null)
				initTexture(EARTH);
			return earth;
		case STARS:
			if (stars == null)
				initTexture(STARS);
			return stars;
		case WHITE:
			if (white == null)
				initTexture(WHITE);
			return white;
		case CHECKERS:
			if (checkers == null)
				initTexture(CHECKERS);
			return checkers;
		case MERCURY:
			if(mercury == null)
				initTexture(MERCURY);
			return mercury;
		case VENUS:
			if(venus == null)
				initTexture(VENUS);
			return venus;
		case MARS:
			if(mars == null)
				initTexture(MARS);
			return mars;
		case SHIP1:
			if(ship1 == null)
				initTexture(SHIP1);
			return ship1;
		case LASERBULLET:
			if(laserbullet == null)
				initTexture(LASERBULLET);
			return laserbullet;
		default:
			return null;
		}
	}

	/**
	 * Initializes the given texture
	 * @param texture Texture to initialize
	 */
	private static void initTexture(int texture) {
		try {
			switch (texture) {
			case EARTH:
				earth = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "earthbig.jpg"), true, GL11.GL_LINEAR);
				break;
			case STARS:
				stars = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "stars.jpg"), true, GL11.GL_LINEAR);
				break;
			case WHITE:
				white = TextureLoader.getTexture("BMP", new FileInputStream(
						TEXTURE_PATH + "white.bmp"), true, GL11.GL_LINEAR);
				break;
			case CHECKERS:
				checkers = TextureLoader.getTexture("BMP", new FileInputStream(
						TEXTURE_PATH + "checkers.bmp"), true, GL11.GL_LINEAR);
				break;
			case MERCURY:
				mercury = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "mercury.jpg"), true, GL11.GL_LINEAR);
				break;
			case VENUS:
				venus = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "venus.jpg"), true, GL11.GL_LINEAR);
				break;
			case MARS:
				mars = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "mars.jpg"), true, GL11.GL_LINEAR);
				break;
			case SHIP1:
				ship1 = TextureLoader.getTexture("PNG", new FileInputStream(TEXTURE_PATH + "ship1.png"));
				break;
			case LASERBULLET:
				laserbullet = TextureLoader.getTexture("PNG", new FileInputStream(TEXTURE_PATH + "laserbullet.png"));
				break;
			default:
				System.out.println("Error creating texture! Parameter "
						+ texture
						+ " doesn't match any known texture (TextureHandler)");
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
