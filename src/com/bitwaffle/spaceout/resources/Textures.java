package com.bitwaffle.spaceout.resources;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * This houses all of the possible textures for the game.
 * To use a texture, just do
 * <code>Textures.texture.texture.bind()</code>
 *  and then do any rendering with the image needed. 
 * @author TranquilMarmot
 *
 */
public enum Textures {	
	// Environment
	SKYBOX("PNG", Paths.TEXTURE_PATH.path() + "skybox.png", true, GL11.GL_NEAREST),
	WHITE("BMP", Paths.TEXTURE_PATH.path() + "white.bmp", true, GL11.GL_NEAREST),
	CHECKERS("BMP", Paths.TEXTURE_PATH.path() + "checkers.bmp", true, GL11.GL_NEAREST),
	
	// Model textures
	WING_X("PNG", Paths.SHIPS_PATH.path() + "wing_x/wing_x.png", false, GL11.GL_NEAREST),
	LASERBULLET("PNG", Paths.MODEL_PATH.path() + "laserbullet/laserbullet.png", false, GL11.GL_NEAREST),
	SAUCER("PNG", Paths.SHIPS_PATH.path() + "saucer/saucer.png", false, GL11.GL_NEAREST),
	WESCOTT("PNG", Paths.SHIPS_PATH.path() + "wescott-8-beta/wescott-8-beta.png", false, GL11.GL_NEAREST),
	DIAMOND("PNG", Paths.MODEL_PATH.path() + "diamond/diamond.png", false, GL11.GL_NEAREST),
	MISSILE("PNG", Paths.MODEL_PATH.path() + "missile/missile.png", false, GL11.GL_NEAREST),
	ASTEROID("PNG", Paths.MODEL_PATH.path() + "asteroid/asteroid.png", false, GL11.GL_LINEAR),
	
	// Planets
	EARTH ("JPG", Paths.TEXTURE_PATH.path() + "planets/earth.jpg", true, GL11.GL_LINEAR),
	MERCURY("JPG", Paths.TEXTURE_PATH.path() + "planets/mercury.jpg", true, GL11.GL_LINEAR),
	VENUS("JPG", Paths.TEXTURE_PATH.path() + "planets/venus.jpg", true, GL11.GL_LINEAR),
	MARS("JPG", Paths.TEXTURE_PATH.path() + "planets/mars.jpg", true, GL11.GL_LINEAR),
	SUN("JPG", Paths.TEXTURE_PATH.path() + "planets/sun.jpg", true, GL11.GL_LINEAR),
	
	// Particles
	FIRE("PNG", Paths.PARTICLE_IMAGE_PATH.path() + "fire.png", false, GL11.GL_NEAREST),
	TRAIL("PNG", Paths.PARTICLE_IMAGE_PATH.path() + "trail.png", false, GL11.GL_NEAREST),
	
	// GUI
	CROSSHAIR("PNG", Paths.CROSSHAIR_PATH.path(), true, GL11.GL_NEAREST),
	TARGET("PNG", Paths.TARGET_PATH.path(), false, GL11.GL_NEAREST),
	MENU_BACKGROUND1("JPG", Paths.BACKGROUND_IMAGE_PATH.path() + "apod1.jpg", false, GL11.GL_NEAREST),
	MENU_BACKGROUND2("JPG", Paths.BACKGROUND_IMAGE_PATH.path() + "apod2.jpg", false, GL11.GL_NEAREST),
	MENU_SPACEOUT_TEXT("PNG", Paths.MENU_IMAGE_PATH.path() + "spaceout.png", false, GL11.GL_NEAREST),
	
	MENU_PICKER_ACTIVE("PNG", Paths.FILEPICKER_IMAGE_PATH.path() + "active.png", false, GL11.GL_NEAREST),
	MENU_PICKER_MOUSEOVER("PNG", Paths.FILEPICKER_IMAGE_PATH.path() + "mouseover.png", false, GL11.GL_NEAREST),
	MENU_PICKER_SELECTED("PNG", Paths.FILEPICKER_IMAGE_PATH.path() + "selected.png", false, GL11.GL_NEAREST),
	MENU_PICKER_PRESSED("PNG", Paths.FILEPICKER_IMAGE_PATH.path() + "active.png", false, GL11.GL_NEAREST),
	
	MENU_BUTTON_ACTIVE("PNG",Paths.MENUBUTTON_IMAGE_PATH.path()  + "active.png", false, GL11.GL_NEAREST),
	MENU_BUTTON_MOUSEOVER("PNG",Paths.MENUBUTTON_IMAGE_PATH.path()  + "mouseover.png", false, GL11.GL_NEAREST),
	MENU_BUTTON_PRESSED("PNG",Paths.MENUBUTTON_IMAGE_PATH.path()  + "pressed.png", false, GL11.GL_NEAREST),
	MENU_BUTTON_INACTIVE("PNG",Paths.MENUBUTTON_IMAGE_PATH.path()  + "inactive.png", false, GL11.GL_NEAREST),
	
	BUILDER_OPEN("PNG", Paths.BUILDER_IMAGE_PATH.path + "open.png", true, GL11.GL_NEAREST),
	BUILDER_GRABBED("PNG", Paths.BUILDER_IMAGE_PATH.path + "grabbed.png", true, GL11.GL_NEAREST);
	
	/** the texture for the texture object */
	private Texture texture;
	
	/** the format of the file and the actual file path */
	private String format, file;
	
	/** whether or not the image is flipped */
	private boolean flipped;
	
	/** the GL filter to use for the image */
	private int filter;
	private Textures(String format, String file,
			boolean flipped, int filter){
		this.format = format;
		this.file = file;
		this.flipped = flipped;
		this.filter = filter;
	}
	
	/**
	 * @return Texture object for this texture reference. If the texture isn't loaded yet, it will be (which can cause the game to stop for a second- try to load textures with initTexture before using them!)
	 */
	public Texture texture(){
		if(!textureLoaded())
			initTexture();
		return texture; 
	}
	
	/**
	 * Initializes the texture object for this texture reference
	 */
	protected void initTexture(){
		if(!textureLoaded()){
			try{
				FileInputStream fis = new FileInputStream(file);
				texture = TextureLoader.getTexture(format, fis, flipped, filter);
				fis.close();
			} catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return Whether or not the texture object for this texture object has been loaded
	 */
	public boolean textureLoaded(){
		return texture != null;
	}
}
