package com.bitwaffle.spaceout.entities.dynamic;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceout.entities.player.Backpack;
import com.bulletphysics.collision.shapes.CollisionShape;

/**
 * An item that can be added to the player's inventory.
 * Whenever something that can pick this up gets near it, it should use the
 * {@code setFollowing} method so that the pickup gravitates towards whatever is trying to
 * pick it up.
 * The pickup will follow whatever it's set to as long as the {@code following} is not null.
 * If you want a pickup to stop following, just set {@code following} to null.
 * 
 * @author TranquilMarmot
 */
public abstract class Pickup extends DynamicEntity{
	/** What this pickup is gravitating towards */
	private DynamicEntity following;
	
	/** How far this pickup can be grabbed from */
	private float pickupDistance;
	
	/** How fast the pickup gravitates towards what it's following */
	private float pickupSpeed = 2.0f;
	
	/** Inventory to add item to when it gets picked up */
	private Backpack destinationInventory;
	
	/**
	 * Pickup constructor
	 * @param location Location of pickup
	 * @param rotation Rotation of pickup
	 * @param shape Shape to use for pickup
	 * @param mass Mass of pickup
	 * @param restitution Resitution of pickup
	 * @param collisionGroup From {@link CollisionTypes}
	 * @param collidesWith From {@link CollisionTypes}
	 */
	public Pickup(Vector3f location, Quaternion rotation,
			CollisionShape shape, float mass, float restitution,
			short collisionGroup, short collidesWith) {
		super(location, rotation, shape, mass, restitution, collisionGroup,
				collidesWith);
	}
	
	
	@Override
	/**
	 * Update this pickup (anything extending this class should call super.update(timeStep))
	 */
	public void update(float timeStep){
		if(following != null)
			gravitateTowards(timeStep);
	}

	/**
	 * @param inventory Pick up the inventory item and add it to an Inventory
	 */
	public void pickup(Backpack inventory){
		inventory.addInventoryItem(this);
		removeFlag = true;
	}
	
	/**
	 * Sets what a pickup is following
	 * @param toFollow New entity to follow
	 * @param pickupDistance How far away the pickup can be picked up from
	 * @param destinationInventory Inventory to place pickup in when it is picked up
	 */
	public void setFollowing(DynamicEntity toFollow, float pickupDistance, Backpack destinationInventory){
		this.following = toFollow;
		this.pickupDistance = pickupDistance;
		this.destinationInventory = destinationInventory;
	}
	
	/**
	 * Gravitates towards whatever the pickup is following
	 * @param timeStep Time since last update
	 */
	private void gravitateTowards(float timeStep){
		// check if we're close enough to pick up
		if(Entities.distance(this.location, following.location) <= pickupDistance){
			this.pickup(destinationInventory);
		} else{
			// find the difference between this's location and following's location then negate it to go towards it
			Vector3f subtract = new Vector3f();
			Vector3f.sub(this.location, following.location, subtract);
			subtract.negate(subtract);
			
			// set linear velocity to go towards following
			javax.vecmath.Vector3f currentVeloc = new javax.vecmath.Vector3f();
			this.rigidBody.getLinearVelocity(currentVeloc);
			currentVeloc.set(new javax.vecmath.Vector3f(subtract.x * pickupSpeed, subtract.y * pickupSpeed, subtract.z * pickupSpeed));
			this.rigidBody.setLinearVelocity(currentVeloc);	
		}
	}
}
