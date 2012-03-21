package com.bitwaffle.spaceguts.entities.particles.trail;

import org.lwjgl.util.vector.Vector3f;

/**
 * Just a link in the chain
 * Each link keeps track of its top and bottom vectors,
 * and the whole chain is rendered as a quad strip.
 * 
 * @author TranquilMarmot
 */
public class TrailLink {
	/** top and bottom of the link - these define how wide it is/how it's rotated */
	public Vector3f top, bottom;
	
	/**
	 * Create a trail link
	 * @param top Top of the link
	 * @param bottom Bottom of the link
	 */
	public TrailLink(Vector3f top, Vector3f bottom){
		this.top = top;
		this.bottom = bottom;
	}
}
