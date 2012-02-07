package spaceguts.util.input;
/**
 * All the possible keys on the keyboard
 * @author TranquilMarmot
 * @see KeyBindings
 *
 */
public enum Keys implements Button{
	/* 
	 * If you notice any missing keys, add them!
	 * It's important that all of these have the same exact name
	 * as what is returned by Keyboard.getKeyName(eventKey);
	 */
	ESCAPE,F1,F2,F3,F4,F5,F6,F7,F8,F9,F10,F11,F12,SYSRQ,SCROLL,PAUSE,
	GRAVE,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,ZERO,MINUS,EQUALS,BACK,
	TAB,Q,W,E,R,T,Y,U,I,O,P,LBRACKET,RBRACKET,BACKSLASH,
	CAPITAL,A,S,D,F,G,H,J,K,L,SEMICOLON,APOSTROPHE,RETURN,
	LSHIFT,Z,X,C,V,B,N,M,COMMA,PERIOD,SLASH,RSHIFT,
	LCONTROL,LWIN,LMENU,SPACE,RMENU,APPS,RCONTROL,
	HOME,END,INSERT,PRIOR,NEXT,DELETE,
	UP,DOWN,LEFT,RIGHT,
	NUMLOCK,DIVIDE,MULTIPLY,SUBTRACT,ADD,DECIMAL,
	NUMPAD1,NUMPAD2,NUMPAD3,NUMPAD4,NUMPAD5,NUMPAD6,NUMPAD7,NUMPAD8,NUMPAD9,NUMPAD0;
	
	/** whether or not the key is being pressed right now */
	private boolean isPressed;
	
	/** whether or not the key is still down from the previous update */
	private boolean stillDown;
	
	/**
	 * Constructor
	 */
	private Keys(){
		isPressed = false;
		stillDown = true;
	}
	
	@Override
	/**
	 * Notify the key that it's been pressed
	 */
	public void press(){
		this.isPressed = true;
		this.stillDown = true;
	}
	
	@Override
	/**
	 * Notify the key that it's been released
	 */
	public void release(){
		this.isPressed = false;
		this.stillDown = false;
	}
	
	@Override
	/**
	 * @return Whether or not the key is being pressed
	 */
	public boolean isPressed(){
		return isPressed;
	}
	
	@Override
	/**
	 * @return True if this is the first call to pressedOnce since the key was pressed, else false
	 */
	public boolean pressedOnce(){
		// if we're still down from the previous frame, return false
		if(stillDown){
			return false;
		} else{
			stillDown = true;
			return true;
		}
	}
}
