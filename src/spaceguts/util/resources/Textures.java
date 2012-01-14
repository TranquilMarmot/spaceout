package spaceguts.util.resources;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;


public enum Textures {	
	// Environment
	STARS("PNG", Paths.TEXTURE_PATH.getPath() + "skybox.png", true, GL11.GL_NEAREST),
	WHITE("BMP", Paths.TEXTURE_PATH.getPath() + "white.bmp", true, GL11.GL_NEAREST),
	CHECKERS("BMP", Paths.TEXTURE_PATH.getPath() + "checkers.bmp", true, GL11.GL_NEAREST),
	
	// Model textures
	SHIP1("PNG", Paths.TEXTURE_PATH.getPath() + "ship1.png", false, GL11.GL_NEAREST),
	LASERBULLET("PNG", Paths.TEXTURE_PATH.getPath() + "laserbullet.png", false, GL11.GL_NEAREST),
	SAUCER("PNG", Paths.TEXTURE_PATH.getPath() + "saucer.png", false, GL11.GL_NEAREST),
	
	// Planets
	EARTH ("JPG", Paths.TEXTURE_PATH.getPath() + "earthbig.jpg", true, GL11.GL_NEAREST),
	MERCURY("JPG", Paths.TEXTURE_PATH.getPath() + "mercury.jpg", true, GL11.GL_NEAREST),
	VENUS("JPG", Paths.TEXTURE_PATH.getPath() + "venus.jpg", true, GL11.GL_NEAREST),
	MARS("JPG", Paths.TEXTURE_PATH.getPath() + "mars.jpg", true, GL11.GL_NEAREST),
	
	// GUI
	MENU_BACKGROUND1("JPG", Paths.BACKGROUND_IMAGE_PATH.getPath() + "apod1.jpg", false, GL11.GL_NEAREST),
	MENU_BACKGROUND2("JPG", Paths.BACKGROUND_IMAGE_PATH.getPath() + "apod2.jpg", false, GL11.GL_NEAREST),
	MENU_SPACEOUT_TEXT("PNG", Paths.MENU_IMAGE_PATH.getPath() + "spaceout.png", false, GL11.GL_NEAREST),
	
	MENU_PICKER_ACTIVE("PNG", Paths.FILEPICKER_IMAGE_PATH.getPath() + "active.png", false, GL11.GL_NEAREST),
	MENU_PICKER_MOUSEOVER("PNG", Paths.FILEPICKER_IMAGE_PATH.getPath() + "mouseover.png", false, GL11.GL_NEAREST),
	MENU_PICKER_SELECTED("PNG", Paths.FILEPICKER_IMAGE_PATH.getPath() + "selected.png", false, GL11.GL_NEAREST),
	MENU_PICKER_PRESSED("PNG", Paths.FILEPICKER_IMAGE_PATH.getPath() + "active.png", false, GL11.GL_NEAREST),
	
	MENU_BUTTON_ACTIVE("PNG",Paths.MENUBUTTON_IMAGE_PATH.getPath()  + "active.png", false, GL11.GL_NEAREST),
	MENU_BUTTON_MOUSEOVER("PNG",Paths.MENUBUTTON_IMAGE_PATH.getPath()  + "mouseover.png", false, GL11.GL_NEAREST),
	MENU_BUTTON_PRESSED("PNG",Paths.MENUBUTTON_IMAGE_PATH.getPath()  + "pressed.png", false, GL11.GL_NEAREST),
	MENU_BUTTON_INACTIVE("PNG",Paths.MENUBUTTON_IMAGE_PATH.getPath()  + "inactive.png", false, GL11.GL_NEAREST),
	
	
	;
	
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
	
	public Texture getTexture(){
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
