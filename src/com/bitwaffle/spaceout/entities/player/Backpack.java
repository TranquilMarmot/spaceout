package com.bitwaffle.spaceout.entities.player;

import java.util.ArrayList;

import com.bitwaffle.spaceguts.entities.Pickup;

public class Backpack {
	private ArrayList<Pickup> items;
	
	private int diamonds;
	
		
	public Backpack(){
		items = new ArrayList<Pickup>();
		diamonds = 0;
	}
	
	public void addInventoryItem(Pickup item){
		if(item.type.equals("Diamond"))
			diamonds++;
		else
			items.add(item);
	}
	
	public void removeInventoryItem(Pickup item){
		items.remove(item);
	}
	
	public ArrayList<Pickup> getItems(){
		return items;
	}
	
	public int numDiamonds(){
		return diamonds;
	}
}
