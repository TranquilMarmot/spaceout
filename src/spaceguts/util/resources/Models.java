package spaceguts.util.resources;

import spaceguts.util.model.Model;
import spaceguts.util.model.ModelLoader;

public enum Models {
	WING_X(Paths.MODEL_PATH.getPath() + "ships/wing_x.obj", 0.5f, Textures.SHIP1),
	LASERBULLET(Paths.MODEL_PATH.getPath() + "laserbullet.obj", 1.0f, Textures.LASERBULLET),
	SAUCER(Paths.MODEL_PATH.getPath() + "ships/saucer.obj", 1.0f, Textures.SAUCER),
	SKYBOX(Paths.MODEL_PATH.getPath() + "skybox.obj", 100000.0f, Textures.STARS);
	
	private Model model;
	private String file;
	private float scale;
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
}
