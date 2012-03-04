package com.bitwaffle.spaceguts.input;

/**
 * Bindings for all the special input for the game. They will all be initialized to their default values,
 * but they can easily be changed using the setKey() method.
 * @author TranquilMarmot
 * @see Button
 * @see Keys
 * @see Buttons
 *
 */
public enum KeyBindings {
	CONTROL_FORWARD(new Button[]{Keys.W}),
	CONTROL_BACKWARD(new Button[]{Keys.S}),
	CONTROL_LEFT(new Button[]{Keys.A}),
	CONTROL_RIGHT(new Button[]{Keys.D}),
	CONTROL_DESCEND(new Button[]{Keys.LSHIFT}),
	CONTROL_ASCEND(new Button[]{Keys.SPACE}),
	CONTROL_ROLL_LEFT(new Button[]{Keys.Q}),
	CONTROL_ROLL_RIGHT(new Button[]{Keys.E}),
	CONTROL_STABILIZE(new Button[]{Keys.LCONTROL}),
	CONTROL_STOP(new Button[]{Keys.F}),
	
	SYS_CAMERA_MODE(new Button[]{Keys.C}),
	SYS_BUILD_MODE(new Button[]{Keys.B}),
	SYS_CONSOLE(new Button[]{Keys.GRAVE}),
	SYS_COMMAND(new Button[]{Keys.SLASH}),
	SYS_CHAT(new Button[]{Keys.T}),
	SYS_PAUSE(new Button[]{Keys.ESCAPE}),
	SYS_FULLSCREEN(new Button[]{Keys.F11}),
	SYS_DEBUG(new Button[]{Keys.F3}),
	SYS_SCREENSHOT(new Button[]{Keys.F2}),
	SYS_DEBUG_PHYSICS(new Button[]{Keys.F4}),
	
	SYS_CONSOLE_PREVIOUS_COMMAND(new Button[]{Keys.UP}),
	SYS_CONSOLE_NEXT_COMMAND(new Button[]{Keys.DOWN}),
	SYS_CONSOLE_SUBMIT(new Button[]{Keys.RETURN}),
	SYS_CONSOLE_BACKSPACE(new Button[]{Keys.BACK}),
	SYS_CONSOLE_SCROLL_UP(new Button[]{Keys.NEXT}),
	SYS_CONSOLE_SCROLL_DOWN(new Button[]{Keys.PRIOR}),
	
	BUILDER_OPEN_ADD_MENU(new Button[]{Keys.TAB});
	
	/** the buttons that activate this binding */
	private Button[] buttons;
	
	/**
	 * KeyBindings constructor
	 * @param buttons Buttons to use for the binding
	 */
	private KeyBindings(Button[] buttons){
		this.buttons = buttons;
	}
	
	/**
	 * @return Whether or not the binding is being pressed
	 */
	public boolean isPressed(){
		for(Button button : buttons){
			if(button.isPressed())
				return true;
		}
		return false;
	}
	
	/**
	 * @return True if this is the first call to pressedOnce since the key was pressed, else false
	 */
	public boolean pressedOnce(){
		for(Button button : buttons){
			if(button.pressedOnce())
				return true;
		}
		return false;
	}
	
	/**
	 * Set the binding to have a new set of buttons
	 * @param newKey Key to set binding to
	 */
	public void setButtons(Button[] newButtons){
		this.buttons = newButtons;
	}
	
	/**
	 * Adds a button that will activate the binding
	 * @param newButton Button to add to binding
	 */
	public void addButton(Button newButton){
		Button[] newKeys = new Keys[buttons.length + 1];
		for(int i = 0; i < buttons.length; i++)
			newKeys[i] = buttons[i];
		newKeys[buttons.length] = newButton;
		this.setButtons(newKeys);
	}
	
	/**
	 * Removes a button that activated the binding (does nothing if button doesn't already activate binding)
	 * @param oldButton Button to remove
	 */
	public void removeButton(Button oldButton){
		// index of oldButton in buttons
		int bIndex = -1;
		
		// find bIndex
		for(int i = 0; i < buttons.length; i++){
			if(buttons[i] == oldButton)
				bIndex = i;
		}
		
		// found oldButton
		if(bIndex != -1){
			Button[] newButtons = new Button[buttons.length - 1];
			for(int i = 0; i < buttons.length; i++){
				// skip the button being removed
				if(i == bIndex)
					continue;
				
				// add old buttons, minus the old button
				if(i < bIndex)
					newButtons[i] = buttons[i];
				else
					newButtons[i - 1] = buttons[i];
			}
			
			this.setButtons(newButtons);
		}
	}
	
	/**
	 * Checks to see if the binding is activated by the given button
	 * @param other Button to check for
	 * @return Whether or not the button activates the binding
	 */
	public boolean isButton(Button other){
		for(Button key : buttons){
			if(key == other)
				return true;
		}
		return false;
	}
	
	/**
	 * To string, or not to string -- that is the question
	 * Whether 'tis nobler in the mind to suffer
	 * The slings and arrows of outrageous fortune
	 * Or to take arms against a sea of troubles
	 * And by opposing them. To die, to sleep--
	 * No more
	 * 
	 * Shakespeare while you code!
	 */
	public String toString(){
		String ret = "";
		for(int i = 0; i < buttons.length; i++){
			ret += buttons[i].toString() + " ";
		}
		
		return ret;
	}
}
