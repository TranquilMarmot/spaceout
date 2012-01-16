package spaceguts.util.input;

import org.lwjgl.input.Keyboard;

import spaceguts.util.console.Console;

/**
 * Handles any key presses. Only one should exist, and it needs to be updated
 * every frame.
 * 
 * @author TranquilMarmot
 * @see Keys
 * @see KeyBindings
 * 
 */
public class KeyboardManager {
	/** These keys are checked even when the console is on (if they weren't on this list, the game will attempt to print them to the console which wouldn't do anything) */
	private final KeyBindings[] checkedWhenConsoleIsOn = {
			KeyBindings.SYS_CONSOLE,
			KeyBindings.SYS_SCREENSHOT,
			KeyBindings.SYS_DEBUG,
			KeyBindings.SYS_DEBUG_PHYSICS,
			KeyBindings.SYS_CONSOLE_BACKSPACE,
			KeyBindings.SYS_CONSOLE_NEXT_COMMAND,
			KeyBindings.SYS_CONSOLE_PREVIOUS_COMMAND,
			KeyBindings.SYS_CONSOLE_SCROLL_DOWN,
			KeyBindings.SYS_CONSOLE_SCROLL_UP,
			KeyBindings.SYS_CONSOLE_SUBMIT };
	
	/**
	 * Loops through all the current Keyboard events and toggles booleans in 
	 */
	public void update() {
		// loop through all keyboard events
		while (Keyboard.next()) {		
			int eventKey = Keyboard.getEventKey();
			
			//System.out.println(Keyboard.getKeyName(eventKey));
			
			// there are some special case keys, so there's a method to check for them
			Keys key = getKey(eventKey);
			// whether or not the key is being pressed or released this frame
			boolean keyState = Keyboard.getEventKeyState();
			
			// if the console is on, we'll check if the key being pressed is a special key, else we'll write to the console
			if(Console.consoleOn){
				//if this turns to true, it means the key isn't written to the console and is pressed instead
				boolean specialKey = false;
				
				// go through all the special keys
				for(KeyBindings binding : checkedWhenConsoleIsOn){
					if(binding.isKey(key)){
						//found a special key
						specialKey = true;
						break;
					}
				}
				
				// print to the console if it's not a special key
				if(!specialKey){
					Character c = Keyboard.getEventCharacter();
					if (!Character.isIdentifierIgnorable(c) && !c.equals('`')
							&& !c.equals('\n') && !c.equals('\r')) {
						Console.console.putCharacter(c);
					}
				// else toggle the key's state
				} else{
					if(keyState)
						key.press();
					else
						key.release();
				}
			} else{
				if(keyState)
					key.press();
				else
					key.release();
			}
		}
	}
	
	/**
	 * Since the Keys enum couldn't have <code>1,2,3,4,5,etc.</code> as variables,
	 * they had to be named <code>ONE,TWO,THREE,FOUR,FIVE,etc.</code>.
	 * This method checks if the key is a digit and returns the proper {@link Keys} key.
	 * Else it gets the Keys object via a String using enum's valueOf method
	 * @param eventKey Key being pressed
	 * @return Keys key representing that key
	 */
	private Keys getKey(int eventKey){
		Keys key = null;
		
		switch(eventKey){
		case(Keyboard.KEY_0):
			key = Keys.ZERO;
			break;
		case(Keyboard.KEY_1):
			key = Keys.ONE;
			break;
		case(Keyboard.KEY_2):
			key = Keys.TWO;
			break;
		case(Keyboard.KEY_3):
			key = Keys.THREE;
			break;
		case(Keyboard.KEY_4):
			key = Keys.FOUR;
			break;
		case(Keyboard.KEY_5):
			key = Keys.FIVE;
			break;
		case(Keyboard.KEY_6):
			key = Keys.SIX;
			break;
		case(Keyboard.KEY_7):
			key = Keys.SEVEN;
			break;
		case(Keyboard.KEY_8):
			key = Keys.EIGHT;
			break;
		case(Keyboard.KEY_9):
			key = Keys.NINE;
			break;
		default:
			key = Keys.valueOf(Keyboard.getKeyName(eventKey));
	}
		
		return key;
	}
}
