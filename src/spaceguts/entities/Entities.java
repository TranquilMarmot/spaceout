package spaceguts.entities;

import java.util.ArrayList;
import java.util.Iterator;
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
	public static ArrayList<Entity> passiveEntities = new ArrayList<Entity>();
	/** all the dynamic entities */
	public static ConcurrentHashMap<Integer, DynamicEntity> dynamicEntities = new ConcurrentHashMap<Integer, DynamicEntity>();
	/** all the current lights */
	public static ArrayList<Light> lights = new ArrayList<Light>();
	
	/** entities to add on next frame (to avoid ConcurrentModificationException) */
	public static ArrayList<Entity> passiveAddBuffer = new ArrayList<Entity>();
	/** entities to add on next frame (to avoid ConcurrentModificationException) */
	public static ArrayList<Light> lightAddBuffer = new ArrayList<Light>();
	
	
	/**
	 * entities to remove on next frame (to avoid
	 * ConcurrentModificationException)
	 */
	public static ArrayList<Entity> staticRemoveBuffer = new ArrayList<Entity>();
	/**
	 * entities to remove on next frame (to avoid
	 * ConcurrentModificationException)
	 */
	public static ArrayList<Light> lightRemoveBuffer = new ArrayList<Light>();
	
	public static void updateEntities(){
		checkBuffers();
		
		for(Entity ent : passiveEntities)
			ent.update();
		
		for(Light l : lights)
			l.update();
		
		if(camera != null)
			camera.update();
		
		if(skybox != null)
			skybox.update();	
	}
	
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

	/**
	 * @return Whether or not there are any entities at the moment
	 */
	public static boolean entitiesExist() {
		return passiveEntities.size() > 0 || dynamicEntities.size() > 0;
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
	 * Checks to see whether or not any entities need to be added or removed
	 */
	public static void checkBuffers() {
		// add any Entities in the addBuffer
		if (!passiveAddBuffer.isEmpty()) {
			Iterator<Entity> addIterator = passiveAddBuffer.iterator();
			while (addIterator.hasNext()) {
				Entity ent = addIterator.next();
				passiveEntities.add(ent);
				addIterator.remove();
			}
		}

		// remove any entities from the removeBuffer
		if (!staticRemoveBuffer.isEmpty()) {
			Iterator<Entity> removeIterator = staticRemoveBuffer.iterator();
			while (removeIterator.hasNext()) {
				Entity ent = removeIterator.next();
				passiveEntities.remove(ent);
				removeIterator.remove();
			}
		}
		// add any Entities in the addBuffer
		if (!lightAddBuffer.isEmpty()) {
			Iterator<Light> addIterator = lightAddBuffer.iterator();
			while (addIterator.hasNext()) {
				Light ent = addIterator.next();
				lights.add(ent);
				addIterator.remove();
			}
		}

		// remove any entities from the removeBuffer
		if (!lightRemoveBuffer.isEmpty()) {
			Iterator<Light> removeIterator = lightRemoveBuffer.iterator();
			while (removeIterator.hasNext()) {
				Light ent = removeIterator.next();
				lights.remove(ent);
				removeIterator.remove();
			}
		}
	}

	/**
	 * Delete all of the entities
	 */
	public static void cleanup() {
		for (Entity ent : passiveEntities) {
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
		passiveAddBuffer.clear();
		staticRemoveBuffer.clear();
		lightAddBuffer.clear();
		lightRemoveBuffer.clear();
	}
}
