package spaceguts.util.resources;

public enum Paths {
	// images
	MENU_IMAGE_PATH("res/images/gui/Menu/"),
	MENUBUTTON_IMAGE_PATH(MENU_IMAGE_PATH.getPath() + "Button/"),
	FILEPICKER_IMAGE_PATH("res/images/gui/FilePicker/"),
	BACKGROUND_IMAGE_PATH("res/images/gui/Menu/Background/"),
	
	TEXTURE_PATH("res/images/textures/"),
	
	MODEL_PATH("res/models/"),
	;
	
	String path;
	private Paths(String path){
		this.path = path;
	}
	
	public String getPath(){
		return path;
	}
}
