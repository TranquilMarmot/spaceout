package util.helper;

import org.lwjgl.input.Keyboard;

import util.console.ConsoleManager;

public class KeyboardHandler {
	// These allow for controls to easily be changed (just change the key for
	// the action)
	/* Player control keys (only checked when the console's not up- primarily keys needed for typing to the console) */
	private static int forwardKey = Keyboard.KEY_W;
	private static int backwardKey = Keyboard.KEY_S;
	private static int leftKey = Keyboard.KEY_A;
	private static int rightKey = Keyboard.KEY_D;
	private static int descendKey = Keyboard.KEY_LSHIFT;
	private static int ascendKey = Keyboard.KEY_SPACE;
	private static int rollLeftKey = Keyboard.KEY_Q;
	private static int rollRightKey = Keyboard.KEY_E;
	private static int vanityModeKey = Keyboard.KEY_V;
	
	private static int previousConsoleCommandKey = Keyboard.KEY_UP;
	private static int nextConsoleCommandKey = Keyboard.KEY_DOWN;

	/* Keys that are always checked */
	private static int consoleKey = Keyboard.KEY_GRAVE;
	private static int commandKey = Keyboard.KEY_SLASH;
	private static int chatKey = Keyboard.KEY_T;
	private static int pauseKey = Keyboard.KEY_ESCAPE;
	private static int fullscreenKey = Keyboard.KEY_F11;
	private static int debugKey = Keyboard.KEY_F3;
	private static int screenshotKey = Keyboard.KEY_F2;

	/* These represent whether or not a key is down */
	public static boolean forward;
	public static boolean backward;
	public static boolean left;
	public static boolean right;
	public static boolean descend;
	public static boolean ascend;
	public static boolean rollLeft;
	public static boolean rollRight;
	public static boolean vanityMode;
	
	public static boolean previousConsoleCommand;
	public static boolean nextConsoleCommand;
	
	public static boolean console;
	public static boolean command;
	public static boolean chat;
	public static boolean pause;
	public static boolean fullscreen;
	public static boolean debug;
	public static boolean screenshot;

	/**
	 * This MUST be called every frame! It updates all the booleans in this class
	 */
	public void update() {
		// loop through all the event keys for this frame
		while (Keyboard.next()) {
			int eventKey = Keyboard.getEventKey();

			if (Keyboard.getEventKeyState()) {
				/* BEGIN KEY DOWN EVENTS */{
					/* BEGIN ALWAYS CHECKED KEYS */{
						if (eventKey == pauseKey)
							pause = true;
						else if (eventKey == debugKey)
							debug = true;
						else if (eventKey == fullscreenKey)
							fullscreen = true;
						else if (eventKey == screenshotKey)
							screenshot = true;
						else if (eventKey == consoleKey)
							console = true;
						else if (eventKey == commandKey)
							command = true;
						else if (eventKey == chatKey)
							chat = true;
					}/* END ALWAYS CHECKED KEYS */

					if (!ConsoleManager.consoleOn) {
						/* BEGIN CONTROL KEYS */
						if (eventKey == forwardKey)
							forward = true;
						else if (eventKey == backwardKey)
							backward = true;
						else if (eventKey == leftKey)
							left = true;
						else if (eventKey == rightKey)
							right = true;
						else if (eventKey == descendKey)
							descend = true;
						else if (eventKey == ascendKey)
							ascend = true;
						else if (eventKey == rollLeftKey)
							rollLeft = true;
						else if (eventKey == rollRightKey)
							rollRight = true;
						else if(eventKey == vanityModeKey)
							vanityMode = true;
						/* END CONTROL KEYS */
					} else {
						/* BEGIN CONSOLE INPUT */
						Character c = Keyboard.getEventCharacter();
						if (!Character.isIdentifierIgnorable(c)
								&& !c.equals('`') && !c.equals('\n')
								&& !c.equals('\r')) {
							ConsoleManager.console.putCharacter(c);
						}
						/* END CONSOLE INPUT */
					}
				}/* END KEY DOWN EVENTS */
			} else {
				/* BEGIN KEY UP EVENTS */{
					/* BEGIN ALWAYS CHECKED KEYS */{
						if (eventKey == pauseKey)
							pause = false;
						else if (eventKey == debugKey)
							debug = false;
						else if (eventKey == fullscreenKey)
							fullscreen = false;
						else if (eventKey == screenshotKey)
							screenshot = false;
						else if (eventKey == consoleKey)
							console = false;
						else if (eventKey == commandKey)
							command = false;
						else if (eventKey == chatKey) 
							chat = false;
					}/* END ALWAYS CHECKED KEYS */

					/* BEGIN CONTROL KEYS */{
						if (eventKey == forwardKey)
							forward = false;
						else if (eventKey == backwardKey)
							backward = false;
						else if (eventKey == leftKey)
							left = false;
						else if (eventKey == rightKey)
							right = false;
						else if (eventKey == descendKey)
							descend = false;
						else if (eventKey == ascendKey)
							ascend = false;
						else if (eventKey == rollLeftKey)
							rollLeft = false;
						else if (eventKey == rollRightKey)
							rollRight = false;
						else if(eventKey == vanityModeKey)
							vanityMode= false;
					}/* END CONTROL KEYS */

				}/* END KEY UP EVENTS */
			}
		}
	}
}
