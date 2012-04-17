package com.bitwaffle.spaceguts.input;

/**
 * Possible buttons for a game pad
 * @author TranquilMarmot
 *
 */
public enum Buttons implements Button{
	// TODO controller buttons!
	;
	
	/** whether or not the button is being pressed right now */
	private boolean isPressed;
	
	/** whether or not the button is still down from the previous update */
	private boolean stillDown;
	
	/**
	 * Constructor
	 */
	private Buttons(){
		isPressed = false;
		stillDown = false;
	}

	@Override
	/**
	 * Notify the button that it's been pressed
	 */
	public void press() {
		isPressed = true;
		stillDown = true;
	}

	@Override
	/**
	 * Notify the key that it's been released
	 */
	public void release() {
		isPressed = false;
		stillDown = false;
	}

	@Override
	/**
	 * @return Whether or not the key is being pressed
	 */
	public boolean isPressed() {
		return isPressed;
	}

	@Override
	/**
	 * @return True if this is the first call to pressedOnce since the key was pressed, else false
	 */
	public boolean pressedOnce() {
		// if we're still down from the previous frame, return false
		if(stillDown){
			return false;
		} else{
			stillDown = true;
			return true;
		}
	}
	
	
}
