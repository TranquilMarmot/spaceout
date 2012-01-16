package spaceguts.util.debug;

import spaceguts.util.DisplayHelper;
import spaceguts.util.Screenshot;
import spaceguts.util.console.Console;
import spaceguts.util.input.KeyBindings;

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
		if (KeyBindings.SYS_DEBUG.isPressed() && !debugDown) {
			Debug.displayDebug = !Debug.displayDebug;
			debugDown = true;
		}
		if (!KeyBindings.SYS_DEBUG.isPressed()) {
			debugDown = false;
		}

		// console button
		if (KeyBindings.SYS_CONSOLE.isPressed() && !consoleDown) {
			Console.consoleOn = !Console.consoleOn;
			Console.console.autoClose = false;
			consoleDown = true;
		}	
		if (!KeyBindings.SYS_CONSOLE.isPressed()) {
			consoleDown = false;
		}
		
		// command button
		if (KeyBindings.SYS_COMMAND.isPressed() && !commandDown) {
			if (Console.consoleOn == false) {
				Console.consoleOn = true;
				Console.commandOn = true;
				Console.console.autoClose = true;
			}
			commandDown = true;
		}
		if (!KeyBindings.SYS_COMMAND.isPressed()) {
			commandDown = false;
		}

		// chat button
		if (KeyBindings.SYS_CHAT.isPressed() && !chatDown) {
			if (Console.consoleOn == false) {
				Console.consoleOn = true;
				Console.console.autoClose = true;
			}
			chatDown = true;
		}
		if (!KeyBindings.SYS_CHAT.isPressed()) {
			chatDown= false;
		}

		//TODO make all these go through KeyboardHandler instead
		// handle the enter key being pressed to submit a line
		if (KeyBindings.SYS_CONSOLE_SUBMIT.isPressed() && !returnDown) {
			Console.console.submit();
			
			returnDown = true;
		}
		if (!KeyBindings.SYS_CONSOLE_SUBMIT.isPressed()) {
			returnDown = false;
		}

		// handle backspace
		if (KeyBindings.SYS_CONSOLE_BACKSPACE.isPressed()) {
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
		if (!KeyBindings.SYS_CONSOLE_BACKSPACE.isPressed()){
			backDown = false;
			backspaceRepeatCounter = 0;
		}

		// handle scrolling up and down in the console
		if (KeyBindings.SYS_CONSOLE_SCROLL_UP.isPressed() && !scrollUpDown) {
			Console.console.scrollUp(1);
			scrollUpDown = true;
		}
		if (!KeyBindings.SYS_CONSOLE_SCROLL_UP.isPressed())
			scrollUpDown = false;

		// handle scrolling up and down in the console
		if (KeyBindings.SYS_CONSOLE_SCROLL_DOWN.isPressed() && !scrollDownDown) {
			Console.console.scrollDown(1);
			scrollDownDown = true;
		}
		if (!KeyBindings.SYS_CONSOLE_SCROLL_DOWN.isPressed())
			scrollDownDown = false;
		
		// screenshot button
		if (KeyBindings.SYS_SCREENSHOT.isPressed() && !screenShotDown) {
			Screenshot.takeScreenshot(DisplayHelper.windowWidth,
					DisplayHelper.windowHeight);
			screenShotDown = true;
		}
		if (!KeyBindings.SYS_SCREENSHOT.isPressed())
			screenShotDown = false;
	}
}
