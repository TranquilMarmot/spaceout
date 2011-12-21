package graphics.gui.button;

import util.Runner;

/**
 * A special MenuButton that is only drawn when the game is paused
 * @author TranquilMarmot
 * @see MenuButton
 *
 */
public class PauseMenuButton extends MenuButton {
	private static final String IMAGE_PATH = "res/images/gui/Menu/Button/"; 

	public PauseMenuButton(String text, int height, int width,
			int xOffsetFromCenter, int yOffsetFromCenter) {
		super(IMAGE_PATH, text, height, width,
				xOffsetFromCenter, yOffsetFromCenter);
	}
	
	@Override
	public void update(){
		super.update();
		
		// this button is only visible when the game is paused
		if(!Runner.paused)
			isVisible = false;
		else
			isVisible = true;
	}

}
