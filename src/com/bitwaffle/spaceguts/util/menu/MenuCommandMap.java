package com.bitwaffle.spaceguts.util.menu;

import java.util.HashMap;
import java.util.Map;

import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.graphics.gui.menu.LoadMenu;
import com.bitwaffle.spaceguts.graphics.gui.menu.MainMenu;
import com.bitwaffle.spaceguts.graphics.gui.menu.PauseMenu;
import com.bitwaffle.spaceout.Runner;

public class MenuCommandMap {
	
	/*
	 * TODO: Convert to some kind of interface so this can
	 * be created from the game (spaceout) instead of the
	 * engine (spaceguts)
	 */
	
	private static final String XML_PATH = "res/XML/";
	Map<String, MenuCommand> commandMap;
	
	public MenuCommandMap() {
		commandMap = new HashMap<String, MenuCommand>();
		commandMap.put("QUIT_GAME",new MenuCommandMap.Quit());
		commandMap.put("LOAD_GAME", new MenuCommandMap.LoadGame());
		
		/* Pause Menu stuff. */
		commandMap.put("RESUME", new MenuCommandMap.Resume());
		commandMap.put("MAIN_MENU", new MenuCommandMap.BackToMenu());
	}
	
	public MenuCommand getCommand(String str) {
		return commandMap.get(str);
	}
	
	public class Quit implements MenuCommand {

		@Override
		public void doCommand() {
			Runner.done = true;
		}
		
	}
	public class BackToMenu implements MenuCommand {

		@Override
		public void doCommand() {
			PauseMenu.backToMainMenu = true;
		}
		
	}
	
	public class Resume implements MenuCommand {

		@Override
		public void doCommand() {
			Runner.paused = false;
		}
		
	}	
	
	public class LoadGame implements MenuCommand {

		@Override
		public void doCommand() {
			System.out.println("Welcome to Spaceout!");
			// create the load menu
			LoadMenu lmenu = new LoadMenu(0, 0, XML_PATH);
			GUI.addGUIObject(lmenu);

			// let the GUI know that a menu is up
			GUI.menuUp = true;

			// done with the main menu
			MainMenu.done = true;
		}
		
	}
}
