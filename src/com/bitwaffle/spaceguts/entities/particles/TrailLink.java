package com.bitwaffle.spaceguts.entities.particles;

import org.lwjgl.util.vector.Vector3f;

public class TrailLink {
	public Vector3f start1, start2, end1, end2;
	
	public float width, height;
	
	public TrailLink(Vector3f start1, Vector3f start2, Vector3f end1, Vector3f end2, float width, float height){
		this.start1 = start1;
		this.start2 = start2;
		this.end1 = end1;
		this.end2 = end2;
		this.width = width;
		this.height = height;
	}
}
