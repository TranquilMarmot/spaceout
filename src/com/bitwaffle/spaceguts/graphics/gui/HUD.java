package com.bitwaffle.spaceguts.graphics.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import com.bitwaffle.spaceguts.graphics.gui.menu.MainMenu;
import com.bitwaffle.spaceguts.util.Debug;
import com.bitwaffle.spaceguts.util.DisplayHelper;
import com.bitwaffle.spaceout.entities.player.Player;

/**
 * This class is hacked together for the presentation. I will take care of it later.
 * @author nick
 *
 */

public class HUD {
	
	private static final int HEALTH_BAR_X = 5;
	//private static final int HEALTH_BAR_Y = DisplayHelper.windowHeight + 5;
	private static final int HEALTH_BAR_HEIGHT = 30;
	private static final int HEALTH_BAR_WIDTH = 200;
	
	//private static final int PLAYER_MAX_HEALTH = Player.MAX_HEALTH;
	
	private static int playerHealth = Player.health;

	public HUD() {
	}

	public static void enable() {
		Debug.displayDebug = false;
	}
	
	public static void draw() {

		if (!Debug.displayDebug && MainMenu.done == true) {
			
			int dynamicHealthBarY = DisplayHelper.windowHeight - 5 - HEALTH_BAR_HEIGHT;
			
			playerHealth = Player.health;
			
			glColor4f(0.07f, 0.07f, 0.07f, 0.5f);
			glBegin(GL_QUADS);
			{
				glVertex2i(HEALTH_BAR_X                    , dynamicHealthBarY);
				glVertex2i(HEALTH_BAR_WIDTH + HEALTH_BAR_X , dynamicHealthBarY);
				glVertex2i(HEALTH_BAR_WIDTH + HEALTH_BAR_X , HEALTH_BAR_HEIGHT + dynamicHealthBarY);
				glVertex2i(HEALTH_BAR_X                    , HEALTH_BAR_HEIGHT + dynamicHealthBarY);
			}
			glEnd();
			
			glColor4f(1.00f, 0.00f, 0.00f, 0.25f);
			glBegin(GL_QUADS);
			{
				glVertex2i(HEALTH_BAR_X                   , dynamicHealthBarY);
				glVertex2i(playerHealth*2 + HEALTH_BAR_X  , dynamicHealthBarY);
				glVertex2i(playerHealth*2 + HEALTH_BAR_X  , HEALTH_BAR_HEIGHT + dynamicHealthBarY);
				glVertex2i(HEALTH_BAR_X                   , HEALTH_BAR_HEIGHT + dynamicHealthBarY);
			}
			glEnd();
			
			
			
		}
		
	}
}
