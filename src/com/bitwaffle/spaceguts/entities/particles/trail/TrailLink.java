package com.bitwaffle.spaceguts.entities.particles.trail;

import org.lwjgl.util.vector.Vector3f;

public class TrailLink {
	public Vector3f top, bottom;
	
	public TrailLink(Vector3f top, Vector3f bottom){
		this.top = top;
		this.bottom = bottom;
	}
}
