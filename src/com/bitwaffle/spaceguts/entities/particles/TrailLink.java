package com.bitwaffle.spaceguts.entities.particles;

import org.lwjgl.util.vector.Vector3f;

public class TrailLink {
	public Vector3f start, end;
	
	public float width, height;
	
	public TrailLink(Vector3f start, Vector3f end, float width, float height){
		this.start = start;
		this.end = end;
		this.width = width;
		this.height = height;
	}
}
