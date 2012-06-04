package com.bitwaffle.spaceguts.graphics.model;

import java.util.HashMap;

/**
 * This would contain all the materials loaded in from a .mtl file.
 * The {@link ModelLoader} creates a MaterialList for every .obj file. 
 * @author TranquilMarmot
 * @see ModelLoader
 * @see Material
 * @see ModelBuilder
 *
 */
public class MaterialList {
	/** the actual list of materials*/
	private HashMap<String, Material> list;
	
	/**
	 * Constructor
	 */
	public MaterialList(){
		list = new HashMap<String, Material>();
	}
	
	/**
	 * Add a new material to the list
	 * @param name Name of the material
	 * @param mat The material itself
	 */
	public void addMaterial(String name, Material mat){
		/*
		 * HashMap oh HashMap
		 * Give you key and element
		 * Store them good for me
		 */
		list.put(name, mat);
	}
	
	/**
	 * Get a material by name
	 * @param name Name of material
	 * @return The material matching the given name
	 */
	public Material getMaterial(String name){
		// HashMap makes this easy!
		return list.get(name);
	}
	
	/**
	 * Returns a string with every material in the list
	 */
	public String toString(){
		String ret = "Material list:\n";
		for(String s : list.keySet()){
			Material mat = list.get(s);
			ret += "Material: " + s + "\n";
			ret += "Shininess: " + mat.getShininess() + "\n"; 
			ret += "Ka: " + mat.getKa().x + " " + mat.getKa().y + " " + mat.getKa().z + "\n";
			ret += "Kd: " + mat.getKd().x + " " + mat.getKd().y + " " + mat.getKd().z + "\n";
			ret += "Ks: " + mat.getKs().x + " " + mat.getKs().y + " " + mat.getKs().z + "\n";
			ret += "\n";
		}
		
		return ret;
	}
}
