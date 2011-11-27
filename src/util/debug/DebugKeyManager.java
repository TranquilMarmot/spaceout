package util.debug;

import org.lwjgl.input.Keyboard;

import util.Screenshot;
import util.helper.DisplayHelper;
import util.helper.KeyboardHandler;

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
	
	// whether or not to close the console when line is submitted
	private static boolean autoClose = false;
	
	private static int backspaceRepeatCounter = 0;
	private static int backspaceRepeatWait = 30;
	
	protected static void updateKeys() {
		// debug button
		if (KeyboardHandler.debug && !debugDown) {
			Debug.displayDebug = !Debug.displayDebug;
			debugDown = true;
		}
		if (!KeyboardHandler.debug) {
			debugDown = false;
		}

		// console button
		if (KeyboardHandler.console && !consoleDown) {
			Debug.consoleOn = !Debug.consoleOn;
			autoClose = false;
			consoleDown = true;
		}	
		if (!KeyboardHandler.console) {
			consoleDown = false;
		}
		
		// command button
		if (KeyboardHandler.command && !commandDown) {
			if (Debug.consoleOn == false) {
				Debug.consoleOn = true;
				Debug.commandOn = true;
				autoClose = true;
			}
			commandDown = true;
		}
		if (!KeyboardHandler.command) {
			commandDown = false;
		}

		// chat button
		if (KeyboardHandler.chat && !chatDown) {
			if (Debug.consoleOn == false) {
				Debug.consoleOn = true;
				autoClose = true;
			}
			chatDown = true;
		}
		if (!KeyboardHandler.chat) {
			chatDown= false;
		}

		//TODO make all these go through KeyboardHandler instead
		// handle the enter key being pressed to submit a line
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && !returnDown) {
			Debug.console.submit();
			
			if (autoClose) {
				autoClose = false;
				Debug.consoleOn = false;
			}
			
			returnDown = true;
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			returnDown = false;
		}

		// handle backspace
		if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
			if(!backDown){
				Debug.console.backspace();
				backDown = true;
			} else{
				if(backspaceRepeatCounter < backspaceRepeatWait)
					backspaceRepeatCounter++;
				else if(backspaceRepeatCounter == backspaceRepeatWait)
					Debug.console.backspace();
			}
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_BACK)){
			backDown = false;
			backspaceRepeatCounter = 0;
		}

		// handle scrolling up and down in the console
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT) && !scrollUpDown) {
			Debug.console.scroll--;
			scrollUpDown = true;
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_NEXT))
			scrollUpDown = false;

		// handle scrolling up and down in the console
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR) && !scrollDownDown) {
			Debug.console.scroll++;
			scrollDownDown = true;
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_PRIOR))
			scrollDownDown = false;
		
		// screenshot button
		if (KeyboardHandler.screenshot && !screenShotDown) {
			Screenshot.takeScreenshot(DisplayHelper.windowWidth,
					DisplayHelper.windowHeight);
			screenShotDown = true;
		}
		if (!KeyboardHandler.screenshot)
			screenShotDown = false;
	}
}
