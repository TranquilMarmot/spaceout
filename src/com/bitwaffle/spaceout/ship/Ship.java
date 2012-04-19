package com.bitwaffle.spaceout.ship;

import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceout.resources.Models;


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
/**
 * A class describing the different variables for a ship.
 * @author TranquilMarmot
 *
 */
public class Ship{
	/** The model to use for drawing the ship */
	private Models model;
	/** The name of the ship */
	private String name;
	/** How much health the ship has */
	private int health;
	/** Info to use for physics simulation*/
	private float mass, restitution;
	/** How fast the ship accelerates along each axis */
	private Vector3f accelerationSpeed;
	/** How fast the ship goes when boosting */
	private Vector3f boostSpeed;
	/** How fast the ship can go */
	private float topSpeed;
	/** How fast the ship can stop */
	private float stopSpeed;
	/** How fast the ship can turn */
	private float rollSpeed, turnSpeed;
	
	/**
	 * Create a ship object
	 * @param name The name of the ship
	 * @param model The model to use when drawing the ship
	 * @param health How much health the ship has
	 * @param mass How much the ship weighs
	 * @param restitution How bouncy the ship is
	 * @param accelerationSpeed How fast the ship can accelerate along each axis
	 * @param topSpeed Maximum speed for the ship
	 * @param stabilizationSpeed How fast the ship stabilizes
	 * @param stopSpeed How fast the ship stops
	 * @param rollSpeed How fast the ship rolls
	 * @param xTurnSpeed How fast the ship turns about its x axis
	 * @param yTurnSpeed How fast the ship turns about its y axis
	 */
	public Ship(String name, Models model, int health, float mass, float restitution, Vector3f accelerationSpeed, Vector3f boostSpeed, float topSpeed, float stopSpeed, float rollSpeed, float turnSpeed){
		this.name = name;
		this.model = model;
		this.health = health;
		this.mass = mass;
		this.restitution = restitution;
		this.accelerationSpeed = accelerationSpeed;
		this.boostSpeed = boostSpeed;
		this.topSpeed = topSpeed;
		this.stopSpeed = stopSpeed;
		this.rollSpeed = rollSpeed;
		this.turnSpeed = turnSpeed;
	}
	
	public Models getModel(){ return model; }
	public String getName(){ return name; }
	public int getHealth(){ return health; }
	public float getMass(){ return mass; }
	public float getRestitution(){ return restitution; }
	public Vector3f getAccelerationSpeed(){ return accelerationSpeed; }
	public Vector3f getBoostSpeed(){ return boostSpeed; }
	public float getTopSpeed(){ return topSpeed; }
	public float getStopSpeed(){ return stopSpeed; }
	public float getRollSpeed(){ return rollSpeed; }
	public float getTurnSpeed(){ return turnSpeed; }
}
