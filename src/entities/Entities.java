package entities;

import java.util.ArrayList;

import entities.player.Player;

public class Entities {
	public static Player player;
	public static Camera camera;
	public static ArrayList<Entity> entities = new ArrayList<Entity>();
	public static ArrayList<Light> lights = new ArrayList<Light>();
	public static ArrayList<Entity> addBuffer = new ArrayList<Entity>();
	public static ArrayList<Entity> removeBuffer = new ArrayList<Entity>();
	
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
}
