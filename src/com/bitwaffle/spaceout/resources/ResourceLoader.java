package com.bitwaffle.spaceout.resources;

import java.util.Stack;


/**
 * Allows multiple resources to be loaded at the same time.
 * If the resources are loaded while the game is running, it causes a pause in the game.
 * If this is used, resources can be initialized before they need to be used.
 * 
 * @author TranquilMarmot
 * 
 */
public class ResourceLoader {
	private static Stack<Textures> texturesToLoad = new Stack<Textures>();
	private static Stack<Models> modelsToLoad = new Stack<Models>();
	private static Stack<Sounds> soundsToLoad = new Stack<Sounds>();
	
	/**
	 * Add a texture to be initialized
	 * @param texture Texture to initialize on next call to processJobs()
	 */
	public static void addJob(Textures texture){
		texturesToLoad.push(texture);
	}
	
	/**
	 * Add a model to be initialized
	 * @param model Model to initialize on next call to processJobs()
	 */
	public static void addJob(Models model){
		modelsToLoad.push(model);
	}
	
	/**
	 * Add a sound to be initialized
	 * @param sound Sound to initialize on next call to processJobs()
	 */
	public static void addJob(Sounds sound){
		soundsToLoad.push(sound);
	}

	/**
	 * Loads all jobs added with addJob() methods
	 */
	public static void processJobs() {
		Textures t = null;
		while(!texturesToLoad.isEmpty()){
			t = texturesToLoad.pop();
			t.initTexture();
		}
		
		Models m = null;
		while(!modelsToLoad.isEmpty()){
			m = modelsToLoad.pop();
			m.initModel();
		}
		
		Sounds s = null;
		while(!soundsToLoad.isEmpty()){
			s = soundsToLoad.pop();
			s.initSound();
		}
		
		// try and force garbage collection to get rid of any spare data left over from loading things
		System.gc();
	}
	
	/**
	 * @return Whether or not there are any queued jobs
	 */
	public static boolean jobsExist(){
		return !texturesToLoad.isEmpty() || !modelsToLoad.isEmpty() || !soundsToLoad.isEmpty();
	}
}