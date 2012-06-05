package com.bitwaffle.spaceguts.graphics.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex2i;

import org.newdawn.slick.Color;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.graphics.gui.menu.MainMenu;
import com.bitwaffle.spaceguts.util.Debug;
import com.bitwaffle.spaceguts.util.DisplayHelper;
import com.bitwaffle.spaceguts.util.console.Console;
import com.bitwaffle.spaceout.entities.player.Player;

/**
 * This class is hacked together for the presentation. I will take care of it later.
 * @author nick
 *
 */

public class HUD {
	
	private static final int HEALTH_BAR_HEIGHT = 20;
	private static final int HEALTH_BAR_WIDTH = 200;
	private static final int HEALTH_BAR_X = 5;
	
	private static final int SPEED_BAR_HEIGHT = 10;
	private static final int SPEED_BAR_WIDTH = 200;
	private static final int SPEED_BAR_X = 5;
	
	//private static final int PLAYER_MAX_HEALTH = Player.MAX_HEALTH;
	
	private static int playerHealth = Player.health;
	private static float playerSpeed;
	
	/* This was going to dinamically raise it from the bottom of the screen, but the chat is already down there.
	 * It's late and I don't want to convert it to a final.
	 * FIXME
	 */
	
	private static int dynamicHealthBarY = 5;
	private static int dynamicSpeedBarY = 27;
	
	private static float healthColorR;
	private static float healthColorG;
	
	public HUD() {
	}

	public static void enable() {
		Debug.displayDebug = false;
	}
	
	public static void draw() {

		if (!Debug.displayDebug && MainMenu.done == true) {
			
			//dynamicHealthBarY = DisplayHelper.windowHeight - 5 - HEALTH_BAR_HEIGHT;
			//dynamicSpeedBarY = DisplayHelper.windowHeight - 27 - SPEED_BAR_HEIGHT;

			playerHealth = Player.health;
			
			healthColorG = ((float)playerHealth / 100);
			healthColorR = 1 - ((float)playerHealth / 100);
			
			playerSpeed = Debug.getSpeed();
			
			/* BEGIN HEALTH BAR */
			
			glColor4f(0.07f, 0.07f, 0.07f, 0.5f);
			glBegin(GL_QUADS);
			{
				glVertex2i(HEALTH_BAR_X                    , dynamicHealthBarY);
				glVertex2i(HEALTH_BAR_WIDTH + HEALTH_BAR_X , dynamicHealthBarY);
				glVertex2i(HEALTH_BAR_WIDTH + HEALTH_BAR_X , HEALTH_BAR_HEIGHT + dynamicHealthBarY);
				glVertex2i(HEALTH_BAR_X                    , HEALTH_BAR_HEIGHT + dynamicHealthBarY);
			}
			glEnd();
			
			glColor4f(healthColorR, healthColorG, 0.00f, 0.25f);
			glBegin(GL_QUADS);
			{
				glVertex2i(HEALTH_BAR_X                   , dynamicHealthBarY);
				glVertex2i(playerHealth*2 + HEALTH_BAR_X  , dynamicHealthBarY);
				glVertex2i(playerHealth*2 + HEALTH_BAR_X  , HEALTH_BAR_HEIGHT + dynamicHealthBarY);
				glVertex2i(HEALTH_BAR_X                   , HEALTH_BAR_HEIGHT + dynamicHealthBarY);
			}
			glEnd();
			
			/* END HEALTH BAR */
			
			/* BEGIN SPEED BAR */
			
			glColor4f(0.07f, 0.07f, 0.07f, 0.5f);
			glBegin(GL_QUADS);
			{
				glVertex2i(SPEED_BAR_X                   , dynamicSpeedBarY);
				glVertex2i(SPEED_BAR_WIDTH + SPEED_BAR_X , dynamicSpeedBarY);
				glVertex2i(SPEED_BAR_WIDTH + SPEED_BAR_X , SPEED_BAR_HEIGHT + dynamicSpeedBarY);
				glVertex2i(SPEED_BAR_X                   , SPEED_BAR_HEIGHT + dynamicSpeedBarY);
			}
			glEnd();
			
			glColor4f(0.00f, 0.00f, 1.00f, 0.5f);
			glBegin(GL_QUADS);
			{
				glVertex2f(SPEED_BAR_X                  , dynamicSpeedBarY);
				glVertex2f(playerSpeed + SPEED_BAR_X  , dynamicSpeedBarY);
				glVertex2f(playerSpeed + SPEED_BAR_X  , SPEED_BAR_HEIGHT + dynamicSpeedBarY);
				glVertex2f(SPEED_BAR_X                  , SPEED_BAR_HEIGHT + dynamicSpeedBarY);
			}
			glEnd();
			
			/* END HEALTH BAR */
			
			Console.console.draw();
			
			if(Entities.player != null){
				String diamonds = "Diamonds: " + Entities.player.numDiamonds();
				Debug.font.drawString(DisplayHelper.windowWidth - Debug.font.getWidth(diamonds) - 2.5f, 10.0f, diamonds, Color.cyan);
			}
			
			Debug.drawControls();
			
		}
		
	}
}
