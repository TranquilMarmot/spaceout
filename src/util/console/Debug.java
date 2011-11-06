package util.console;

import java.util.Formatter;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import util.helper.DisplayHelper;
import util.helper.TextureHandler;
import entities.Entities;

public class Debug {
	//the current FPS
	public static int currentFPS;
	// time at the last frame
	private static Long lastFrame = 0L;
	// last FPS time
	private static Long lastFPS = null;
	// counter to keep track of FPS
	private static int fpsCount;

	// whether or not debug info is being displayed
	public static boolean displayDebug = true;

	// font for printing stuff to the screen
	public static UnicodeFont text = null;
	
	private static int rectangleCallList = 0;

	protected static void drawDebugInfo() {
		
		GL11.glCallList(rectangleCallList);
		
		// formats the coordinates
		Formatter coords = new Formatter();
		coords.format("x: %,09.3f%n" + "y: %,09.3f%n" + "z: %,09.3f%n",
				Entities.player.location.x, Entities.player.location.y,
				Entities.player.location.z);

		// formats the rotations
		/*
		 * Formatter rots = new Formatter(); rots.format("yaw:   %05.1f%n" +
		 * "pitch: %05.1f%n" + "roll:  %05.1f%n", Entities.player.yaw,
		 * Entities.player.pitch, Entities.player.roll);
		 */
		// draw the text
		text.drawString(3, 3, coords.toString(), Color.cyan);
		text.drawString(3, 59, "quatX: " + Entities.player.rotation.x
				+ "\nquatY: " + Entities.player.rotation.y + "\nquatZ: "
				+ Entities.player.rotation.z + "\nquatW: "
				+ Entities.player.rotation.w, new Color(0, 123, 255));
		// text.drawString(10, 74, rots.toString());

		String cameraInfo = "zoom: " + Entities.camera.zoom;
		if(Entities.camera.vanityMode)
			cameraInfo += " (vanity)";
		text.drawString(3, 135, cameraInfo, Color.blue);

		text.drawString(DisplayHelper.windowWidth - 70, text.getDescent() + 5,
				currentFPS + " fps");


	}
	
	/**
	 * Initialize's Debug's objects as needed
	 */
	@SuppressWarnings("unchecked")
	protected static void checkForInit() {
		// initialize variables if this is the first draw
		if (lastFrame == null)
			lastFrame = getTime();
		if (lastFPS == null)
			lastFPS = getTime();
		updateFPS();

		// initialize the font if this is the first draw
		if (text == null) {
			try {
				text = new UnicodeFont("res/VeraMono.ttf", 15, false, false);
				text.addAsciiGlyphs();
				text.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
				text.loadGlyphs();
			} catch (SlickException e) {
				System.out.println("Error initializing font!!!");
				e.printStackTrace();
			}
		}
		
		if(rectangleCallList == 0){
			rectangleCallList = GL11.glGenLists(1);
			
			GL11.glNewList(rectangleCallList, GL11.GL_COMPILE);{
				TextureHandler.getTexture(TextureHandler.WHITE).bind();
				GL11.glColor3f(0.07f, 0.07f, 0.07f);
				GL11.glBegin(GL11.GL_QUADS);{
					GL11.glVertex2f(0.0f, 0.0f);
					GL11.glVertex2f(190.0f, 0.0f);
					GL11.glVertex2f(190.0f, 155.0f);
					GL11.glVertex2f(0.0f, 155.0f);
				}GL11.glEnd();
			}GL11.glEndList();
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
