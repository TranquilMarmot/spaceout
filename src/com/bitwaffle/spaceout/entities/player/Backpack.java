package com.bitwaffle.spaceout.entities.player;

import java.util.ArrayList;

import com.bitwaffle.spaceout.interfaces.InventoryItem;

public class Backpack {
	private ArrayList<InventoryItem> items;
	
		
	public Backpack(){
		items = new ArrayList<InventoryItem>();
	}
	
	public void addInventoryItem(InventoryItem item){
		items.add(item);
	}
	
	public void removeInventoryItem(InventoryItem item){
		items.remove(item);
	}
	
	public ArrayList<InventoryItem> getItems(){
		return items;
	}
}
