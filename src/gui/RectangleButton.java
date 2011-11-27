package gui;

import org.lwjgl.util.Rectangle;

import util.manager.MouseManager;

public class RectangleButton extends Button{
	Rectangle rectangle;
	
	public RectangleButton(int x, int y, int height, int width){
		super(x, y);
		rectangle = new Rectangle();
		rectangle.setBounds(x, y, width, height);
	}

	@Override
	void update() {
		//FIXME might be necessary to change the mouse's x and y values based on current window width/height
		if(rectangle.contains((int)MouseManager.x, (int)MouseManager.y)){
			mouseOver = true;
			System.out.println("mouse over!");
		} else
			mouseOver = false;
		
		
		
	}

	@Override
	void draw() {
		// TODO Auto-generated method stub
		
	}
	
}
