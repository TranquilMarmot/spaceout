package com.bitwaffle.spaceguts.graphics.gui.menu;

import java.awt.event.ActionListener;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.graphics.gui.GUIObject;
import com.bitwaffle.spaceguts.graphics.gui.button.MenuButton;
import com.bitwaffle.spaceguts.util.Debug;
import com.bitwaffle.spaceguts.util.DisplayHelper;
import com.bitwaffle.spaceguts.util.menu.MenuPainter;
import com.bitwaffle.spaceguts.util.menu.MenuPainter.Style;
import com.bitwaffle.spaceguts.util.xml.MenuParser;
import com.bitwaffle.spaceout.Runner;
import com.bitwaffle.spaceout.resources.Textures;


/**
 * Main menu to show when the game first runs
 * 
 * @author TranquilMarmot
 * @author arthurdent
 * 
 */
public class MenuTester extends GUIObject {
	/** XML path */

	private static final int spaceoutYOffset = -200;
	private static final int spaceoutScale = 2;

	/** whether or not we're done with the main menu */
	public static boolean done;
	
	private Textures background = Textures.MENU_BACKGROUND1;
	private Textures spaceout = Textures.MENU_SPACEOUT_TEXT;
	
	private Map<MenuButton, ActionListener> menuMap;

	/**
	 * Main menu constructor. Creates a startButton that loads from an XML file.
	 */
	public MenuTester() {
		super(0, 0);
		done = false;
		
		// Build the main menu from menus.xml
		MenuParser mp = new MenuParser("test");
		Map<String,String> rawMenu = mp.getMenu();
		
		// Paint the menu to a hashmap
		MenuPainter menuPainter = new MenuPainter(rawMenu, Style.HORIZONTAL_MENU, -40);
		menuMap = menuPainter.getMenu();
		
		// let the GUI know that a menu is up
		GUI.menuUp = true;
		Runner.paused = false;
	}

	@Override
	public void update() {
		// update the buttons
		for (Map.Entry<MenuButton, ActionListener> entry : menuMap.entrySet()) {
			entry.getKey().update();
		}
		//loadMenuButton.update();
		//quitButton.update();

		// remove the main menu if we're done with it
		if (done) {
			GUI.removeGUIObject(this);
		}
	}

	@Override
	public void draw() {
		// draw the background
		background.texture().bind();
		GL11.glBegin(GL11.GL_QUADS);
		{

			/*
			 * currentImage.getWidth() and currentImage.getHeight() return the
			 * actual height of the texture. My best guess is that the image
			 * gets put into the smallest possible texture that has dimensions
			 * that are powers of 2 by Slick, because OpenGL can handle those
			 * much better.
			 */
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(0, 0);

			GL11.glTexCoord2f(background.texture().getWidth(), 0);
			GL11.glVertex2i(DisplayHelper.windowWidth, 0);

			GL11.glTexCoord2f(background.texture().getWidth(), background.texture().getHeight());
			GL11.glVertex2i(DisplayHelper.windowWidth,
					DisplayHelper.windowHeight);

			GL11.glTexCoord2f(0, background.texture().getHeight());
			GL11.glVertex2i(0, DisplayHelper.windowHeight);
		}
		GL11.glEnd();

		// draw 'spaceout'
		spaceout.texture().bind();

		int x1 = (DisplayHelper.windowWidth / 2)
				- (spaceout.texture().getImageWidth() * spaceoutScale);
		int y1 = (DisplayHelper.windowHeight / 2)
				- (spaceout.texture().getImageHeight() * spaceoutScale) + spaceoutYOffset;

		int x2 = (DisplayHelper.windowWidth / 2)
				+ (spaceout.texture().getImageWidth() * spaceoutScale);
		int y2 = (DisplayHelper.windowHeight / 2)
				- (spaceout.texture().getImageHeight() * spaceoutScale) + spaceoutYOffset;

		int x3 = (DisplayHelper.windowWidth / 2)
				+ (spaceout.texture().getImageWidth() * spaceoutScale);
		int y3 = (DisplayHelper.windowHeight / 2)
				+ (spaceout.texture().getImageHeight() * spaceoutScale) + spaceoutYOffset;

		int x4 = (DisplayHelper.windowWidth / 2)
				- (spaceout.texture().getImageWidth() * spaceoutScale);
		int y4 = (DisplayHelper.windowHeight / 2)
				+ (spaceout.texture().getImageHeight() * spaceoutScale) + spaceoutYOffset;

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x1, y1);

			GL11.glTexCoord2f(spaceout.texture().getWidth(), 0);
			GL11.glVertex2i(x2, y2);

			GL11.glTexCoord2f(spaceout.texture().getWidth(), spaceout.texture().getHeight());
			GL11.glVertex2i(x3, y3);

			GL11.glTexCoord2f(0, spaceout.texture().getHeight());
			GL11.glVertex2i(x4, y4);
		}
		GL11.glEnd();

		// draw the buttons
		
		for (Map.Entry<MenuButton, ActionListener> entry : menuMap.entrySet()) {
			entry.getKey().draw();
		}
		
//		loadMenuButton.draw();
//		quitButton.draw();

		Debug.drawVersion();
	}
}
