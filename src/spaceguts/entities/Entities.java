package spaceguts.entities;

import java.util.ArrayList;

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
	public static ArrayList<Entity> passiveEntities = new ArrayList<Entity>(100);
	/** all the dynamic entities */
	public static ArrayList<DynamicEntity> dynamicEntities = new ArrayList<DynamicEntity>();
	/** all the current lights */
	public static ArrayList<Light> lights = new ArrayList<Light>();
	
	public static void addDynamicEntity(DynamicEntity ent){
		dynamicEntities.add(ent);
	}
	
	public static void addPassiveEntity(Entity ent){
		passiveEntities.add(ent);
	}
	
	public static void addLight(Light light){
		lights.add(light);
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
