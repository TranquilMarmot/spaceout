package com.bitwaffle.spaceguts.util;

import java.awt.Font;
import java.util.Formatter;

import org.lwjgl.Sys;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.input.KeyBindings;
import com.bitwaffle.spaceguts.util.console.Console;
import com.bitwaffle.spaceout.resources.Paths;


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

	/** time at the last frame */
	private static Long lastFrame = 0L;
	/** last FPS time */
	private static Long lastFPS = null;
	/** counter to keep track of FPS */
	private static int fpsCount;

	/** whether or not debug info is being displayed */
	public static boolean displayDebug = true;

	/** font for printing stuff to the screen */
	public static UnicodeFont font = null;
	
	/** make it so backspace can be held down for the console */
	private static int backspaceRepeatCounter = 0;
	public static int backspaceRepeatWait = 30;
	
	/** String formatters */
	private static Formatter cameraInfoFormatter, locationFormatter;

	/**
	 * Updates the console and the FPS
	 */
	public static void update() {
		checkKeys();
		Console.console.update();
		updateFPS();
	}
	
	/**
	 * Checks to see if any keys have been pressed and acts accordingly
	 */
	private static void checkKeys(){
		// debug key
		if(KeyBindings.SYS_DEBUG.pressedOnce())
			displayDebug = !displayDebug;
		
		// console key
		if(KeyBindings.SYS_CONSOLE.pressedOnce() && !GUI.menuUp){
			Console.consoleOn = !Console.consoleOn;
			Console.autoClose = false;
		}
		
		// command key
		if(KeyBindings.SYS_COMMAND.pressedOnce()){
			if (Console.consoleOn == false) {
				Console.consoleOn = true;
				Console.commandOn = true;
				Console.autoClose = true;
			}
		}
		
		// chat key
		if(KeyBindings.SYS_CHAT.pressedOnce()){
			if (Console.consoleOn == false) {
				Console.consoleOn = true;
				Console.autoClose = true;
			}
		}
		
		// console submit key
		if(KeyBindings.SYS_CONSOLE_SUBMIT.pressedOnce())
			Console.console.submit();
		
		// console scroll up
		if(KeyBindings.SYS_CONSOLE_SCROLL_UP.pressedOnce())
			Console.console.scrollUp(1);
		
		// console scroll down
		if(KeyBindings.SYS_CONSOLE_SCROLL_DOWN.pressedOnce())
			Console.console.scrollDown(1);
		
		// history scroll up
		if (KeyBindings.SYS_CONSOLE_PREVIOUS_COMMAND.pressedOnce())
			Console.console.commandHistory((byte)-1);
		
		// history scroll down
		if (KeyBindings.SYS_CONSOLE_NEXT_COMMAND.pressedOnce())
			Console.console.commandHistory((byte)1);
		
		// screenshot key
		if(KeyBindings.SYS_SCREENSHOT.pressedOnce())
			Screenshot.takeScreenshot(DisplayHelper.windowWidth,
					DisplayHelper.windowHeight);
		
		// backspace key
		// if the key is only pressed once (not held down), backspace
		if(KeyBindings.SYS_CONSOLE_BACKSPACE.pressedOnce()){
			Console.console.backspace();
		// if the key is being held down, increment the counter and backspace if counter is finished
		} else if(KeyBindings.SYS_CONSOLE_BACKSPACE.isPressed()){
			if(backspaceRepeatCounter < backspaceRepeatWait)
				backspaceRepeatCounter++;
			else if(backspaceRepeatCounter == backspaceRepeatWait)
				Console.console.backspace();
		} else{
			// set counter to 0 if key isn't being held down
			backspaceRepeatCounter = 0;
		}
	}
 
	/**
	 * Draws debug info, the console, the crosshair and the 'PAUSED' text
	 */
	public static void draw() {
		if (displayDebug) {
			drawDebugInfo();
			Console.console.draw();
		}
		
		// draw 'PAUSED' in the middle of the screen if the game is paused
		if (Runner.paused && Entities.entitiesExist())
			Debug.font.drawString((DisplayHelper.windowWidth / 2) - 25,
					DisplayHelper.windowHeight / 2, "PAUSED");
	}

	/**
	 * Draws debug info to the screen
	 */
	public static void drawDebugInfo() {
		// only draw if there's info to draw
		if (Entities.entitiesExist()) {
			// change blending and draw the rectangle in the top-left
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			GL11.glColor3f(0.07f, 0.07f, 0.07f);
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glVertex2f(0.0f, 0.0f);
				GL11.glVertex2f(192.0f, 0.0f);
				GL11.glVertex2f(192.0f, 155.0f);
				GL11.glVertex2f(0.0f, 155.0f);
			}
			GL11.glEnd();

			// change blending for font drawing
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			// formats the coordinates
			if (Entities.camera != null) {
				locationFormatter = new Formatter();
				if (!Entities.camera.freeMode) {
					locationFormatter.format("x: %,09.3f%n" + "y: %,09.3f%n"
							+ "z: %,09.3f%n", Entities.player.location.x,
							Entities.player.location.y,
							Entities.player.location.z);
				} else {
					locationFormatter.format("x: %,09.3f%n" + "y: %,09.3f%n"
							+ "z: %,09.3f%n", Entities.camera.location.x,
							Entities.camera.location.y,
							Entities.camera.location.z);
				}

				// draw coordinates
				font.drawString(3, 3, locationFormatter.toString(), Color.cyan);

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
				
				cameraInfoFormatter = new Formatter();

				if(Entities.camera.buildMode)
					cameraInfoFormatter.format("speed: %,04.2f", Entities.camera.speed);
				else
					cameraInfoFormatter.format("zoom: %,04.2f", Entities.camera.zoom);

				// draw camera info
				String cameraInfo = cameraInfoFormatter.toString();
				if(Entities.camera.buildMode)
					cameraInfo += "\n(build)";
				else if (Entities.camera.vanityMode)
					cameraInfo += "\n(vanity)";
				else if (Entities.camera.freeMode)
					cameraInfo += "\n(free)";
				font.drawString(3, 114, cameraInfo, Color.blue);
				
				if(Entities.camera.buildMode){
					String look;
					if(Entities.camera.builder.leftGrabbed || Entities.camera.builder.rightGrabbed)
						look = "Grabbed:      ";
					else
						look = "At crosshair: ";
					if(Entities.camera.builder.lookingAt != null){
						look += Entities.camera.builder.lookingAt.hashCode() + " | " + Entities.camera.builder.lookingAt.type + " | Mass: " + Entities.camera.builder.lookingAt.rigidBody.getInvMass();
					}
					font.drawString(100, 3, look, Color.green);
				}

				javax.vecmath.Vector3f linear = new javax.vecmath.Vector3f();
				Entities.player.rigidBody.getLinearVelocity(linear);
				
				float speed = linear.length();
				if(speed < 0.05f)
					speed = 0.0f;
				
				font.drawString(DisplayHelper.windowWidth - 125,
						DisplayHelper.windowHeight - 20, "Speed: " + Float.toString(speed));
			}
		}

		
		drawVersion();

		// draw the current fps
		String fpsString = currentFPS + " fps";
		font.drawString(DisplayHelper.windowWidth - font.getWidth(fpsString) - 2, font.getDescent() + 16,
				currentFPS + " fps");
		
		drawControls();
		
		if(Entities.player != null){
			String lockon = "Locked on to: ";
			if(Entities.player.lockon != null)
				lockon += Entities.player.lockon.type;
			font.drawString(200, 20, lockon);
		}
	}

	/**
	 * Draws what version the game is in the top right of the screen
	 */
	public static void drawVersion() {
		// draw what version of Spaceout this is
		font.drawString(DisplayHelper.windowWidth - font.getWidth(Runner.VERSION) - 3, font.getDescent() - 4,
				Runner.VERSION);
	}
	
	private static void drawControls(){
		String controls;
		
		if(Entities.camera != null && !Entities.camera.buildMode){
			controls = 
					"WASD - Accelerate\n" +
					"QE - Roll\n" +
					"Space - Ascend\n" + 
					"Shift - Descend\n" + 
					"F - Brake\n" + 
					"Z - Boost\n" +
					"B - Enter Build Mode\n" + 
					"C - Change Camera Mode";
		} else{
			controls = 
					"WASD - Move\n" +
					"QE - Roll\n" +
					"Space - Ascend\n" +
					"Shift - Descend\n" +
					"MouseWheel - Change Speed\n" +
					"Tab - Open Builder Menu\n" +
					"P - Add Random Planets\n" +
					"O - Add Random Diamonds\n" +
					"B - Exit Build Mode";
			
		}
		
		font.drawString(DisplayHelper.windowWidth - 220, DisplayHelper.windowHeight - font.getHeight(controls) - 20, controls);
	}

	/**
	 * Initialize's Debug's objects as needed
	 */
	@SuppressWarnings("unchecked")
	public static void init() {
		// initialize the font if this is the first draw
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

		// initialize variables if this is the first draw
		lastFrame = getTime();
		lastFPS = getTime();
		updateFPS();
	}

	/**
	 * For FPS
	 */
	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	/**
	 * Needs to be called every frame to update the FPS
	 */
	private static void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			currentFPS = fpsCount;
			fpsCount = 0;
			lastFPS += 1000;
		}
		fpsCount++;
	}

	/**
	 * @return How much time has passed since the last time getDelta() was called
	 */
	public static int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;

		return delta;
	}

	/**
	 * Prints all the info about the system to System.out
	 */
	public static void printSysInfo() {
		// print out which version of Spaceout this is
		System.out.println("Spaceout version " + Runner.VERSION);
		
		// print out JVM info
		String vmName = System.getProperty("java.vm.name");
		String vmVersion = System.getProperty("java.vm.version");
		String vmVendor = System.getProperty("java.vm.vendor");
		String jvm = vmName + " version " + vmVersion + " (" + vmVendor + ")";
		
		System.out.println(jvm);

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
		
		String alVersion = AL10.alGetString(AL10.AL_VERSION);
		String alVendor = AL10.alGetString(AL10.AL_VENDOR);
		System.out.println("OpenAL version " + alVersion + " (" + alVendor + ")");
	}
}
