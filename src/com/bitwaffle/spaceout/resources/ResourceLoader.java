package com.bitwaffle.spaceout.resources;

import java.util.LinkedList;


/**
 * Manages textures. This makes it so only one texture instance needs to be
 * around but multiple entities can use it.
 * 
 * @author TranquilMarmot
 * 
 */
public class ResourceLoader {
	private static LinkedList<Textures> texturesToLoad = new LinkedList<Textures>();
	private static LinkedList<Models> modelsToLoad = new LinkedList<Models>();
	
	public static void addJob(Textures texture){
		texturesToLoad.add(texture);
	}
	
	public static void addJob(Models model){
		modelsToLoad.add(model);
	}

	public static void processJobs() {
		Textures t = null;
		while(!texturesToLoad.isEmpty()){
			t = texturesToLoad.remove();
			t.initTexture();
		}
		
		Models m = null;
		while(!modelsToLoad.isEmpty()){
			m = modelsToLoad.remove();
			m.initModel();
		}
		
		// try and force garbage collection to get rid of any spare data left over from loading things
		System.gc();
	}
	
	public static boolean jobsExist(){
		return !texturesToLoad.isEmpty() || !modelsToLoad.isEmpty();
	}
}