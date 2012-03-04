package com.bitwaffle.spaceguts.entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

/**
 * Class that pretty much anything in-game extends.
 * 
 * @author TranquilMarmot
 * 
 */
public abstract class Entity {
	/** the entity's current location */
	public Vector3f location;

	/** quaternion representing rotation */
	public Quaternion rotation;

	/** type, used for lots of things */
	public String type;

	/**
	 * Entity constructor
	 */
	public Entity() {
		location = new Vector3f(0.0f, 0.0f, 0.0f);
		rotation = new Quaternion(1.0f, 0.0f, 0.0f, 1.0f);
		type = "entity";
	}

	/**
	 * Updates this entity
	 */
	public abstract void update(float timeStep);

	/**
	 * Draws this entity
	 */
	public abstract void draw();

	/**
	 * Have the entity provide any necessary cleanup
	 */
	public abstract void cleanup();
}
