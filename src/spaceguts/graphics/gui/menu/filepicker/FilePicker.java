package spaceguts.graphics.gui.menu.filepicker;

import spaceguts.graphics.gui.GUIObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

public class FilePicker extends GUIObject{
	File directory;
	FilePickerListItem selectedItem;
	FilePickerListItem[] list;

	public FilePicker(int x, int y, String path) {
		super(x, y);
		
		initDirectory(path);
		
		selectedItem = null;
		
		// TODO Auto-generated constructor stub
	}
	
	private void initDirectory(String path){
		directory = new File(path);
		
		String[] files = directory.list(new XMLFilter());
		list = new FilePickerListItem[files.length];
		
		int listItemHeight = 20;
		int listItemWidth = 200;
		
		for(int i = 0; i < files.length; i++){
			String file = files[i];
			int listX = this.x;
			int listY = this.y + (i * listItemHeight);
			list[i] = new FilePickerListItem(file, listX, listY, listItemHeight, listItemWidth);
			list[i].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					selectedItem = (FilePickerListItem)e.getSource();
				}
			});
		}
	}
	
	public FilePickerListItem getSelected(){
		return selectedItem;
	}

	@Override
	public void update() {
		for(FilePickerListItem item : list){
			item.update();
			
			if(item != selectedItem && item.selected)
				item.selected = false;
		}
		
		if(selectedItem != null && !selectedItem.selected)
			selectedItem.selected = true;
	}

	@Override
	public void draw() {
		for(FilePickerListItem item : list){
			item.draw();
		}
	}

}

class XMLFilter implements FilenameFilter{
	@Override
	public boolean accept(File directory, String filename) {
		CharSequence xml = ".xml";
		if(filename.contains(xml))
			return true;
		else
			return false;
	}
}
