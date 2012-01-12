package spaceguts.util.debug;

import org.lwjgl.input.Keyboard;

import spaceguts.util.Screenshot;
import spaceguts.util.helper.DisplayHelper;
import spaceguts.util.manager.KeyboardManager;
import spaceguts.util.console.Console;

/**
 * Manages all the keys that {@link Debug} uses
 * @author TranquilMarmot
 * @author arthurdent
 * @see Debug
 *
 */
public class DebugKeyManager {
	// Booleans to keep buttons from repeating
	private static boolean debugDown = false;
	private static boolean consoleDown = false;
	private static boolean commandDown = false;
	private static boolean chatDown = false;
	private static boolean screenShotDown = false;
	private static boolean returnDown = false;
	private static boolean backDown = false;
	private static boolean scrollUpDown = false;
	private static boolean scrollDownDown = false;
	

	
	// make it so backspace can be held down
	private static int backspaceRepeatCounter = 0;
	private static int backspaceRepeatWait = 30;
	
	/**
	 * Update all the booleans that Debug uses
	 */
	protected static void updateKeys() {
		// debug button
		if (KeyboardManager.debug && !debugDown) {
			Debug.displayDebug = !Debug.displayDebug;
			debugDown = true;
		}
		if (!KeyboardManager.debug) {
			debugDown = false;
		}

		// console button
		if (KeyboardManager.console && !consoleDown) {
			Console.consoleOn = !Console.consoleOn;
			Console.console.autoClose = false;
			consoleDown = true;
		}	
		if (!KeyboardManager.console) {
			consoleDown = false;
		}
		
		// command button
		if (KeyboardManager.command && !commandDown) {
			if (Console.consoleOn == false) {
				Console.consoleOn = true;
				Console.commandOn = true;
				Console.console.autoClose = true;
			}
			commandDown = true;
		}
		if (!KeyboardManager.command) {
			commandDown = false;
		}

		// chat button
		if (KeyboardManager.chat && !chatDown) {
			if (Console.consoleOn == false) {
				Console.consoleOn = true;
				Console.console.autoClose = true;
			}
			chatDown = true;
		}
		if (!KeyboardManager.chat) {
			chatDown= false;
		}

		//TODO make all these go through KeyboardHandler instead
		// handle the enter key being pressed to submit a line
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && !returnDown) {
			Console.console.submit();
			
			returnDown = true;
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			returnDown = false;
		}

		// handle backspace
		if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
			if(!backDown){
				Console.console.backspace();
				backDown = true;
			} else{
				if(backspaceRepeatCounter < backspaceRepeatWait)
					backspaceRepeatCounter++;
				else if(backspaceRepeatCounter == backspaceRepeatWait)
					Console.console.backspace();
			}
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_BACK)){
			backDown = false;
			backspaceRepeatCounter = 0;
		}

		// handle scrolling up and down in the console
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT) && !scrollUpDown) {
			Console.console.scrollUp(1);
			scrollUpDown = true;
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_NEXT))
			scrollUpDown = false;

		// handle scrolling up and down in the console
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR) && !scrollDownDown) {
			Console.console.scrollDown(1);
			scrollDownDown = true;
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_PRIOR))
			scrollDownDown = false;
		
		// screenshot button
		if (KeyboardManager.screenshot && !screenShotDown) {
			Screenshot.takeScreenshot(DisplayHelper.windowWidth,
					DisplayHelper.windowHeight);
			screenShotDown = true;
		}
		if (!KeyboardManager.screenshot)
			screenShotDown = false;
	}
}
