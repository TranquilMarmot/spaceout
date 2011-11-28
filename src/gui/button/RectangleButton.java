package gui.button;

import javax.vecmath.Color3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.geom.Circle;

import util.manager.MouseManager;
import util.manager.TextureManager;

public class RectangleButton extends Button {
	/** rectangle representing this button */
	Rectangle rectangle;
	
	/** the button's size */
	public int height, width;
	
	private Color3f color;

	public RectangleButton(int x, int y, int height, int width) {
		super(x, y);
		this.height = height;
		this.width = width;
		rectangle = new Rectangle();
		rectangle.setX(x);
		rectangle.setY(y);
		rectangle.setWidth(width);
		rectangle.setHeight(height);
		
		color = new Color3f(1.0f, 0.0f, 0.0f);
	}

	@Override
	public void update() {
		if (rectangle.contains(MouseManager.x, MouseManager.y)) {
			mouseOver = true;
		} else{
			mouseOver = false;
		}
		
		if(mouseOver){
			if (MouseManager.button0) {
				pressed = true;
			} else{
				pressed = false;
			}
		// the button was pressed, then the mouse was moved off of it but the mouse button was held down
		} else if(pressed && MouseManager.button0){
			pressed = true;
		} else {
			pressed = false;
		}
		
		color.x = 1.0f;
		color.y = 0.0f;
		color.z = 0.0f;
		
		if(mouseOver){
			color.x = 0.0f;
			color.y = 0.0f;
			color.z = 1.0f;
		}
		
		if(pressed){
			color.x = 0.0f;
			color.y = 1.0f;
			color.z = 0.0f;
		}
		
		
	}

	@Override
	public void draw() {
		GL11.glColor3f(color.x, color.y, color.z);
		TextureManager.getTexture(TextureManager.CHECKERS).bind();
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x, y);

			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2i(x + width, y);

			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2i(x + width, y + height);

			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2i(x, y + height);
		}
		GL11.glEnd();
	}

}
