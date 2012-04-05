package com.bitwaffle.spaceout.interfaces;

import java.util.ArrayList;

/**
 * Anything that can pick up items should implement this interface
 * @author TranquilMarmot
 */
public interface Inventory {
	/**
	 * @param item Item to add to inventory
	 */
	public void addInventoryItem(InventoryItem item);
	
	/**
	 * @param item Item to remove from inventory
	 */
	public void removeInventoryItem(InventoryItem item);
	
	/**
	 * @return A list of all the current items
	 */
	public ArrayList<InventoryItem> getItems();
}
