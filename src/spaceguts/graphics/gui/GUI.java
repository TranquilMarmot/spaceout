package spaceguts.graphics.gui;

import java.util.concurrent.ConcurrentHashMap;

import spaceguts.util.debug.Debug;

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
	public static ConcurrentHashMap<Integer, GUIObject> guiObjects = new ConcurrentHashMap<Integer, GUIObject>();
	
	/**
	 * Updates the GUI
	 */
	public static void update(){
		Debug.update();
		
		for(GUIObject obj : guiObjects.values()){
			obj.update();
		}
	}
	
	/**
	 * Draws the GUI
	 */
	public static void draw(){
		Debug.draw();
		
		for(GUIObject obj : guiObjects.values()){
			obj.draw();
		}
	}
	
	public static void addGUIObject(GUIObject obj){
		GUIObject test = guiObjects.put(obj.hashCode(), obj);
		
		while(test != null){
			test = guiObjects.put(test.hashCode() + 5, test);
		}
	}
}
