package entities.dynamic;

import graphics.model.Model;

public abstract class Ship {
	public Model model;
	public float mass, restitution;
	public float xAccel, yAccel, zAccel;
	public float maxX, maxY, maxZ;
	public float stabilizationSpeed, stopSpeed;
	public float rollSpeed, xTurnSpeed, yTurnSpeed;
	
}
