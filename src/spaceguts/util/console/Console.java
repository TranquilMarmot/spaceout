package spaceguts.util.console;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import spaceguts.util.debug.Debug;
import spaceguts.util.helper.DisplayHelper;
import spaceguts.util.manager.MouseManager;
import spaceguts.util.manager.TextureManager;

/**
 * Console for printing text and interacting with the game. Note that there should only be one console at any time.
 * @author TranquilMarmot
 * @author arthurdent
 *
 */
public class Console {
	
	/** the console */
	public static Console console = new Console();
	
	// whether or not the console is up
	public static boolean consoleOn = false;
	public static boolean commandOn = false;
	
	// the maximum alpha for console text
	public static float consoleTextMaxAlpha = 1.0f;
	// the minimum alpha for console text
	public static float consoleTextMinAlpha = 0.3f;
	// the alpha difference for each update
	public static float consoleTextFadeValue = 0.005f;
	// the time in seconds before the text begins to fade
	public static int consoleTextFadeDelay = 5;
	
	// the current alpha for console text
	public static float consoleTextAlpha = consoleTextMaxAlpha;
	// the current fade time for console
	public static float consoleTextFadeDelayCurrent = 0;
	
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
		if (Console.commandOn) {
			input = "/";
			Console.commandOn = false; 
		}
		
		
		boolean chatOn = true;
		
		// draw the input text
		//if (Debug.consoleOn) {
		if (chatOn) {
			
			// If you hit any of the keys to bring up the console window:
			// draw a semi-transparent box behind the console text, and blink the '_'.			
			if (Console.consoleOn) {
	
				// Draw the box
				TextureManager.getTexture(TextureManager.WHITE).bind();
				GL11.glColor4f(0.15f, 0.15f, 0.15f, 0.35f);
				GL11.glBegin(GL11.GL_QUADS);{
					GL11.glVertex2f(0.0f, DisplayHelper.windowHeight);
					GL11.glVertex2f(0.0f, DisplayHelper.windowHeight - 225.0f);
					GL11.glVertex2f(600, DisplayHelper.windowHeight - 225.0f);
					GL11.glVertex2f(600, DisplayHelper.windowHeight);
				}GL11.glEnd();
			
				// Draw the blinking cursor
				String toPrint = "> " + input;
				if (blink) 
					toPrint += "_";
				Debug.font.drawString(x, y, toPrint, new Color(38, 255, 0));
				
			}
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			
			// print out however many strings, going backwards 
			// (we want the latest strings to be printed first)
			for (int i = text.size() - 1 - scroll; i > stringsToPrint - scroll && i >= 0; i--) {				
				
				// which line we're at in the console itself
				int line = text.size() - (i + scroll);
				
				// draw the string, going up on the y axis by how tall each line is
				Debug.font.drawString(x, y - (advanceY * line), text.get(i), new Color(0.15f, 1.0f, 0.0f, consoleTextAlpha));
				
			}
			
			// Fade the text if the console is closed!
			if (Console.consoleOn) {
				// Set the alpha to the max alpha value
				consoleTextAlpha = consoleTextMaxAlpha;
				consoleTextFadeDelayCurrent = 0;
			} else if (consoleTextAlpha > consoleTextMinAlpha && consoleTextFadeDelayCurrent >= consoleTextFadeDelay*60){
				// Subtract the proper amount from the current console text alpha value
				consoleTextAlpha -= consoleTextFadeValue;
			} else {
				// if statement to avoid overflow exception
				if (consoleTextFadeDelayCurrent < consoleTextFadeDelay*60) {
					consoleTextFadeDelayCurrent++;
				}
			}
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
			
			scroll = 0;
		}
		// clear the input string (this is important!)
		input = "";
		
		if(autoClose){
			autoClose = false;
			Console.consoleOn = false;
		}
	}
	

}
