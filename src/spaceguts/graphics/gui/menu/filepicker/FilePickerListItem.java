package spaceguts.graphics.gui.menu.filepicker;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import spaceguts.util.debug.Debug;

import spaceguts.graphics.DisplayHelper;
import spaceguts.graphics.gui.button.RectangleButton;

public class FilePickerListItem extends RectangleButton{
	/** where the images for this button are */
	private static final String IMAGE_PATH = "res/images/gui/FilePicker/";
	
	public boolean selected = false;
	
	/** image this button uses */
	protected static Texture activeImage, mouseOverImage, selectedImage, pressedImage;
	
	protected Texture currentImage;
	
	private String file;
	private String printString;
	
	int xOffset, yOffset;

	public FilePickerListItem(String file, int xOffsetFromCenter, int yOffsetFromCenter, int height, int width) {
		super(0, 0, height, width);
		this.xOffset = xOffsetFromCenter;
		this.yOffset = yOffsetFromCenter;
		this.file = file;
		printString = file.substring(0, file.indexOf("."));
		
		initImages();
	}
	
	private void initImages(){
		try{
			if(activeImage == null)
				activeImage = TextureLoader.getTexture("PNG", new FileInputStream(IMAGE_PATH + "active.png"));
			if(mouseOverImage == null)
				mouseOverImage = TextureLoader.getTexture("PNG", new FileInputStream(IMAGE_PATH + "mouseover.png"));
			if(selectedImage == null)
				selectedImage = TextureLoader.getTexture("PNG", new FileInputStream(IMAGE_PATH +"selected.png"));
			if(pressedImage == null)
				pressedImage = TextureLoader.getTexture("PNG", new FileInputStream(IMAGE_PATH + "pressed.png"));
		} catch (IOException e){
			e.printStackTrace();
		}
		currentImage = activeImage;
	}

	@Override
	public void activeEvent() {
		currentImage = activeImage;
	}

	@Override
	public void inactiveEvent() {
	}

	@Override
	public void releasedEvent() {
		buttonListener.actionPerformed(new ActionEvent(this, 1, file));
	}

	@Override
	public void mouseOverEvent() {
		currentImage = mouseOverImage;
		
	}

	@Override
	public void pressedEvent() {
		currentImage = pressedImage;
	}
	
	public String getFile(){
		return file;
	}
	
	@Override
	public void update(){
		super.update();
		
		if(this.selected)
			currentImage = selectedImage;
		
		// keep the button in the middle of the screen
		this.x = (DisplayHelper.windowWidth / 2) - (this.width / 2) + xOffset;
		this.y = (DisplayHelper.windowHeight / 2) - (this.height / 2) + yOffset;
		this.rectangle.setX(this.x);
		this.rectangle.setY(this.y);
	}

	@Override
	public void draw() {
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		if (this.isVisible) {
			currentImage.bind();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glBegin(GL11.GL_QUADS);
			{	
				/*
				 * currentImage.getWidth() and currentImage.getHeight() return the actual height of the texture.
				 * My best guess is that the image gets put into the smallest possible texture that has dimensions that are
				 * powers of 2 by Slick, because OpenGL can handle those much better.
				 */
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2i(x, y);

				GL11.glTexCoord2f(currentImage.getWidth(), 0);
				GL11.glVertex2i(x + width, y);

				GL11.glTexCoord2f(currentImage.getWidth(), currentImage.getHeight());
				GL11.glVertex2i(x + width, y + height);

				GL11.glTexCoord2f(0, currentImage.getHeight());
				GL11.glVertex2i(x, y + height);
			}
			GL11.glEnd();
			
			int textWidth = Debug.font.getWidth(file);
			int textHeight = Debug.font.getAscent();
			
			int textX = this.x + ((this.width - textWidth) / 2);
			int textY = this.y + ((this.height - textHeight) / 2) - 2;
			
			Debug.font.drawString(textX, textY, printString, Color.white);
		}
	}	
}