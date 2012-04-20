package com.bitwaffle.spaceguts.audio;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceout.resources.Sounds;

/**
 * Plays a sound from {@link Sounds} at a given location and velocity 
 * @author TranquilMarmot
 *
 */
public class SoundSource {
	/** Buffers for transferring data */
	private FloatBuffer posBuf, velBuf;
	
	/** Handle to use for source */
	private int handle;
	
	/**
	 * Create a new source for sound
	 * @param sound Sound to play
	 * @param loop Whether or not to loop sound
	 * @param location Initial location
	 * @param velocity Initial velocity
	 */
	public SoundSource(Sounds sound, boolean loop, Vector3f location, Vector3f velocity){
		// create buffers
		posBuf = BufferUtils.createFloatBuffer(3);
		velBuf = BufferUtils.createFloatBuffer(3);
		
		// generate handle
		IntBuffer buf = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(buf);
		handle = buf.get(0);
		
		// set location and speed
		setLocation(location);
		setVelocity(velocity);
		
		// set values
		AL10.alSourcei(handle, AL10.AL_BUFFER, sound.getHandle());
		AL10.alSourcef(handle, AL10.AL_PITCH, sound.pitch);
		AL10.alSourcef(handle, AL10.AL_GAIN, sound.gain);
		AL10.alSourcei(handle, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
		
		// all sound sources are added to this list so they can be deleted when the game exits
		Audio.soundSources.add(this);
	}
	
	/**
	 * Sets the pitch of the sound
	 * @param pitch New pitch
	 */
	public void setPitch(float pitch){
		AL10.alSourcef(handle, AL10.AL_PITCH, pitch);
	}
	
	/**
	 * Sets the gain of the sound
	 * @param gain New gain
	 */
	public void setGain(float gain){
		AL10.alSourcef(handle, AL10.AL_GAIN, gain);
	}
	
	/**
	 * Sets the location of the sound
	 * @param location New location
	 */
	public void setLocation(Vector3f location){
		posBuf.clear();
		posBuf.put(location.x);
		posBuf.put(location.y);
		posBuf.put(location.z);
		posBuf.rewind();
		AL10.alSource (handle, AL10.AL_POSITION, posBuf);
	}
	
	/**
	 * Sets the velocity of the sound
	 * @param velocity New velocity
	 */
	public void setVelocity(Vector3f velocity){
		velBuf.clear();
		velBuf.put(velocity.x);
		velBuf.put(velocity.y);
		velBuf.put(velocity.z);
		velBuf.rewind();
		AL10.alSource (handle, AL10.AL_VELOCITY, velBuf);
	}

	/**
	 * Plays the sound
	 */
	public void playSound(){
		AL10.alSourcePlay(handle);
	}
	
	/**
	 * Stops sound
	 */
	public void stopSound(){
		AL10.alSourceStop(handle);
	}
	
	/**
	 * Pauses sound
	 */
	public void pauseSound(){
		AL10.alSourcePause(handle);
	}
	
	public void rewindSound(){
		AL10.alSourceRewind(handle);
	}
	
	
	/**
	 * Queues a buffer
	 * @param buffer Buffer to queue
	 */
	public void queueBuffer(int buffer){
		AL10.alSourceQueueBuffers(handle, buffer);
	}
	
	/**
	 * Un-queues buffers
	 */
	public void unqueueBuffers(){
		AL10.alSourceUnqueueBuffers(handle);
	}
	
	/**
	 * Should be called when removing source
	 */
	public void shutdown(){
		AL10.alDeleteSources(handle);
	}
}
