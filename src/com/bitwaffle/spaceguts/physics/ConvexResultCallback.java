package com.bitwaffle.spaceguts.physics;

import java.util.ArrayList;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.CollisionWorld.LocalConvexResult;

/**
 * A custom ConvexResultCallback class! See jBullet's documentation of its ConvexResultCallback for more info.
 * This takes an ArrayList in the constructor, which it adds results to after a test has been performed;
 * The entities added to the list are cast to whatever type the ConvexReusltCallback is.
 * 
 * @author TranquilMarmot
 *
 * @param <T> Anything that extends DynamicEntity. The ArrayList passed into the constructor needs to be of the same type. 
 */
public class ConvexResultCallback<T extends DynamicEntity> extends CollisionWorld.ConvexResultCallback{
	/** list of hits */
	private ArrayList<T> hits;
	
	/**
	 * @param hits List to add hits to
	 * @param collisionMask What to look for
	 */
	public ConvexResultCallback(ArrayList<T> hits, short collisionMask){
		this.hits = hits;
		this.collisionFilterMask = collisionMask;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public float addSingleResult(LocalConvexResult convexResult, boolean normalInWorldSpace) {
		CollisionObject obj = convexResult.hitCollisionObject;
		
		DynamicEntity ent = (DynamicEntity) obj.getUserPointer();
		
		// dont' add the camera
		if(ent != Entities.camera)
			// might be doing some unsafe typecasting here, but I haven't run into any issues!
			hits.add((T)ent);
		
		return 0;
	}
	
}
