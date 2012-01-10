package spaceguts.graphics.gui;

import java.util.ArrayList;
import java.util.Iterator;

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
	
	/** GUI objects to add on the next update */
	public static ArrayList<GUIObject> addBuffer = new ArrayList<GUIObject>();
	
	/** GUI objects to remove on the next update*/
	public static ArrayList<GUIObject> removeBuffer = new ArrayList<GUIObject>();

	/**
	 * Updates and renders all GUI objects
	 */
	public static void updateAndRenderGUI() {
		// remove any entities from the removeBuffer
		if (!removeBuffer.isEmpty()) {
			Iterator<GUIObject> removeIterator = removeBuffer.iterator();
			while (removeIterator.hasNext()) {
				GUIObject ent = removeIterator.next();
				guiObjects.remove(ent);
				removeIterator.remove();
			}
		}
		
		// add any Entities in the addBuffer
		if (!addBuffer.isEmpty()) {
			Iterator<GUIObject> addIterator = addBuffer.iterator();
			while (addIterator.hasNext()) {
				GUIObject ent = addIterator.next();
				guiObjects.add(ent);
				addIterator.remove();
			}
		}
		
		// update and render
		for (GUIObject obj : guiObjects) {
			obj.update();
			if (obj.isVisible)
				obj.draw();
		}
	}
}
