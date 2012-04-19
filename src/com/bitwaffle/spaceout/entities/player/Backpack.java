package com.bitwaffle.spaceout.entities.player;

import java.util.ArrayList;

import com.bitwaffle.spaceguts.entities.Pickup;

public class Backpack {
	private ArrayList<Pickup> items;
	
		
	public Backpack(){
		items = new ArrayList<Pickup>();
	}
	
	public void addInventoryItem(Pickup item){
		items.add(item);
	}
	
	public void removeInventoryItem(Pickup item){
		items.remove(item);
	}
	
	public ArrayList<Pickup> getItems(){
		return items;
	}
}
