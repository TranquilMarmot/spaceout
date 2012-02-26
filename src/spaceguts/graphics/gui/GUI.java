package spaceguts.graphics.gui;

import java.util.ArrayList;

import spaceguts.util.Debug;

/**
 * Handles all the GUI objects
 * 
 * @author TranquilMarmot
 * 
 */
public class GUI {
	/** whether or not a menu is up right now- needs to be set whenever a menu is added/removed */
	public static boolean menuUp = false;
	
	/** all the GUI objects to draw */
	public static ArrayList<GUIObject> guiObjects = new ArrayList<GUIObject>();
	
	/**
	 * Updates the GUI
	 */
	public static void update(){
		Debug.update();
		
		for(GUIObject obj : guiObjects){
			obj.update();
		}
	}
	
	/**
	 * Draws the GUI
	 */
	public static void draw(){
		Debug.draw();
		
		for(GUIObject obj : guiObjects){
			obj.draw();
		}
	}
	
	public static void addGUIObject(GUIObject obj){
		guiObjects.add(obj);
	}
	
	public static void removeGUIObject(GUIObject obj){
		guiObjects.remove(obj);
	}
}
