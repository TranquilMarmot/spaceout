package spaceguts.util.console;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import spaceguts.input.MouseManager;
import spaceguts.util.Debug;
import spaceguts.util.DisplayHelper;
import spaceout.resources.Textures;

/**
 * Console for printing text and interacting with the game. Note that there
 * should only be one console at any time.
 * 
 * @author TranquilMarmot
 * @author arthurdent
 * 
 */
public class Console {
	/**
	 * TODO: Decide what needs to be static and what doesn't 
	 * - The input bar will remain static, while the command scrollback is per object.
	 * 		(this should already work fine) 
	 * TODO: Implement tabs 
	 * TODO: Fix scrolling so it only happens in the tab you have open 
	 * TODO: Consider the implications of moving issueCommand to another class
	 * TODO: Implement chat log files
	 * TODO: Fix backspace erratic behvaior
	 * TODO: A bunch of crap that belongs in util.console is still in util.Debug
	 */

	/** the console */
	public static Console console = new Console();

	/** whether or not the console is up */
	public static boolean consoleOn = false;
	
	/**
	 * whether or not the console is up because the command key was pressed
	 * (closes the console on submission)
	 */
	public static boolean commandOn = false;

	/**
	 * This is currently pointless, but it may come in handy in the future.
	 * Disabling this will completely disable the console window visibilty.
	 * (warning: does not disable keyboard bindings)
	 */
	private static boolean consoleEnabled = true;

	/**
	 * The following is static because only 
	 * the current tab will be visible anyway...
	 */

	/** the maximum alpha for console text */
	public static float consoleTextMaxAlpha = 1.0f;
	/** the minimum alpha for console text */
	public static float consoleTextMinAlpha = 0.0f;

	/** the alpha difference for each update */
	public static float consoleTextFadeValue = 0.011f;
	/** the time in seconds before the text begins to fade */
	public static int consoleTextFadeDelay = 5;

	/**
	 * the current alpha for console text (should always be consoleTextMaxAlpha)
	 */
	public static float consoleTextAlpha = consoleTextMaxAlpha;
	/** the current fade time for console (should always be 0) */
	public static float consoleTextFadeDelayCurrent = 0;

	/**
	 * if blink is true, there's an underscore at the end of the input string,
	 * else there's not
	 */
	private static boolean blink = true;
	/** counter for blink */
	private static int blinkCount = 0;
	/** how often to blink (this is changed to match the current FPS) */
	private static int blinkInterval = 30;

	/** font for printing stuff to the screen */
	public static UnicodeFont font = null;

	/** whether or not to close the console when line is submitted */
	public static boolean autoClose = false;

	/** START non-static code */

	/** location to draw the console at */
	private int x;
	private int y;

	/** Height of the console in characters */
	private int consoleHeight;

	/** Width of the console in characters */
	private int consoleWidth;

	/**
	 * if scroll is 0, we're at the most recent line, 1 is one line up, 2 is two
	 * lines up, etc
	 */
	private int scroll;

	/** the text being typed into the console */
	public static String input;

	/** all the text that the console contains and will print out */
	private ArrayList<String> text;

	/** all the commands that are typed in */
	private ArrayList<String> commandHistoryList;
	private int chIndex;

	/**
	 * Creates a new console with the specified parameters
	 * 
	 * @param x
	 *            distance from the left side of the screen
	 * @param y
	 *            distance from the bottom of the screen
	 * @param w
	 *            window width expanding to the right
	 * @param h
	 *            window height expanding up
	 */
	public Console(int x, int y, int w, int h) {

		this.x = x;
		this.y = y;
		consoleWidth = w;
		consoleHeight = h;

		input = "";
		autoClose = false;
		scroll = 0;

		text = new ArrayList<String>();
		commandHistoryList = new ArrayList<String>();
		commandHistoryList.add("");
		chIndex = 0;
		updateCommandHistory();
	}

	/**
	 * new Console(0,10,65,14)
	 */
	public Console() {
		this(0, 10, 65, 14);
	}

	/**
	 * Puts a character into the input for the console
	 * 
	 * @param c
	 *            The character to add to the input
	 */
	public void putCharacter(Character c) {
		input += c;
		updateCommandHistory();
	}

	/**
	 * Goes back one space in the input string
	 */
	public void backspace() {
		if (input.length() > 0)
			input = input.substring(0, input.length() - 1);
		updateCommandHistory();
	}

