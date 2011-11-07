package util.debug.console;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import util.Screenshot;
import util.debug.Debug;
import util.helper.DisplayHelper;
import util.helper.KeyboardHandler;

public class ConsoleManager {
	private static final String FONT_PATH = "res/fonts/";

	// whether or not the console is up
	public static boolean consoleOn = false;
	public static boolean commandOn = false;

	// whether or not debug info is being displayed
	public static boolean displayDebug = true;

	// font for printing stuff to the screen
	public static UnicodeFont font = null;

	// the console
	public static Console console = new Console();

	// Key listener booleans
	private static boolean debugDown = false;
	private static boolean consoleDown = false;
	private static boolean commandDown = false;
	private static boolean chatDown = false;
	private static boolean autoClose = false;
	private static boolean screenShotDown = false;
	private static boolean returnDown = false;
	private static boolean backDown = false;
	private static boolean scrollUpDown = false;
	private static boolean scrollDownDown = false;
	
	private static int backspaceRepeatCounter = 0;
	private static int backspaceRepeatWait = 30;

	public static void updateAndDraw() {
		// everything in this class is static so that it can be accessed whenever, so everything has to be initialized
		checkForInit();
		//Console.checkForInit();
		Debug.checkForInit();

		updateKeys();

		console.draw();

		if (displayDebug) {
			Debug.drawDebugInfo();
		}
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
	}
	
	private static void updateKeys() {
		// handle the debug button
		if (KeyboardHandler.debug && !debugDown) {
			ConsoleManager.displayDebug = !ConsoleManager.displayDebug;
			debugDown = true;
		}

		if (!KeyboardHandler.debug) {
			debugDown = false;
		}

		if (KeyboardHandler.console && !consoleDown) {
			ConsoleManager.consoleOn = !ConsoleManager.consoleOn;
			autoClose = false;
			consoleDown = true;
		}	
		
		if (!KeyboardHandler.console) {
			consoleDown = false;
		}
		
		if (KeyboardHandler.command && !commandDown) {
			if (consoleOn == false) {
				consoleOn = true;
				commandOn = true;
				autoClose = true;
			}
			commandDown = true;
		}
		
		if (!KeyboardHandler.command) {
			commandDown = false;
		}
		
		if (KeyboardHandler.chat && !chatDown) {
			if (consoleOn == false) {
				consoleOn = true;
				autoClose = true;
			}
			chatDown = true;
		}

		if (!KeyboardHandler.chat) {
			chatDown= false;
		}
		
		/* END DEBUG KEY HANDLING */

		if (KeyboardHandler.screenshot && !screenShotDown) {
			Screenshot.takeScreenshot(DisplayHelper.windowWidth,
					DisplayHelper.windowHeight);
			screenShotDown = true;
		}

		if (!KeyboardHandler.screenshot)
			screenShotDown = false;

		// handle the enter key being pressed to submit a line
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && !returnDown) {
			console.submit();
			
			if (autoClose) {
				autoClose = false;
				consoleOn = false;
			}
			
			returnDown = true;
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			returnDown = false;
		}

		
		
		// handle backspace
		if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
			if(!backDown){
				console.backspace();
				backDown = true;
			} else{
				if(backspaceRepeatCounter < backspaceRepeatWait)
					backspaceRepeatCounter++;
				else if(backspaceRepeatCounter == backspaceRepeatWait)
					console.backspace();
			}
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_BACK)){
			backDown = false;
			backspaceRepeatCounter = 0;
		}

		// handle scrolling up and down in the console
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT) && !scrollUpDown) {
			console.scroll--;
			scrollUpDown = true;
		}

		if (!Keyboard.isKeyDown(Keyboard.KEY_NEXT))
			scrollUpDown = false;

		// handle scrolling up and down in the console
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR) && !scrollDownDown) {
			console.scroll++;
			scrollDownDown = true;
		}

		if (!Keyboard.isKeyDown(Keyboard.KEY_PRIOR))
			scrollDownDown = false;
	}
}
