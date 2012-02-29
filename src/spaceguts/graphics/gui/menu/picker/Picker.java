package spaceguts.graphics.gui.menu.picker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import spaceguts.graphics.gui.GUIObject;
import spaceout.resources.Textures;

public class Picker<T> extends GUIObject{
	ListItem<T> selectedItem;
	ListItem<T>[] list;

	@SuppressWarnings("unchecked")
	public Picker(int x, int y, int listItemHeight, int listItemWidth, T[] values, Textures activeImage, Textures mouseOverImage, Textures pressedImage, Textures selectedImage) {
		super(x, y);
		
		list = new ListItem[values.length];
		
		for(int i = 0; i < values.length; i++){
			int listX = this.x;
			int listY = this.y + (i * listItemHeight);
			list[i] = new ListItem<T>(values[i], listX, listY, listItemHeight, listItemWidth, activeImage, mouseOverImage, pressedImage, selectedImage);
			list[i].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					selectedItem = (ListItem<T>)e.getSource();
				}
			});
		}
		
		selectedItem = null;
	}
	
	public T getSelectedItem(){
		return selectedItem.getValue();
	}
	
	public boolean itemHasBeenSelected(){
		return selectedItem != null;
	}
	
	@Override
	public void update(){
		for(ListItem<T> item : list){
			item.update();
			
			if(item != selectedItem && item.selected)
				item.selected = false;
		}
		
		if(selectedItem != null && !selectedItem.selected)
			selectedItem.selected = true;
	}
	
	@Override
	public void draw(){
		for(ListItem<T> item : list)
			item.draw();
	}
}
