package util.manager;

import graphics.model.Model;
import graphics.model.ModelLoader;

/**
 * Manages any models. This makes it so only one model instance needs to be around but multiple entities can use it.
 * @author TranquilMarmot
 * @see Model
 *
 */
public class ModelManager {
	private static final String MODEL_PATH = "res/models/";
	
	//the models themselves
	private static Model ship1;
	private static Model laserbullet;
	
	// integers to refer to the models by
	public static final int SHIP1 = 98;
	public static final int LASERBULLET = 67;
	
	/**
	 * Returns a {@link Model} and initializes it if need be
	 * @param model The integer referring to the needed model (see declarations in this class)
	 * @return
	 */
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
	
	/**
	 * Initializes the given model
	 * @param model The integer referring to the model to initialize
	 */
	private static void initModel(int model){
		switch(model){
		case SHIP1:
			ship1 = ModelLoader.loadObjFile(MODEL_PATH + "ships/ship1.obj", 0.5f);
			break;
		case LASERBULLET:
			//laserbullet = ModelLoader.loadPlyFile(MODEL_PATH + "laserbullet.ply", 2.5f);
			laserbullet = ModelLoader.loadObjFile(MODEL_PATH + "laserbullet.obj", 2.5f);
			break;
		}
	}
}
