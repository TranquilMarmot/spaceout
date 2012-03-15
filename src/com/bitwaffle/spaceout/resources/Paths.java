package com.bitwaffle.spaceout.resources;

/**
 * This houses paths for resources, to make it easy to change them if need be
 * @author TranquilMarmot
 *
 */
public enum Paths {
	// images
	MENU_IMAGE_PATH("res/images/gui/Menu/"),
	MENUBUTTON_IMAGE_PATH(MENU_IMAGE_PATH.path() + "Button/"),
	FILEPICKER_IMAGE_PATH("res/images/gui/FilePicker/"),
	BACKGROUND_IMAGE_PATH("res/images/gui/Menu/Background/"),
	PARTICLE_IMAGE_PATH("res/images/particles/"),
	
	CROSSHAIR_PATH("res/images/gui/crosshair.png"),
	BUILDER_IMAGE_PATH("res/images/gui/Builder/"),
	
	TEXTURE_PATH("res/images/textures/"),
	
	MODEL_PATH("res/models/"),
	SHIPS_PATH(MODEL_PATH.path() + "/ships/"),
	
	SHADER_PATH("res/shaders/"), 
	FONT_PATH("res/fonts/")
	;
	
	String path;
	private Paths(String path){
		this.path = path;
	}
	
	public String path(){
		return path;
	}
}
