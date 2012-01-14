package spaceguts.util.manager;

import org.lwjgl.input.Mouse;

import spaceguts.util.DisplayHelper;

/**
 * Manages everything to do with the mouse
 * 
 * @author TranquilMarmot
 * 
 */
public class MouseManager {
	/** whether or not button a is being pressed */
	public static boolean button0, button1, button2;

	/** how much the mouse has moved on the X axis */
	public static float dx;
	/** how much the mouse has move on the Y axis */
	public static float dy;
	/** how much the mouse wheel has moved */
	public static float wheel;
	/** the mouse's current location */
	public static int x, y;

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
		while (Mouse.next()) {
			String eventButton = Mouse.getButtonName(Mouse.getEventButton());
			if (eventButton != null) {
				if (Mouse.getEventButtonState()) {
					if (eventButton.equals("BUTTON0")) {
						button0 = true;
					}
					if (eventButton.equals("BUTTON1")) {
						button1 = true;
					}
					if (eventButton.equals("BUTTON2")) {
						button2 = true;
					}
				} else {
					if (eventButton.equals("BUTTON0")) {
						button0 = false;
					}
					if (eventButton.equals("BUTTON1")) {
						button1 = false;
					}
					if (eventButton.equals("BUTTON2")) {
						button2 = false;
					}
				}
			}
		}

		/*
		 * if (Mouse.isButtonDown(0)) button0 = true; else button0 = false;
		 * 
		 * if (Mouse.isButtonDown(1)) button1 = true; else button1 = false;
		 */

		// only update dx and dy if the mouse isn't grabbed
		if (Mouse.isGrabbed()) {
			dx = (-(float) Mouse.getDX()) / verticalSensitivity;
			dy = ((float) Mouse.getDY()) / horizontalSensitivity;
			// invert if necessary
			if (inverted)
				dy = -dy;
		} else {
			dx = 0.0f;
			dy = 0.0f;
		}

		// grab wheel change amount
		wheel = (float) Mouse.getDWheel();

		x = Mouse.getX();
		// we want the top-left corner to be 0,0
		y = DisplayHelper.windowHeight - Mouse.getY();
	}

}
