package gui;

import java.util.ArrayList;

/**
 * Handles all the GUI objects
 * @author TranquilMarmot
 *
 */
public class GUI {
	public static ArrayList<GUIObject> guiObjects = new ArrayList<GUIObject>();
	
	public static void updateAndRenderGUI(){
		for(GUIObject obj : guiObjects){
			obj.update();
			if(obj.isVisible)
				obj.draw();
		}
	}
}
