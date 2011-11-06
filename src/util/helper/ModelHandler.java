package util.helper;

import util.model.ModelLoader;

public class ModelHandler {
	private static Integer ship1List;
	
	public static final int SHIP1 = 98;
	
	public static int getCallList(int model){
		switch(model){
		case SHIP1:
			if(ship1List == null)
				initModel(SHIP1);
			return ship1List;
		}
		return 0;
	}
	
	private static void initModel(int model){
		switch(model){
		case SHIP1:
			ship1List = ModelLoader.loadPlyFile("res/test2.ply").get(0);
			break;
		}
	}
}
