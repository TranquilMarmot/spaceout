package util.helper;

import graphics.model.ModelLoader;

public class ModelHandler {
	private static final String MODEL_PATH = "res/models/";
	private static Integer ship1List;
	private static Integer laserbulletList;
	
	public static final int SHIP1 = 98;
	public static final int LASERBULLET = 67;
	
	public static int getCallList(int model){
		switch(model){
		case SHIP1:
			if(ship1List == null)
				initModel(SHIP1);
			return ship1List;
		case LASERBULLET:
			if(laserbulletList == null)
				initModel(LASERBULLET);
			return laserbulletList;
		}
		return 0;
	}
	
	private static void initModel(int model){
		switch(model){
		case SHIP1:
			ship1List = ModelLoader.loadPlyFile(MODEL_PATH + "ships/ship1.ply", 0.5f).get(0);
			break;
		case LASERBULLET:
			laserbulletList = ModelLoader.loadPlyFile(MODEL_PATH + "laserbullet.ply", 1.0f).get(0);
			break;
		}
	}
}
