package com.bitwaffle.spaceout.resources;

import com.bitwaffle.spaceguts.graphics.model.Model;
import com.bitwaffle.spaceguts.graphics.model.ModelLoader;

/**
 * All the possible models for the game. Each value in the enum also contains the path for the model, its scale, and which texture it uses.
 * @author TranquilMarmot
 *
 */
public enum Models {
	WING_X(Paths.SHIPS_PATH.path() + "wing_x", 0.5f, Textures.WING_X),
	LASERBULLET(Paths.MODEL_PATH.path() + "laserbullet", 3.5f, Textures.LASERBULLET),
	SAUCER(Paths.SHIPS_PATH.path() + "saucer", 1.0f, Textures.SAUCER),
	SKYBOX(Paths.MODEL_PATH.path() + "box", 100000.0f, Textures.SKYBOX),
	WESCOTT(Paths.SHIPS_PATH.path() + "wescott-8-beta", 1.0f, Textures.WESCOTT),
	DIAMOND(Paths.MODEL_PATH.path() + "diamond", 2.0f, Textures.DIAMOND),
	MISSILE(Paths.MODEL_PATH.path() + "missile", 1.0f, Textures.MISSILE);
	
	/** the actual model object */
	private Model model;
	/** which file to load the model from */
	private String file;
	/** what scale the model is at */
	private float scale;
	/** which texture to use for the model */
	private Textures texture;
	
	private Models(String file, float scale, Textures texture){
		this.file = file;
		this.scale = scale;
		this.texture = texture;
	}
	
	public Model getModel(){
		if(!modelLoaded())
			initModel();
		return model; 
	}
	
	public boolean modelLoaded(){
		return model != null;
	}
	
	public void initModel(){
		//System.out.println("initializing model " + this);
		if(!modelLoaded()){
			model = ModelLoader.loadObjFile(file, scale, texture);
		}
	}
	
	public void render(){
		
	}
}
