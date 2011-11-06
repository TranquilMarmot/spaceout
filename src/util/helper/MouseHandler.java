package util.helper;

import org.lwjgl.input.Mouse;

import util.console.ConsoleManager;

public class MouseHandler {
	/** whether or not button 0 is being pressed */
	public static boolean button0;

	/** whether or not button 1 is being pressed */
	public static boolean button1;

	/** how much the mouse has moved on the X axis */
	public static float dx;
	/** how much the mouse has move on the Y axis */
	public static float dy;
	/** how much the mouse wheel has moved */
	public static float wheel;
	
	public static boolean inverted = false;

	/** sensitivity values for mouse movement */
	/**
	 * NOTE:These divide the mouse's movement by whatever they are, so setting
	 * it to 5 results in 1/5th of the mouse movement (higher numbers means less
	 * sensitivity)
	 */
	public static float verticalSensitivity = 10.0f;
	public static float horizontalSensitivity = 10.0f;

	public void update() {
		if (Mouse.isButtonDown(0))
			button0 = true;
		else
			button0 = false;

		if (Mouse.isButtonDown(1))
			button1 = true;
		else
			button1 = false;

		if (!ConsoleManager.consoleOn) {
			dx = (-(float) Mouse.getDX()) / verticalSensitivity;
			if(!inverted)
				dy = ((float) Mouse.getDY()) / horizontalSensitivity;
			else
				dy = (-(float) Mouse.getDY()) / horizontalSensitivity;
		} else{
			dx = 0.0f;
			dy = 0.0f;
		}

		wheel = (float) Mouse.getDWheel();
	}

}