	/**
	 * Changes the value of the current spot in the command history
	 */
	private void updateCommandHistory() {
		commandHistoryList.set(chIndex, input);
	}

	/**
	 * Adds a string to the console, wraps if necessary. Also, the console will
	 * brighten if dim.
	 * 
	 * @param s
	 *            The string to print to the console
	 */
	public void print(String s) {
		// Make the console text bright
		this.wake();

		// Make the string an array of words
		String words[] = s.split(" ");

		// The current number of characters, and the iterator
		int currentWidth = 0;

		// If the next word is too big, time to split the line.
		if (words[0].length() > consoleWidth) {
			currentWidth = consoleWidth;
		} else {
			// Otherwise, for each word:
			for (int i = 0; i < words.length
					&& (currentWidth + words[i].length()) < consoleWidth; i++)
				currentWidth += words[i].length() + 1;
			// Add the width of that word and a space
			// unless the combined number of characters excedes the width of the
			// console.
		}

		// Add the words to the screen.
		text.add(s.substring(0, currentWidth - 1));

		// Recurse and print the remaining stuff.
		if (s.length() > consoleWidth)
			print(s.substring(currentWidth));
	}

	/**
	 * Scroll up
	 * 
	 * @param amount
	 *            Number of lines to scroll up
	 */
	public void scrollUp(int amount) {
		console.wake();
		if (scroll - amount >= 0)
			scroll -= amount;
	}

	/**
	 * Scroll down
	 * 
	 * @param amount
	 *            Number of lines to scroll down
	 */
	public void scrollDown(int amount) {
		console.wake();
		if (scroll + amount < text.size())
			scroll += amount;
	}

	/**
	 * Scroll through console submission history -1 is older, 1 is more recent
	 */
	public void commandHistory(int roll) {
		if (Console.consoleOn) {
			if (roll == -1 && chIndex + 1 < commandHistoryList.size())
				chIndex++;
			else if (roll == 1 && chIndex - 1 >= 0)
				chIndex--;
			else if (roll == 0)
				chIndex = 0;
				
			if (!commandHistoryList.get(chIndex).equals(""))
				input = commandHistoryList.get(chIndex);
			else
				input = "";
		}
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
				issueCommand(input);
			// otherwise just add it to the text
			else
				// TODO give the player a name
				print("<Player> " + input);

			// adds the input to the command history
			if (!commandHistoryList.get(0).equals(""))
				commandHistoryList.add(0, "");
			else
				commandHistoryList.add(1, input);

			// rolls to the new empty spot in the commandHistory List
			commandHistory((byte)0);

			scroll = 0;
		}
		// clear the input string (this is important!)
		input = "";

