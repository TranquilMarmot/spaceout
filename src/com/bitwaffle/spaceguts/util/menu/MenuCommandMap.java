package com.bitwaffle.spaceguts.util.menu;

import java.util.HashMap;
import java.util.Map;

import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.graphics.gui.menu.LoadMenu;
import com.bitwaffle.spaceguts.graphics.gui.menu.MenuTester;
import com.bitwaffle.spaceout.Runner;

public class MenuCommandMap {
	
	private static final String XML_PATH = "res/XML/";
	Map<String, MenuCommand> commandMap;
	
	public MenuCommandMap() {
		commandMap = new HashMap<String, MenuCommand>();
		commandMap.put("QUIT",new MenuCommandMap.Quit());
		commandMap.put("LOAD_GAME", new MenuCommandMap.LoadGame());
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
	public class QuitToMenu implements MenuCommand {

		@Override
		public void doCommand() {
			// TODO Auto-generated method stub
			
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
			MenuTester.done = true;
		}
		
	}
}
