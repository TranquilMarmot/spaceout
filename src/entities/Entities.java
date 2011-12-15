package entities;

import java.util.ArrayList;
import java.util.Iterator;

import entities.dynamic.DynamicEntity;
import entities.dynamic.Player;


/**
 * Handles all the current entities
 * @author TranquilMarmot
 *
 */
public class Entities {
	/** player instance */
	public static Player player;
	/** camera instance */
	public static Camera camera;
	
	/** all the current entities */
	public static ArrayList<Entity> entities = new ArrayList<Entity>();
	
	/** all the current lights */
	public static ArrayList<Light> lights = new ArrayList<Light>();
	
	/** entities to add on next frame (to avoid ConcurrentModificationException) */
	public static ArrayList<Entity> addBuffer = new ArrayList<Entity>();
	
	/** entities to add on next frame (to avoid ConcurrentModificationException) */
	public static ArrayList<Entity> removeBuffer = new ArrayList<Entity>();
	
	/**
	 * @return Whether or not there are any entities at the moment
	 */
	public static boolean entitiesExist(){
		return entities.size() > 0;
	}
	
	/**
	 * Gets the distance between two entities
	 * @param first The first entity
	 * @param second The second entity
	 * @return The distance between the two entities
	 */
	public static float distance(Entity first, Entity second){
		float xDist = first.location.x - second.location.x;
		float yDist = first.location.y - second.location.y;
		float zDist = first.location.z - second.location.z;
		
		float xSqr = xDist * xDist;
		float ySqr = yDist * yDist;
		float zSqr = zDist * zDist;
		
		double total = (double)(xSqr + ySqr + zSqr);
		
		return (float)Math.sqrt(total);
	}
	
	public static void checkBuffers(){
		// add any Entities in the addBuffer
		if (!addBuffer.isEmpty()) {
			Iterator<Entity> addIterator = addBuffer.iterator();
			while (addIterator.hasNext()) {
				Entity ent = addIterator.next();
				entities.add(ent);
				addIterator.remove();
			}
		}

		// remove any entities from the removeBuffer
		if (!removeBuffer.isEmpty()) {
			Iterator<Entity> removeIterator = removeBuffer.iterator();
			while (removeIterator.hasNext()) {
				Entity ent = removeIterator.next();
				entities.remove(ent);
				removeIterator.remove();
			}
		}
	}
	
	public static void cleanup(){
		for(Entity ent : entities){
			ent.cleanup();
		}
		player = null;
		camera = null;
		entities.clear();
		lights.clear();
		addBuffer.clear();
		removeBuffer.clear();
	}
}
