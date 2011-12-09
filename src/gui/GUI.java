package gui;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Handles all the GUI objects
 * 
 * @author TranquilMarmot
 * 
 */
public class GUI {
	public static boolean menuUp = false;
	
	public static ArrayList<GUIObject> guiObjects = new ArrayList<GUIObject>();
	public static ArrayList<GUIObject> addBuffer = new ArrayList<GUIObject>();
	public static ArrayList<GUIObject> removeBuffer = new ArrayList<GUIObject>();

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
		
		for (GUIObject obj : guiObjects) {
			obj.update();
			if (obj.isVisible)
				obj.draw();
		}
	}
}
