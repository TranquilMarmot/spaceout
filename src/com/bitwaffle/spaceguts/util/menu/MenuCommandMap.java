package com.bitwaffle.spaceguts.util.menu;

import java.util.HashMap;
import java.util.Map;

public class MenuCommandMap {
	
	Map<String, MenuCommand> commandMap = new HashMap<String, MenuCommand>(); 

	public class Quit implements MenuCommand {

		@Override
		public void doCommand() {
			// TODO Auto-generated method stub
			
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
			// TODO Auto-generated method stub
			
		}
		
	}
}
