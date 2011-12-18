package entities;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import entities.dynamic.DynamicEntity;
import entities.dynamic.Player;

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

	/** all the current entities */
	public static ArrayList<Entity> staticEntities = new ArrayList<Entity>();
	/** all the dynamic entities */
	public static ArrayList<DynamicEntity> dynamicEntities = new ArrayList<DynamicEntity>();
	/** all the current lights */
	public static ArrayList<Light> lights = new ArrayList<Light>();
	
	/** entities to add on next frame (to avoid ConcurrentModificationException) */
	public static ArrayList<Entity> staticAddBuffer = new ArrayList<Entity>();
	/** entities to add on next frame (to avoid ConcurrentModificationException) */
	public static ArrayList<DynamicEntity> dynamicAddBuffer = new ArrayList<DynamicEntity>();
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
	public static ArrayList<DynamicEntity> dynamicRemoveBuffer = new ArrayList<DynamicEntity>();
	/**
	 * entities to remove on next frame (to avoid
	 * ConcurrentModificationException)
	 */
	public static ArrayList<Light> lightRemoveBuffer = new ArrayList<Light>();

	/**
	 * @return Whether or not there are any entities at the moment
	 */
	public static boolean entitiesExist() {
		return staticEntities.size() > 0 || dynamicEntities.size() > 0;
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
		if (!staticAddBuffer.isEmpty()) {
			Iterator<Entity> addIterator = staticAddBuffer.iterator();
			while (addIterator.hasNext()) {
				Entity ent = addIterator.next();
				staticEntities.add(ent);
				addIterator.remove();
			}
		}

		// remove any entities from the removeBuffer
		if (!staticRemoveBuffer.isEmpty()) {
			Iterator<Entity> removeIterator = staticRemoveBuffer.iterator();
			while (removeIterator.hasNext()) {
				Entity ent = removeIterator.next();
				staticEntities.remove(ent);
				removeIterator.remove();
			}
		}
		
		// add any Entities in the addBuffer
		if (!dynamicAddBuffer.isEmpty()) {
			Iterator<DynamicEntity> addIterator = dynamicAddBuffer.iterator();
			while (addIterator.hasNext()) {
				DynamicEntity ent = addIterator.next();
				dynamicEntities.add(ent);
				addIterator.remove();
			}
		}

		// remove any entities from the removeBuffer
		if (!dynamicRemoveBuffer.isEmpty()) {
			Iterator<DynamicEntity> removeIterator = dynamicRemoveBuffer.iterator();
			while (removeIterator.hasNext()) {
				DynamicEntity ent = removeIterator.next();
				dynamicEntities.remove(ent);
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
		for (Entity ent : staticEntities) {
			ent.cleanup();
		}
		
		for(DynamicEntity ent : dynamicEntities){
			ent.cleanup();
		}
		
		player = null;
		camera = null;
		staticEntities.clear();
		dynamicEntities.clear();
		lights.clear();
		dynamicAddBuffer.clear();
		dynamicRemoveBuffer.clear();
		staticAddBuffer.clear();
		staticRemoveBuffer.clear();
		lightAddBuffer.clear();
		lightRemoveBuffer.clear();
	}
}
