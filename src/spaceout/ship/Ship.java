package spaceout.ship;

import org.lwjgl.util.vector.Vector3f;

import spaceguts.graphics.model.Model;

/*
 * TODO
 * Each ship should be stored in its own folder. The ship's name will come from the name of the folder,
 * The folder will contain:
 * -An obj file for describing the ship's geometry
 * -A png file for the ship's texture
 * -An XML file containing all the info about the ship (health, mass, acceleration, and all the other vairbales)
 * 
 * So whenever a ship is needed, it gets loaded in from its XML file
 */
public abstract class Ship{
	/** The model to use for drawing the ship */
	public Model model;
	/** The name of the ship */
	public String name;
	/** How much health the ship has */
	public int health;
	/** Info to use for physics simulation*/
	public float mass, restitution;
	/** How fast the ship accelerates along each axis */
	public Vector3f accelerationSpeed;
	/** How fast the ship can go */
	public float topSpeed;
	/** How fast the ship can staiblize/stop */
	public float stabilizationSpeed, stopSpeed;
	/** How fast the ship can turn */ //TODO these should be controlled by the ship's thrusters
	public float rollSpeed, xTurnSpeed, yTurnSpeed;
	
	public Ship(String name, Model model, int health, float mass, float restitution, Vector3f accelerationSpeed, float topSpeed, float stabilizationSpeed, float stopSpeed, float rollSpeed, float xTurnSpeed, float yTurnSpeed){
		this.name = name;
		this.model = model;
		this.health = health;
		this.mass = mass;
		this.restitution = restitution;
		this.accelerationSpeed = accelerationSpeed;
		this.topSpeed = topSpeed;
		this.stabilizationSpeed = stabilizationSpeed;
		this.stopSpeed = stopSpeed;
		this.rollSpeed = rollSpeed;
		this.xTurnSpeed = xTurnSpeed;
		this.yTurnSpeed = yTurnSpeed;
	}
}
