package com.bitwaffle.spaceout.interfaces;

import com.bitwaffle.spaceout.entities.player.Backpack;

/**
 * An item that can be added to the player's inventory
 * @author TranquilMarmot
 *
 */
public interface InventoryItem {
	/**
	 * @return A string representing what this inventory item is
	 */
	public String getName();
	
	/**
	 * @param inventory Pick up the inventory item and add it to an Inventory
	 */
	public void pickup(Backpack inventory);
}
