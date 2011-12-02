package util.debug.console;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import util.debug.Debug;
import util.helper.DisplayHelper;
import util.manager.MouseManager;
import util.manager.TextureManager;

/**
 * Console for printing text and interacting with the game. Note that there should only be one console at any time.
 * @author TranquilMarmot
 *
 */
public class Console {
	// if blink is true, there's an underscore at the end of the input string,
	// else there's not
	private boolean blink = true;
	// counter for blink
	private static int blinkCount = 0;
	// how often to blink (this is changed to match the current FPS)
	private static int blinkInterval = 30;
	
	/** font for printing stuff to the screen */
	public static UnicodeFont font = null;

	// location to draw the console at
	private int x = 10;
	private int y = 0;
	
	// whether or not to close the console when line is submitted
	public boolean autoClose = false;

	// number of lines to print
	private int numLines = 14;

	// if scroll is 0, we're at the most recent line, 1 is one line up, 2 is two
	// lines up, etc
	private int scroll = 0;

	// the text being typed into the console
	public static String input = "";

	// all the text that the console contains and will print out
	public ArrayList<String> text = new ArrayList<String>();
	
	/**
	 * Updates and draws the console
	 */
	public void updateAndDraw() {
		// scroll with the mouse wheel
		scroll += MouseManager.wheel / 100;
		// how tall each line is
		int advanceY = Debug.font.getAscent();
		// where to draw the console (x stays at 10)
		y = DisplayHelper.windowHeight - advanceY - 10;

		// figure out how many lines to print out
		int stringsToPrint = text.size() - (numLines + 1);
		// avoid any possibility of out of bounds (the for loop is kind of
		// weird. if stringsToPrint == -1, then it doesn't print out any lines)
		if (stringsToPrint < 0)
			stringsToPrint = -1;

		// keep scroll from getting too big or too small
		if (scroll < 0)
			scroll = 0;
		if (scroll > text.size() - numLines && text.size() > numLines)
			scroll = text.size() - numLines; 

		// do blinking effect
		updateBlink();

		// check for command
		if (Debug.commandOn) {
			input = "/";
			Debug.commandOn = false; 
		}
		
		// draw the input text
		if (Debug.consoleOn) {
			
			// draw a semi-transparent box behind the console text
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			TextureManager.getTexture(TextureManager.WHITE).bind();
			GL11.glColor4f(0.03f, 0.03f, 0.03f, 1.0f);
			GL11.glBegin(GL11.GL_QUADS);{
				GL11.glVertex2f(0.0f, DisplayHelper.windowHeight);
				GL11.glVertex2f(0.0f, DisplayHelper.windowHeight - 225.0f);
				GL11.glVertex2f(600, DisplayHelper.windowHeight - 225.0f);
				GL11.glVertex2f(600, DisplayHelper.windowHeight);
			}GL11.glEnd();
			
			// print out however many strings, going backwards (we want the latest
			// strings to be printed first)
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			for (int i = text.size() - 1 - scroll; i > stringsToPrint - scroll; i--) {
				// break if we go out of the array
				if (i < 0)
					break;
				// which line we're at in the console itself
				int line = text.size() - (i + scroll);
				// draw the string, going up on the y axis by how tall each line is
				Debug.font.drawString(x, y - (advanceY * line), text.get(i), new Color(38, 255, 0));
			}
			
			String toPrint = "> " + input;
			if (blink) 
				toPrint += "_";
			Debug.font.drawString(x, y, toPrint, new Color(38, 255, 0));
		}
		
	}
	
	/**
	 * This gets called every time that the console is drawn to make the
	 * underscore at the end of the input blink
	 */
	private void updateBlink() {
		// blink twice every second
		blinkInterval = Debug.currentFPS / 2;

		if (blink) {
			blinkCount++;
			if (blinkCount >= blinkInterval)
				blink = false;
		} else {
			blinkCount--;
			if (blinkCount <= 0)
				blink = true;
		}

	}

	/**
	 * Puts a character into the input for the console
	 * 
	 * @param c
	 *            The character to add to the input
	 */
	public void putCharacter(Character c) {
		input += c;
	}

	/**
	 * Adds a string to the console
	 * 
	 * @param s
	 *            The string to print to the console
	 */
	public void print(String s) {
		text.add(s);
	}

	/**
	 * Goes back one space in the input string
	 */
	public void backspace() {
		if (input.length() > 0)
			input = input.substring(0, input.length() - 1);
	}
	
	public void scrollUp(int amount){
		scroll -= amount;
	}
	
	public void scrollDown(int amount){
		scroll += amount;
	}

	/**
	 * What happens when the return key is pressed. If the current input starts
	 * with a /, this issues a command
	 */
	public void submit() {
		if (input.length() > 0) {
			// trim off any whitespace
			input.trim();
			// do a command if the input starts with a /
			if (input.charAt(0) == '/')
				ConsoleCommands.issueCommand(input);
			// otherwise just add it to the text
			else
				// TODO give the player a name
				text.add("<Player> " + input);
		}
		// clear the input string (this is important!)
		input = "";
		
		if(autoClose){
			autoClose = false;
			Debug.consoleOn = false;
		}
	}
	

}
