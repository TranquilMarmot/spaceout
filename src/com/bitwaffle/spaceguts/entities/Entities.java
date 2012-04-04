package com.bitwaffle.spaceguts.entities;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceout.entities.dynamic.Player;
import com.bitwaffle.spaceout.entities.passive.Skybox;


/**
 * Handles all the current entities
 * 
 * @author TranquilMarmot
 */
public class Entities {
	/** player instance */
	public static Player player;
	/** camera instance */
	public static Camera camera;
	/** the skybox */
	public static Skybox skybox;

	/** all the current passive entities */
	public static ArrayList<Entity> passiveEntities = new ArrayList<Entity>(10), passiveEntitiesToAdd = new ArrayList<Entity>(10), passiveEntitiesToRemove = new ArrayList<Entity>(10);
	/** all the dynamic entities */
	public static ArrayList<DynamicEntity> dynamicEntities = new ArrayList<DynamicEntity>(100);
	/** all the current lights */
	public static ArrayList<Light> lights = new ArrayList<Light>(8), lightsToAdd = new ArrayList<Light>(8), lightsToRemove = new ArrayList<Light>(8);
	
	/**
	 * Updates everything that doesn't get updated by the {@link DynamicEntityCallback}
	 * @param timeStep How much time has passed since the last tick (passed in from DynamicEntityCallback)
	 */
	public static void updateAll(float timeStep){
		/*
		 * Since passiveEntities and lights all get updated by iterating through their lists,
		 * directly adding and removing  from the list can cause a ConcurrentModificationException.
		 * Everything in DynamicEntities is updated in the DynamicEntityCallback, and as such the list
		 * in here is used solely for rendering. So adds and removes can be done directly on the
		 * list.
		 */
		if(!passiveEntitiesToRemove.isEmpty()){
			for(Entity ent : passiveEntitiesToRemove)
				passiveEntities.remove(ent);
			passiveEntitiesToRemove.clear();
		}
		
		if(!passiveEntitiesToAdd.isEmpty()){
			for(Entity ent : passiveEntitiesToAdd)
				passiveEntities.add(ent);
			passiveEntitiesToAdd.clear();
		}
		
		if(!lightsToRemove.isEmpty()){
			for(Light l : lightsToRemove)
				lights.remove(l);
			lightsToRemove.clear();
		}
		
		if(!lightsToAdd.isEmpty()){
			for(Light l : lightsToAdd)
				lights.add(l);
			lightsToAdd.clear();
		}
		
		camera.update(timeStep);
		skybox.update(timeStep);
		
		for(Entity ent : passiveEntities)
			ent.update(timeStep);
		
		for(Light l : lights)
			l.update(timeStep);
	}
	
	/**
	 * Adds the given DynamicEntity to the rendering world
	 * @param ent Entity to add
	 */
	public static void addDynamicEntity(DynamicEntity ent){
		dynamicEntities.add(ent);
	}
	
	/**
	 * Adds the given Entity to the rendering world
	 * @param ent Entity to add
	 */
	public static void addPassiveEntity(Entity ent){
		passiveEntitiesToAdd.add(ent);
	}
	
	/**
	 * Adds the given Light to the rendering world
	 * @param light Light to add
	 */
	public static void addLight(Light light){
		lightsToAdd.add(light);
	}
	
	/**
	 * Removes the given DynamicEntity from the rendering world
	 * @param ent Entity to remove
	 */
	public static void removeDynamicEntity(DynamicEntity ent){
		dynamicEntities.remove(ent);
	}
	
	/**
	 * Removes the given Entity from the rendering world
	 * @param ent Entity to remove
	 */
	public static void removePassiveEntity(Entity ent){
		passiveEntitiesToRemove.add(ent);
	}
	
	/**
	 * Removes the given Light from the rendering world
	 * @param light Light to remove
	 */
	public static void removeLight(Light l){
		lightsToRemove.add(l);
	}

	/**
	 * @return Whether or not there are any entities at the moment
	 */
	public static boolean entitiesExist() {
		return !passiveEntities.isEmpty() || !dynamicEntities.isEmpty() || player != null;
	}

	/**
	 * Gets the distance between two vectors
	 * 
	 * @param first
	 *            The first vector
	 * @param second
	 *            The second vector
	 * @return The distance between the two vectors
	 */
	public static float distance(Vector3f first, Vector3f second) {
		float xDist = first.x - second.x;
		float yDist = first.y - second.y;
		float zDist = first.z - second.z;

		float xSqr = xDist * xDist;
		float ySqr = yDist * yDist;
		float zSqr = zDist * zDist;

		double total = (double) (xSqr + ySqr + zSqr);

		return (float) Math.sqrt(total);
	}

	/**
	 * Delete all of the entities
	 */
	public static void cleanup() {
		for(Entity ent : passiveEntities){
			ent.cleanup();
		}
		
		for(DynamicEntity ent : dynamicEntities){
			ent.cleanup();
		}
		player = null;
		camera = null;
		passiveEntities.clear();
		dynamicEntities.clear();
		lights.clear();
	}
}
