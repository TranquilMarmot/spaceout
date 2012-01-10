package spaceguts.util.manager;

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
	private static Texture spirit;
	private static Texture saucer;
	
	private static final String BACKGROUND_IMAGE_PATH = "res/images/gui/Menu/Background/";
	
	private static Texture background1;
	private static Texture background2;

	public static final int EARTH = 0;
	public static final int STARS = 1;
	public static final int WHITE = 2;
	public static final int CHECKERS = 3;
	public static final int MERCURY = 4;
	public static final int VENUS = 5;
	public static final int MARS = 6;
	public static final int SHIP1 = 7;
	public static final int LASERBULLET = 8;
	public static final int BACKGROUND1 = 9;
	public static final int BACKGROUND2 = 10;
	public static final int SPIRIT = 11;
	public static final int SAUCER = 12;

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
		case BACKGROUND1:
			if(background1 == null)
				initTexture(BACKGROUND1);
			return background1;
		case BACKGROUND2:
			if(background2 == null)
				initTexture(BACKGROUND2);
			return background2;
		case SPIRIT:
			if(spirit == null)
				initTexture(SPIRIT);
			return spirit;
		case SAUCER:
			if(saucer == null)
				initTexture(SAUCER);
			return saucer;
		default:
			return null;
		}
	}

	/**
	 * Initializes the given texture
	 * @param texture Texture to initialize
	 */
	public static void initTexture(int texture) {
		try {
			switch (texture) {
			case EARTH:
				if(earth == null)
					earth = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "earthbig.jpg"), true, GL11.GL_NEAREST);
				break;
			case STARS:
				if(stars == null)
					stars = TextureLoader.getTexture("PNG", new FileInputStream(
						TEXTURE_PATH + "skybox.png"), true, GL11.GL_NEAREST);
				break;
			case WHITE:
				if(white == null)
					white = TextureLoader.getTexture("BMP", new FileInputStream(
						TEXTURE_PATH + "white.bmp"), true, GL11.GL_NEAREST);
				break;
			case CHECKERS:
				if(checkers == null)
					checkers = TextureLoader.getTexture("BMP", new FileInputStream(
						TEXTURE_PATH + "checkers.bmp"), true, GL11.GL_NEAREST);
				break;
			case MERCURY:
				if(mercury == null)
					mercury = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "mercury.jpg"), true, GL11.GL_NEAREST);
				break;
			case VENUS:
				if(venus == null)
					venus = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "venus.jpg"), true, GL11.GL_NEAREST);
				break;
			case MARS:
				if(mars == null)
					mars = TextureLoader.getTexture("JPG", new FileInputStream(
						TEXTURE_PATH + "mars.jpg"), true, GL11.GL_NEAREST);
				break;
			case SHIP1:
				if(ship1 == null)
					ship1 = TextureLoader.getTexture("PNG", new FileInputStream(TEXTURE_PATH + "ship1.png"));
				break;
			case LASERBULLET:
				if(laserbullet == null)
					laserbullet = TextureLoader.getTexture("PNG", new FileInputStream(TEXTURE_PATH + "laserbullet.png"));
				break;
			case BACKGROUND1:
				if(background1 == null)
					background1 = TextureLoader.getTexture("JPG", new FileInputStream(BACKGROUND_IMAGE_PATH + "apod1.jpg"), GL11.GL_NEAREST);
				break;
			case BACKGROUND2:
				if(background2 == null)
					background2 = TextureLoader.getTexture("JPG", new FileInputStream(BACKGROUND_IMAGE_PATH + "apod2.jpg"), GL11.GL_NEAREST);
				break;
			case SPIRIT:
				if(spirit == null)
					spirit = TextureLoader.getTexture("PNG", new FileInputStream(TEXTURE_PATH + "spirit.png"), GL11.GL_NEAREST);
				break;
			case SAUCER:
				if(saucer == null)
					saucer = TextureLoader.getTexture("PNG", new FileInputStream(TEXTURE_PATH + "saucer.png"), GL11.GL_NEAREST);
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
