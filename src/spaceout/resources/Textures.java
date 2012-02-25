package spaceout.resources;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;


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
	
	// Planets
	EARTH ("JPG", Paths.TEXTURE_PATH.path() + "planets/earth.jpg", true, GL11.GL_LINEAR),
	MERCURY("JPG", Paths.TEXTURE_PATH.path() + "planets/mercury.jpg", true, GL11.GL_LINEAR),
	VENUS("JPG", Paths.TEXTURE_PATH.path() + "planets/venus.jpg", true, GL11.GL_LINEAR),
	MARS("JPG", Paths.TEXTURE_PATH.path() + "planets/mars.jpg", true, GL11.GL_LINEAR),
	SUN("JPG", Paths.TEXTURE_PATH.path() + "planets/sun.jpg", true, GL11.GL_LINEAR),
	
	// GUI
	CROSSHAIR("PNG", Paths.CROSSHAIR_PATH.path(), false, GL11.GL_NEAREST),
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
	MENU_BUTTON_INACTIVE("PNG",Paths.MENUBUTTON_IMAGE_PATH.path()  + "inactive.png", false, GL11.GL_NEAREST);
	
	private Texture texture;
	private String format, file;
	private boolean flipped;
	private int filter;
	private Textures(String format, String file,
			boolean flipped, int filter){
		this.format = format;
		this.file = file;
		this.flipped = flipped;
		this.filter = filter;
	}
	
	public Texture texture(){
		if(!textureLoaded())
			initTexture();
		return texture; 
	}
	
	public void initTexture(){
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
	
	public boolean textureLoaded(){
		return texture != null;
	}
}
