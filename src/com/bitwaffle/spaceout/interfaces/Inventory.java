package com.bitwaffle.spaceout.interfaces;

import java.util.ArrayList;

import com.bitwaffle.spaceout.entities.dynamic.Pickup;

/**
 * Anything that can pick up items should implement this interface
 * @author TranquilMarmot
 */
public interface Inventory {
	/**
	 * @param item Item to add to inventory
	 */
	public void addInventoryItem(Pickup item);
	
	/**
	 * @param item Item to remove from inventory
	 */
	public void removeInventoryItem(Pickup item);
	
	/**
	 * @return A list of all the current items
	 */
	public ArrayList<Pickup> getItems();
}
