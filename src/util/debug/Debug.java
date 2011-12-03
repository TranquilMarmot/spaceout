package util.debug;

import java.util.Formatter;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import util.Runner;
import util.debug.console.Console;
import util.helper.DisplayHelper;
import util.manager.TextureManager;
import entities.Entities;

/**
 * Handles drawing all the debug info. This class also contains the console
 * object, so whenever anything is printed Debug.console.print should be used
 * 
 * @author TranquilMarmot
 * 
 */
public class Debug {
	private static final String FONT_PATH = "res/fonts/";
	
	/** the current FPS */
	public static int currentFPS;

	// time at the last frame
	private static Long lastFrame = 0L;
	// last FPS time
	private static Long lastFPS = null;
	// counter to keep track of FPS
	private static int fpsCount;

	/** whether or not debug info is being displayed */
	public static boolean displayDebug = true;
	
	// whether or not the console is up
	public static boolean consoleOn = false;
	public static boolean commandOn = false;

	// call list to draw a rectangle behind the debug info
	private static int rectangleCallList = 0;

	/** the console */
	public static Console console = new Console();
	
	// font for printing stuff to the screen
	public static UnicodeFont font = null;
	
	public static void updateAndDraw() {
		// everything in this class is static so that it can be accessed whenever, so everything has to be initialized
		checkForInit();

		// update keys
		DebugKeyManager.updateKeys();

		Debug.console.updateAndDraw();

		if (displayDebug) {
			Debug.drawDebugInfo();
		}
		
		// draw 'PAUSED' in the middle of the screen if the game is paused
		if (Runner.paused)
			Debug.font.drawString((DisplayHelper.windowWidth / 2) - 25,
					DisplayHelper.windowHeight / 2, "PAUSED");
	}

	public static void drawDebugInfo() {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
		GL11.glCallList(rectangleCallList);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		// formats the coordinates
		Formatter coords = new Formatter();
		coords.format("x: %,09.3f%n" + "y: %,09.3f%n" + "z: %,09.3f%n",
				Entities.player.location.x, Entities.player.location.y,
				Entities.player.location.z);

		// draw the text
		font.drawString(3, 3, coords.toString(), Color.cyan);
		font.drawString(3, 59, "quatX: "
				+ Entities.player.rotation.x + "\nquatY: "
				+ Entities.player.rotation.y + "\nquatZ: "
				+ Entities.player.rotation.z + "\nquatW: "
				+ Entities.player.rotation.w, new Color(0, 123, 255));

		String cameraInfo = "zoom: " + Entities.camera.zoom;
		if (Entities.camera.vanityMode)
			cameraInfo += " (vanity)";
		font.drawString(3, 135, cameraInfo, Color.blue);
		
		// draw what version of Spaceout this is
		font.drawString(DisplayHelper.windowWidth - 70,
				font.getDescent() + 5, Runner.VERSION);
		
		// draw the current fps
		font.drawString(DisplayHelper.windowWidth - 70,
				font.getDescent() + 25, currentFPS + " fps");
	}

	/**
	 * Initialize's Debug's objects as needed
	 */
	@SuppressWarnings("unchecked")
	public static void checkForInit() {
		// initialize the font if this is the first draw
		if (font == null) {
			try {
				font = new UnicodeFont(FONT_PATH + "VeraMono.ttf", 15, false, false);
				font.addAsciiGlyphs();
				font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
				font.loadGlyphs();
			} catch (SlickException e) {
				System.out.println("Error initializing font!!!");
				e.printStackTrace();
			}
		}
		
		// initialize variables if this is the first draw
		if (lastFrame == null)
			lastFrame = getTime();
		if (lastFPS == null)
			lastFPS = getTime();
		updateFPS();

		if (rectangleCallList == 0) {
			rectangleCallList = GL11.glGenLists(1);

			GL11.glNewList(rectangleCallList, GL11.GL_COMPILE);
			{
				TextureManager.getTexture(TextureManager.WHITE).bind();
				GL11.glColor3f(0.07f, 0.07f, 0.07f);
				GL11.glBegin(GL11.GL_QUADS);
				{
					GL11.glVertex2f(0.0f, 0.0f);
					GL11.glVertex2f(190.0f, 0.0f);
					GL11.glVertex2f(190.0f, 155.0f);
					GL11.glVertex2f(0.0f, 155.0f);
				}
				GL11.glEnd();
			}
			GL11.glEndList();
		}
	}

	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	private static void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			currentFPS = fpsCount;
			fpsCount = 0;
			lastFPS += 1000;
		}
		fpsCount++;
	}

	public static int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;

		return delta;
	}

}