		if (autoClose) {
			autoClose = false;
			Console.consoleOn = false;
		}

	}

	/**
	 * Clears the text
	 */
	public void clear() {
		text.clear();
	}

	/**
	 * Makes the text bright until the fade delay is reached again.
	 */
	public void wake() {
		// Set the alpha to the max alpha value
		consoleTextAlpha = consoleTextMaxAlpha;
		// Reset the current fade delay timer
		consoleTextFadeDelayCurrent = 0;
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
	 * Issues the given command. See {@link ConsoleCommands}.
	 * 
	 * @param comm
	 *            Command to issue
	 */
	public void issueCommand(String comm) {
		// make sure the command isn't empty
		if (comm.length() > 1) {
			// split the command at the spaces
			StringTokenizer toker = new StringTokenizer(comm, " ");

			// grab the actual command and lop off the / at the beginning
			String command = toker.nextToken();
			command = command.substring(1, command.length());

			boolean commandHelp = false;

			try {
				commandHelp = comm.substring(command.length() + 2,
						command.length() + 6).equals("help");
			} catch (StringIndexOutOfBoundsException e) {
			}

			// if the command string is followed immediately by the string
			// "help", call the help function for that command. Else, issue the
			// command.
			if (comm.length() > 7 && commandHelp) {
				ConsoleCommands.help.issue(new StringTokenizer(command));
			} else {
				try {
					// this one line issues a command! Neat!
					ConsoleCommands.valueOf(command).issue(toker);
				} catch (NumberFormatException e) {
					console.print("Incorrect number format "
							+ e.getLocalizedMessage().toLowerCase());
				} catch (IllegalArgumentException e) {
					console.print("Command not found! (" + command + ")");
				} catch (NoSuchElementException e) {
					console.print("Not enough vairbales for command '"
							+ command + "'!");
				}
			}
		}
	}

	/**
	 * Update the console
	 */
	public void update() {

		// check for command
		if (Console.commandOn) {
			input = "/";
			Console.commandOn = false;
		}

		// Logic for scrolling, cursor blinking, and console fading.
		if (Console.consoleOn) {
			// Brighten the console
			this.wake();

			// scroll with the mouse wheel
			int scamt = scroll + (int)MouseManager.wheel / 100;
			if (scamt >= 0 && scamt < text.size())
				scroll = scamt;

			// do blinking effect
			updateBlink();

		} else if (consoleTextAlpha > consoleTextMinAlpha
				&& consoleTextFadeDelayCurrent >= consoleTextFadeDelay * 60) {
			// Subtract the proper amount from the current console text
			// alpha value
			consoleTextAlpha -= consoleTextFadeValue;
		} else {
			// if statement to avoid overflow exception
			if (consoleTextFadeDelayCurrent < consoleTextFadeDelay * 60) {
				consoleTextFadeDelayCurrent++;
			}
		}
	}

	/**
	 * Draws the console
	 */
	public void draw() {
		// how tall each line is
		int advanceY = Debug.font.getAscent();
		// where to draw the console (x stays at 10)
		y = DisplayHelper.windowHeight - advanceY - 10;

		// If the console is enabled.
		if (consoleEnabled) {
			if (Console.consoleOn) {
				// Draw the box
				Textures.WHITE.texture().bind();
				/* BEGIN CONSOLE BACKGROUND */
				GL11.glColor4f(0.15f, 0.15f, 0.15f, 0.35f);
				GL11.glBegin(GL11.GL_QUADS);
				{
					GL11.glVertex2f(0.0f, DisplayHelper.windowHeight
							- Debug.font.getAscent() - 7);
					GL11.glVertex2f(
							0.0f,
							DisplayHelper.windowHeight
									- ((Console.console.consoleHeight + 2) * Debug.font
											.getAscent()));
					GL11.glVertex2f(
							(consoleWidth * 9) + 10,
							DisplayHelper.windowHeight
									- ((Console.console.consoleHeight + 2) * Debug.font
											.getAscent()));
					GL11.glVertex2f((consoleWidth * 9) + 10,
							DisplayHelper.windowHeight - Debug.font.getAscent()
									- 7);
				}
				GL11.glEnd();
				/* END CONSOLE BACKGROUND */

				/* BEGIN INPUT BOX */
				GL11.glColor4f(0.20f, 0.20f, 0.20f, 0.35f);
				GL11.glBegin(GL11.GL_QUADS);
				{
					GL11.glVertex2f(0.0f, DisplayHelper.windowHeight);
					GL11.glVertex2f(0.0f, DisplayHelper.windowHeight
							- Debug.font.getAscent() - 7);
					GL11.glVertex2f(DisplayHelper.windowWidth,
							DisplayHelper.windowHeight - Debug.font.getAscent()
									- 7);
					GL11.glVertex2f(DisplayHelper.windowWidth,
							DisplayHelper.windowHeight);
				}
				GL11.glEnd();
				/* END INPUT BOX */

				// Draw the blinking cursor
				String toPrint = "> " + input;
				if (blink)
					toPrint += "_";
				Debug.font.drawString(x, y, toPrint, new Color(38, 255, 0));

			}
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			// figure out how many lines to print out
			int stringsToPrint = text.size() - (consoleHeight + 1);
			// avoid any possibility of out of bounds (the for loop is kind of
			// weird. if stringsToPrint == -1, then it doesn't print out any
			// lines)
			if (stringsToPrint < 0)
				stringsToPrint = -1;

			// print out however many strings, going backwards
			// (we want the most recent strings to be printed first)
			for (int i = text.size() - 1 - scroll; i > stringsToPrint - scroll
					&& i >= 0; i--) {

				// which line we're at in the console itself
				int line = text.size() - (i + scroll);

				// draw the string, going up on the y axis by how tall each line
				// is
				Debug.font.drawString(x, y - (advanceY * line), text.get(i),
						new Color(0.15f, 1.0f, 0.0f, consoleTextAlpha));

			}
		}

	}
}
