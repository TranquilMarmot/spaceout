package spaceguts.util.manager;

import spaceguts.graphics.model.Model;
import spaceguts.graphics.model.ModelLoader;

/**
 * Manages any models. This makes it so only one model instance needs to be around but multiple entities can use it.
 * @author TranquilMarmot
 * @see Model
 *
 */
public class ModelManager {
	/*
	 * An easier way to do this would probably be to create a hash function for
	 * each string representing a model. Then, rather than storing them as random ints,
	 * they're stored as the has value. Then all the models are kept in a macthing
	 * hash table, The end result would be that you could request a model by string
	 * rather than by int (which would be much easier for storing save files- or would it?
	 * perhaps simply having an int in the save file representing which model
	 * is being used will be good enough)
	 */
	private static final String MODEL_PATH = "res/models/";
	
	//the models themselves
	private static Model ship1;
	private static Model laserbullet;
	private static Model spirit;
	private static Model saucer;
	
	// integers to refer to the models by
	public static final int WING_X = 98;
	public static final int LASERBULLET = 67;
	public static final int SPIRIT = 32;
	public static final int SAUCER = 45;
	
	/**
	 * Returns a {@link Model} and initializes it if need be
	 * @param model The integer referring to the needed model (see declarations in this class)
	 * @return
	 */
	public static Model getModel(int model){
		switch(model){
		case WING_X:
			if(ship1 == null)
				initModel(WING_X);
			return ship1;
		case LASERBULLET:
			if(laserbullet == null)
				initModel(LASERBULLET);
			return laserbullet;
		case SPIRIT:
			if(spirit == null)
				initModel(SPIRIT);
			return spirit;
		case SAUCER:
			if(saucer == null)
				initModel(SAUCER);
			return saucer;
		}
		return null;
	}
	
	/**
	 * Initializes the given model
	 * @param model The integer referring to the model to initialize
	 */
	private static void initModel(int model){
		switch(model){
		case WING_X:
			ship1 = ModelLoader.loadObjFile(MODEL_PATH + "ships/wing_x.obj", 0.5f, TextureManager.SHIP1);
			break;
		case LASERBULLET:
			laserbullet = ModelLoader.loadObjFile(MODEL_PATH + "laserbullet.obj", 2.5f, TextureManager.LASERBULLET);
			break;
		case SPIRIT:
			spirit = ModelLoader.loadObjFile(MODEL_PATH + "ships/spirit.obj", 0.1f, TextureManager.SPIRIT);
			break;
		case SAUCER :
			saucer = ModelLoader.loadObjFile(MODEL_PATH + "ships/saucer.obj", 1.0f, TextureManager.SAUCER);
			break;
		default:
			System.out.println("Error initializing model! " + model);
		}
	}
}
