package com.bitwaffle.spaceout.entities.dynamic;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceout.entities.player.Backpack;
import com.bulletphysics.collision.shapes.CollisionShape;

/**
 * An item that can be added to the player's inventory
 * @author TranquilMarmot
 *
 */
public abstract class Pickup extends DynamicEntity{
	private DynamicEntity gravitatingTowards;
	private float pickupDistance;
	private Backpack destinationInventory;
	
	public Pickup(Vector3f location, Quaternion rotation,
			CollisionShape shape, float mass, float restitution,
			short collisionGroup, short collidesWith) {
		super(location, rotation, shape, mass, restitution, collisionGroup,
				collidesWith);
	}
	
	@Override
	public void update(float timeStep){
		if(gravitatingTowards != null)
			gravitateTowards(timeStep);
	}

	/**
	 * @param inventory Pick up the inventory item and add it to an Inventory
	 */
	public void pickup(Backpack inventory){
		inventory.addInventoryItem(this);
		removeFlag = true;
	}
	
	public void setGravitateTowards(DynamicEntity other, float pickupDistance, Backpack destinationInventory){
		this.gravitatingTowards = other;
		this.pickupDistance = pickupDistance;
		this.destinationInventory = destinationInventory;
	}
	
	private void gravitateTowards(float timeStep){
		if(Entities.distance(this.location, gravitatingTowards.location) <= pickupDistance){
			this.pickup(destinationInventory);
		} else{
			Vector3f subtract = new Vector3f();
			Vector3f.sub(this.location, gravitatingTowards.location, subtract);
			subtract.negate(subtract);
			
			float speed = 10.0f;
			
			javax.vecmath.Vector3f currentVeloc = new javax.vecmath.Vector3f();
			this.rigidBody.getLinearVelocity(currentVeloc);
			currentVeloc.add(new javax.vecmath.Vector3f(subtract.x / speed, subtract.y / speed, subtract.z / speed));
			this.rigidBody.setLinearVelocity(currentVeloc);	
		}
	}
}
