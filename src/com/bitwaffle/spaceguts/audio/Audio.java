package com.bitwaffle.spaceguts.audio;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.util.QuaternionHelper;

/**
 * Manages intiializing OpenAL and upating the listener location to be at the camera's location
 * @author TranquilMarmot
 *
 */
public class Audio {
	/** Used for deleting all sound sources on shutdown (see {@link SoundSource}'s constructor, each SoundSource gets added to this when it's created */
	protected static ArrayList<SoundSource> soundSources = new ArrayList<SoundSource>();
	
	/** Buffers for transferring data to OpenAL */
	private static FloatBuffer listenerPos, listenerVel, listenerOrient;
	
	/** For checking for errors */
	private static int err;
	
	/** Factor to use for doppler effect */
	private static final float DOPPLER_FACTOR = 0.0f;
	
	/** Velocity to use for doppler effect*/
	private static final float DOPPLER_VELOCITY = 1.0f;
	
	/** Whether or not the game is muted */
	private static boolean muted = false;
	
	/** Current volume */
	private static float volume = 1.0f;
	
	
	/**
	 * Initializes OpenAL
	 */
	public static void init(){
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		// zero out error
		AL10.alGetError();
		
		// set doppler values
		AL10.alDopplerFactor(DOPPLER_FACTOR);
		AL10.alDopplerVelocity(DOPPLER_VELOCITY);
		
		// initialize buffers
		listenerPos = BufferUtils.createFloatBuffer(3);
		listenerVel = BufferUtils.createFloatBuffer(3);
		listenerOrient = BufferUtils.createFloatBuffer(6);
	}
	
	/**
	 * Updates the listener's position, velocity and orientation to match the camera
	 */
	public static void update(){
		// position
		listenerPos.clear();
		
		listenerPos.put(Entities.camera.location.x);
		listenerPos.put(Entities.camera.location.y);
		listenerPos.put(Entities.camera.location.z);
		listenerPos.rewind();
		AL10.alListener(AL10.AL_POSITION, listenerPos);
		
		// velocity
		javax.vecmath.Vector3f linvec = new javax.vecmath.Vector3f();
		
		// if we're following anything, we want its velocity
		if(Entities.camera.buildMode || Entities.camera.freeMode){
			Entities.camera.rigidBody.getLinearVelocity(linvec);
		} else{
			Entities.camera.following.rigidBody.getLinearVelocity(linvec);
		}
		
		listenerVel.clear();
		listenerVel.put(linvec.x);
		listenerVel.put(linvec.y);
		listenerVel.put(linvec.z);
		listenerVel.rewind();
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		
		// orientation
		Vector3f at = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Quaternion cameraRev = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Entities.camera.rotation.negate(cameraRev);
		at = QuaternionHelper.rotateVectorByQuaternion(at, cameraRev);
		up = QuaternionHelper.rotateVectorByQuaternion(up, cameraRev);
		listenerOrient.clear();
		listenerOrient.put(at.x);
		listenerOrient.put(at.y);
		listenerOrient.put(at.z);
		listenerOrient.put(up.x);
		listenerOrient.put(up.y);
		listenerOrient.put(up.z);
		listenerOrient.rewind();
		AL10.alListener(AL10.AL_ORIENTATION, listenerOrient);
		
		// check for errors
		err = AL10.alGetError();
		if(err != AL10.AL_NO_ERROR){
			System.out.println("Error in OpenAL! number: " + err + " string: " + AL10.alGetString(err));
		}
		
		// get rid of any straggling sound sources
		ArrayList<SoundSource> toRemove = new ArrayList<SoundSource>();
		for(SoundSource src : soundSources){
			if(src.removeFlag){
				// only remove something if it's not playing anything
				if(!src.isPlaying()){
					src.shutdown();
					toRemove.add(src);
				}
			}
		}
		for(SoundSource src : toRemove)
			soundSources.remove(src);
	}
	
	/**
	 * Changes the volume of everything
	 * @param gain New volume level (0 = none, 0.5 = 50%, 1 = 100%, 2 = 200% etc.)
	 */
	public static void setVolume(float gain){
		volume = gain;
		
		for(SoundSource src : soundSources)
			src.setGain(volume);
	}
	
	/**
	 * @return Current volume level
	 */
	public static float currentVolume(){
		return volume;
	}
	
	/**
	 * Pauses all sounds
	 */
	public static void pause(){
		for(SoundSource src : soundSources)
			src.pauseSound();
	}
	
	/**
	 * Plays all sounds
	 */
	public static void play(){
		for(SoundSource src : soundSources)
			if(!src.isPlaying())
				src.playSound();
	}
	
	/**
	 * Mutes/un-mutes audio
	 */
	public static void mute(){
		muted = !muted;
		
		for(SoundSource src : soundSources)
			src.setGain(muted ? 0.0f : volume);
	}
	
	/**
	 * @return Whether or not audio is muted
	 */
	public static boolean isMuted(){
		return muted;
	}
	
	/**
	 * Shuts down OpenAL
	 */
	public static void shutdown(){
		// get rid of all sound sources
		for(SoundSource src : soundSources)
			src.shutdown();
		AL.destroy();
	}
}
