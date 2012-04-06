package com.bitwaffle.spaceguts.entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceout.entities.player.Backpack;
import com.bulletphysics.collision.dispatch.CollisionObject;
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
	protected DynamicEntity following;
	
	/** How far this pickup can be grabbed from */
	private float pickupDistance;
	
	/** How fast the pickup gravitates towards what it's following */
	private float pickupSpeed = 1.8f;
	
	/** Inventory to add item to when it gets picked up */
	private Backpack destinationInventory;
	
	/** Used to increase speed over time while following */
	float timeFollowing;
	
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
		
		// so the diamonds don't deactivate at the wrong times
		this.rigidBody.setActivationState(CollisionObject.ISLAND_SLEEPING);
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
			timeFollowing += timeStep;
			
			// find the difference between this's location and following's location then negate it to go towards it
			Vector3f subtract = new Vector3f();
			Vector3f.sub(this.location, following.location, subtract);
			subtract.negate(subtract);
			
			javax.vecmath.Vector3f linvec = new javax.vecmath.Vector3f();
			this.following.rigidBody.getLinearVelocity(linvec);
			
			// add linear velocity so that the pickup doesn't trail behind
			float dx = (subtract.x * pickupSpeed) + linvec.x;
			float dy = (subtract.y * pickupSpeed) + linvec.y;
			float dz = (subtract.z * pickupSpeed) + linvec.z;
			
			// set linear velocity to go towards following
			this.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(dx, dy, dz));
			
			// give it a good twirl
			this.rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(subtract.x, subtract.y, subtract.z));
		}
	}
}
