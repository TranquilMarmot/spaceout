package spaceguts.util.manager;


public enum KeyBindings {
	CONTROL_FORWARD(Keys.W, false),
	CONTROL_BACKWARD(Keys.S, false),
	CONTROL_LEFT(Keys.A, false),
	CONTROL_RIGHT(Keys.D, false),
	CONTROL_DESCEND(Keys.LSHIFT, false),
	CONTROL_ASCEND(Keys.SPACE, false),
	CONTROL_ROLL_LEFT(Keys.Q, false),
	CONTROL_ROLL_RIGHT(Keys.E, false),
	CONTROL_STABILIZE(Keys.LCONTROL, false),
	CONTROL_STOP(Keys.F, false),
	
	SYSTEM_CAMERA_MODE(Keys.C, false),
	SYSTEM_CONSOLE(Keys.GRAVE, true),
	SYSTEM_COMMAND(Keys.SLASH, true),
	SYSTEM_CHAT(Keys.T, true),
	SYSTEM_PAUSE(Keys.ESCAPE, true),
	SYSTEM_FULLSCREEN(Keys.F11, true),
	SYSTEM_DEBUG(Keys.F3, true),
	SYSTEM_SCREENSHOT(Keys.F2, true),
	SYSTEM_DEBUG_PHYSICS(Keys.F4, true),
	
	SYSTEM_CONSOLE_PREVIOUS(Keys.UP, false),
	SYSTEM_CONSOLE_NEXT(Keys.DOWN, false),
	SYSTEM_CONSOLE_SUBMIT(Keys.RETURN, false);
	
	private Keys key;
	private boolean alwaysChecked;
	private KeyBindings(Keys key, boolean alwaysChecked){
		this.key = key;
		this.alwaysChecked = alwaysChecked;
	}
	
	public boolean isPressed(){
		return key.isPressed();
	}
	
	public boolean isAlwaysChecked(){
		return alwaysChecked;
	}
	
	public void setKey(Keys newKey){
		this.key = newKey;
	}
}
