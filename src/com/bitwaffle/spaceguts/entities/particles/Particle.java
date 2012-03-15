package com.bitwaffle.spaceguts.entities.particles;

import org.lwjgl.util.vector.Vector3f;

public class Particle{
	public Vector3f location;
	public float width, height;
	
	public Particle(Vector3f location, float width, float height){
		this.location = location;
		this.width = width;
		this.height = height;
	}

	public void update(float timeStep) {
	}
}
