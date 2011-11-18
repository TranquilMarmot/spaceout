package util.helper;

import graphics.model.Model;
import graphics.model.ModelLoader;

public class ModelHandler {
	private static final String MODEL_PATH = "res/models/";
	private static Model ship1;
	private static Model laserbullet;
	
	public static final int SHIP1 = 98;
	public static final int LASERBULLET = 67;
	
	public static Model getModel(int model){
		switch(model){
		case SHIP1:
			if(ship1 == null)
				initModel(SHIP1);
			return ship1;
		case LASERBULLET:
			if(laserbullet == null)
				initModel(LASERBULLET);
			return laserbullet;
		}
		return null;
	}
	
	private static void initModel(int model){
		switch(model){
		case SHIP1:
			ship1 = ModelLoader.loadObjFile(MODEL_PATH + "ships/ship1.obj", 0.5f);
			break;
		case LASERBULLET:
			laserbullet = ModelLoader.loadObjFile(MODEL_PATH + "laserbullet.obj", 2.5f);
			break;
		}
	}
}
