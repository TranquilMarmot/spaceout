package com.bitwaffle.spaceguts.graphics.gui.button;

import com.bitwaffle.spaceout.Runner;

/**
 * A special MenuButton that is only drawn when the game is paused
 * @author TranquilMarmot
 * @see MenuButton
 *
 */
public class PauseMenuButton extends MenuButton {
	public PauseMenuButton(String text, int height, int width,
			int xOffsetFromCenter, int yOffsetFromCenter) {
		super(text, height, width,
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
