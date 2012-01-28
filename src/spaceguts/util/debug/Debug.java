package spaceguts.util.debug;

import java.awt.Font;
import java.util.Formatter;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import spaceguts.entities.Entities;
import spaceguts.util.DisplayHelper;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.Runner;
import spaceguts.util.console.Console;
import spaceguts.util.resources.Paths;

/**
 * Handles drawing all the debug info. This class also contains the console
 * object, so whenever anything is printed Debug.console.print should be used
 * 
 * @author TranquilMarmot
 * 
 */
public class Debug {
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
	// public static boolean consoleOn = false;
	// public static boolean commandOn = false;

	// call list to draw a rectangle behind the debug info
	private static int rectangleCallList = 0;

	// font for printing stuff to the screen
	public static UnicodeFont font = null;

	public static void update() {
		// update keys
		DebugKeyManager.updateKeys();
		
		Console.console.update();
		
		updateFPS();
	}

	public static void draw() {
		Console.console.draw();
		if (displayDebug) {
			drawDebugInfo();
		}
		
		// draw 'PAUSED' in the middle of the screen if the game is paused
		if (Runner.paused && Entities.entitiesExist())
			Debug.font.drawString((DisplayHelper.windowWidth / 2) - 25,
					DisplayHelper.windowHeight / 2, "PAUSED");
		
		/*
		Debug.font.drawString(5, DisplayHelper.windowHeight - 121, 
				"Left Click + Drag - Rotate X/Y\n" +
				"Right Click + Drag - Rotate X/Z\n" +
				"Middle Click + Drag - Move X/Y\n" +
				"Mouse Wheel - Move Z\n" +
				"Arrow Keys - Light Position\n" +
				"M - Change Model");
		*/
	}

	public static void drawDebugInfo() {
		// only draw if there's info to draw
		if (Entities.entitiesExist()) {
			// change blending and draw the rectangle
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			GL11.glCallList(rectangleCallList);

			// change blending for font drawing
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			// formats the coordinates
			if (Entities.camera != null) {
				Formatter coords = new Formatter();
				if (!Entities.camera.freeMode) {
					coords.format("x: %,09.3f%n" + "y: %,09.3f%n"
							+ "z: %,09.3f%n", Entities.player.location.x,
							Entities.player.location.y,
							Entities.player.location.z);
				} else {
					coords.format("x: %,09.3f%n" + "y: %,09.3f%n"
							+ "z: %,09.3f%n", Entities.camera.location.x,
							Entities.camera.location.y,
							Entities.camera.location.z);
				}

				// draw coordinates
				font.drawString(3, 3, coords.toString(), Color.cyan);

				Vector3f angles;
				if (!Entities.camera.freeMode)
					angles = QuaternionHelper
							.getEulerAnglesFromQuaternion(Entities.player.rotation);
				else
					angles = QuaternionHelper
							.getEulerAnglesFromQuaternion(Entities.camera.rotation);

				font.drawString(3, 59, "roll: " + angles.x + "\npitch: "
						+ angles.y + "\nyaw: " + angles.z, new Color(0, 123,
						255));

				Formatter zoom = new Formatter();
				zoom.format("zoom: %,04.2f", Entities.camera.zoom);

				// draw camera info
				String cameraInfo = zoom.toString();
				if (Entities.camera.vanityMode)
					cameraInfo += "\n(vanity)";
				else if (Entities.camera.freeMode)
					cameraInfo += "\n(free)";
				font.drawString(3, 114, cameraInfo, Color.blue);

				javax.vecmath.Vector3f linear = new javax.vecmath.Vector3f();
				Entities.player.rigidBody.getLinearVelocity(linear);
				float xSpeed = (linear.x * 100.0f) / 1000.0f;
				float ySpeed = (linear.y * 100.0f) / 1000.0f;
				float zSpeed = (linear.z * 100.0f) / 1000.0f;
				font.drawString(DisplayHelper.windowWidth - 125,
						DisplayHelper.windowHeight - 75, xSpeed + "\n" + ySpeed
								+ "\n" + zSpeed);
			}
		}

		drawVersion();

		// draw the current fps
		String fpsString = currentFPS + " fps";
		font.drawString(DisplayHelper.windowWidth - font.getWidth(fpsString) - 2, font.getDescent() + 16,
				currentFPS + " fps");
	}

	/**
	 * Draws what version the game is in the top left of the screen
	 */
	public static void drawVersion() {
		// draw what version of Spaceout this is
		font.drawString(DisplayHelper.windowWidth - font.getWidth(Runner.VERSION) - 3, font.getDescent() - 4,
				Runner.VERSION);
	}

	/**
	 * Initialize's Debug's objects as needed
	 */
	@SuppressWarnings("unchecked")
	public static void init() {
		GL20.glUseProgram(0);
		// initialize the font if this is the first draw
		if (font == null) {
			try {
				Font awtFont = new Font(Paths.FONT_PATH.path() + "VeraMono.ttf", Font.PLAIN, 15);
				font = new UnicodeFont(awtFont, 15, false,
						false);
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
				GL11.glColor3f(0.07f, 0.07f, 0.07f);
				GL11.glBegin(GL11.GL_QUADS);
				{
					GL11.glVertex2f(0.0f, 0.0f);
					GL11.glVertex2f(192.0f, 0.0f);
					GL11.glVertex2f(192.0f, 155.0f);
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

	public static void printSysInfo() {
		// print out which version of Spaceout this is
		System.out.println("Spaceout version " + Runner.VERSION);

		// print out LWJGL version, followed by whether the system is 32 or 64
		// bit
		System.out.print("LWJGL version " + Sys.getVersion());
		if (Sys.is64Bit())
			System.out.println(" (64 bit)");
		else
			System.out.println(" (32 bit)");

		// print out which version of OpenGL is being used
		String glVersion = GL11.glGetString(GL11.GL_VERSION);

		String glDriver = null;
		int i = glVersion.indexOf(' ');
		if (i != -1) {
			glDriver = glVersion.substring(i + 1);
			glVersion = glVersion.substring(0, i);
		}

		System.out.print("OpenGL version " + glVersion);
		if (glDriver != null)
			System.out.print("(" + glDriver + ")");
		System.out.println();
		
		String glslVersion = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
		System.out.println("GLSL version " + glslVersion);

		// print out info about the graphics card
		String glVendor = GL11.glGetString(GL11.GL_VENDOR);
		String glRenderer = GL11.glGetString(GL11.GL_RENDERER);
		System.out.println(glRenderer + " (" + glVendor + ")");
	}
}
