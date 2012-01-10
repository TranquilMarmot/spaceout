package spaceout.ship;

import org.lwjgl.util.vector.Vector3f;

import spaceguts.graphics.model.Model;

public abstract class Ship{
	public Model model;
	public String name;
	public float mass, restitution;
	public Vector3f acceleration;
	public float topSpeed;
	public float stabilizationSpeed, stopSpeed;
	public float rollSpeed, xTurnSpeed, yTurnSpeed;
}
