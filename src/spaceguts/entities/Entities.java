package spaceguts.entities;

import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.util.vector.Vector3f;

import spaceout.entities.dynamic.Player;
import spaceout.entities.passive.Skybox;

/**
 * Handles all the current entities
 * 
 * @author TranquilMarmot
 * 
 */
public class Entities {
	/** player instance */
	public static Player player;
	/** camera instance */
	public static Camera camera;
	/** the skybox */
	public static Skybox skybox;

	/** all the current passive entities */
	public static ConcurrentHashMap<Integer, Entity> passiveEntities = new ConcurrentHashMap<Integer, Entity>();
	/** all the dynamic entities */
	public static ConcurrentHashMap<Integer, DynamicEntity> dynamicEntities = new ConcurrentHashMap<Integer, DynamicEntity>();
	/** all the current lights */
	public static ConcurrentHashMap<Integer, Light> lights = new ConcurrentHashMap<Integer, Light>();
	
	public static void addDynamicEntity(DynamicEntity ent){
		DynamicEntity test = dynamicEntities.put(ent.hashCode(), ent);
		/*
		 *  Check for any collisions
		 *  If two objects use the same hash code, the hash table overwrites the value at the
		 *  given key then returns the overwritten value
		 *  TODO this loop might go for a looong time if it keeps running into collisions, 
		 *  so it might be a good idea to change how this works 
		 */
		while(test != null){
			test = dynamicEntities.put(test.hashCode() + 5, test);
		}
	}
	
	public static void addPassiveEntity(Entity ent){
		Entity test = passiveEntities.put(ent.hashCode(), ent);
		/*
		 *  Check for any collisions
		 *  If two objects use the same hash code, the hash table overwrites the value at the
		 *  given key then returns the overwritten value
		 *  TODO this loop might go for a looong time if it keeps running into collisions, 
		 *  so it might be a good idea to change how this works 
		 */
		while(test != null){
			test = passiveEntities.put(test.hashCode() + 5, test);
		}
	}
	
	public static void addLight(Light light){
		Light test = lights.put(light.hashCode(), light);
		/*
		 *  Check for any collisions
		 *  If two objects use the same hash code, the hash table overwrites the value at the
		 *  given key then returns the overwritten value
		 *  TODO this loop might go for a looong time if it keeps running into collisions, 
		 *  so it might be a good idea to change how this works 
		 */
		while(test != null){
			test = lights.put(test.hashCode() + 5, test);
		}
	}

	/**
	 * @return Whether or not there are any entities at the moment
	 */
	public static boolean entitiesExist() {
		return !passiveEntities.isEmpty() || !dynamicEntities.isEmpty();
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
		for(Entity ent : passiveEntities.values()){
			ent.cleanup();
		}
		
		for(DynamicEntity ent : dynamicEntities.values()){
			ent.cleanup();
		}
		player = null;
		camera = null;
		passiveEntities.clear();
		dynamicEntities.clear();
		lights.clear();
	}
}
