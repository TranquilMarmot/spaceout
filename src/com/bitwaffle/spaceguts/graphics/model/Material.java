package com.bitwaffle.spaceguts.graphics.model;

import org.lwjgl.util.vector.Vector3f;

/**
 * Describes a material to use with the Phong reflection model
 * @author TranquilMarmot
 *
 */
public class Material {
	/** Ambient, diffuse, and specular colors (all between 0.0 and 1.0) */
	private Vector3f Ka, Kd, Ks;
	/** Shininess of the material */
	private float Shininess;
	
	/**
	 * 
	 * @param Ka Ambient color
	 * @param Kd Diffuse color
	 * @param Ks Specular color
	 * @param Shininess How shiny material is 
	 */
	public Material(Vector3f Ka, Vector3f Kd, Vector3f Ks, float Shininess){
		this.Ka = Ka;
		this.Kd= Kd;
		this.Ks = Ks;
		this.Shininess = Shininess;
	}
	
	public Vector3f getKa(){ return Ka; }
	public Vector3f getKs(){ return Ks; }
	public Vector3f getKd(){ return Kd; }
	public float getShininess(){ return Shininess; }
}
