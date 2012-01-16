package spaceguts.util.input;

/**
 * Bindings for all the special input keys for the game. They will all be initialized to their default values,
 * but they can easily be changed using the setKey() method.
 * @author TranquilMarmot
 * @see Keys
 *
 */
public enum KeyBindings {
	CONTROL_FORWARD(Keys.W),
	CONTROL_BACKWARD(Keys.S),
	CONTROL_LEFT(Keys.A),
	CONTROL_RIGHT(Keys.D),
	CONTROL_DESCEND(Keys.LSHIFT),
	CONTROL_ASCEND(Keys.SPACE),
	CONTROL_ROLL_LEFT(Keys.Q),
	CONTROL_ROLL_RIGHT(Keys.E),
	CONTROL_STABILIZE(Keys.LCONTROL),
	CONTROL_STOP(Keys.F),
	
	SYS_CAMERA_MODE(Keys.C),
	SYS_CONSOLE(Keys.GRAVE),
	SYS_COMMAND(Keys.SLASH),
	SYS_CHAT(Keys.T),
	SYS_PAUSE(Keys.ESCAPE),
	SYS_FULLSCREEN(Keys.F11),
	SYS_DEBUG(Keys.F3),
	SYS_SCREENSHOT(Keys.F2),
	SYS_DEBUG_PHYSICS(Keys.F4),
	
	SYS_CONSOLE_PREVIOUS_COMMAND(Keys.UP),
	SYS_CONSOLE_NEXT_COMMAND(Keys.DOWN),
	SYS_CONSOLE_SUBMIT(Keys.RETURN),
	SYS_CONSOLE_BACKSPACE(Keys.BACK),
	SYS_CONSOLE_SCROLL_UP(Keys.NEXT),
	SYS_CONSOLE_SCROLL_DOWN(Keys.PRIOR);
	
	/** the key to check for for this binding */
	private Keys key;
	
	/**
	 * KeyBindings constructor
	 * @param key Key to use for the binding
	 */
	private KeyBindings(Keys key){
		this.key = key;
	}
	
	/**
	 * @return Whether or not the key is being pressed
	 */
	public boolean isPressed(){
		return key.isPressed();
	}
	
	/**
	 * Set the key to a new key
	 * @param newKey Key to set binding to
	 */
	public void setKey(Keys newKey){
		this.key = newKey;
	}
	
	/**
	 * Checks to see if the given key matches the binding's key
	 * @param other Key to check against
	 * @return Whether or not the keys are the same
	 */
	public boolean isKey(Keys other){
		return key == other;
	}
}
