package spaceguts.graphics.gui.menu.picker;

import java.awt.event.ActionEvent;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import spaceguts.graphics.gui.button.RectangleButton;
import spaceguts.util.Debug;
import spaceguts.util.DisplayHelper;
import spaceout.resources.Textures;

public class ListItem<T> extends RectangleButton{
	private T object;
	public boolean selected;
	
	private Textures currentImage, activeImage, mouseOverImage, pressedImage, selectedImage;
	
	int xOffset, yOffset;
	

	public ListItem(T object, int xOffsetFromCenter, int yOffsetFromCenter, int height, int width, Textures activeImage, Textures mouseOverImage, Textures pressedImage, Textures selectedImage) {
		super(0, 0, height, width);
		this.xOffset = xOffsetFromCenter;
		this.yOffset = yOffsetFromCenter;
		this.activeImage = activeImage;
		this.mouseOverImage = mouseOverImage;
		this.pressedImage = pressedImage;
		this.selectedImage = selectedImage;
		this.object = object;
		
		currentImage = activeImage;
	}
	
	public T getValue(){
		return object;
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
		buttonListener.actionPerformed(new ActionEvent(this, 1, object.toString()));
	}

	@Override
	public void mouseOverEvent() {
		currentImage = mouseOverImage;
	}

	@Override
	public void pressedEvent() {
		currentImage = pressedImage;
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
			currentImage.texture().bind();
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

				GL11.glTexCoord2f(currentImage.texture().getWidth(), 0);
				GL11.glVertex2i(x + width, y);

				GL11.glTexCoord2f(currentImage.texture().getWidth(), currentImage.texture().getHeight());
				GL11.glVertex2i(x + width, y + height);

				GL11.glTexCoord2f(0, currentImage.texture().getHeight());
				GL11.glVertex2i(x, y + height);
			}
			GL11.glEnd();
			
			int textWidth = Debug.font.getWidth(object.toString());
			int textHeight = Debug.font.getAscent();
			
			int textX = this.x + ((this.width - textWidth) / 2);
			int textY = this.y + ((this.height - textHeight) / 2) - 2;
			
			Debug.font.drawString(textX, textY, object.toString(), Color.white);
		}
	}

}
