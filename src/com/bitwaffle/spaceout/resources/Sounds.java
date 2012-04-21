package com.bitwaffle.spaceout.resources;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

/**
 * This enum keeps track of all sound data, and also handles sending sound data to OpenAL
 * @author TranquilMarmot
 */
public enum Sounds {
	DIAMOND_PICKUP(Paths.SOUND_PATH.path() + "diamond-pickup.wav", 1.0f, 0.6f),
	SPLODE(Paths.SOUND_PATH.path() + "splosion.wav", 1.0f, 25.0f),
	THRUSTER(Paths.SOUND_PATH.path() + "thruster.wav", 1.0f, 50.0f),
	PEW(Paths.SOUND_PATH.path() + "pew.wav", 1.0f, 1.5f),
	
	SELECT(Paths.SOUND_PATH.path() + "select.wav", 1.0f, 1.0f),
	BACK(Paths.SOUND_PATH.path() + "back.wav", 1.0f, 1.0f),
	FRIENDLY_ALERT(Paths.SOUND_PATH.path() + "friendly-alert.wav", 1.0f, 1.0f);
	
	/** File to load sound from */
	private String file;
	
	/** Buffer to use for OpenAL stuff */
	private int bufferHandle = -1;
	
	/** Pitch and gain for sound */
	public float pitch, gain;
	
	/**
	 * Creates a new sound and sends its data to OpenAL
	 * @param file Location of sound
	 * @param pitch Pitch to use for sound
	 * @param gain Gain to use for sound
	 */
	private Sounds(String file, float pitch, float gain){
		this.pitch = pitch;
		this.gain = gain;
		this.file = file;
	}
	
	/**
	 * @return Handle to use for sound
	 */
	public int getHandle(){
		// make sure sound is initialized and initialize it if it's not
		if(bufferHandle == -1)
			initSound();
		return bufferHandle;
	}
	
	/**
	 * Initializes a sound
	 * @param file Location of sound file
	 */
	protected void initSound(){
		// in case this gets called more than once
		if(bufferHandle == -1){
			try{
				// generate buffer handle
				IntBuffer buf = BufferUtils.createIntBuffer(1);
				AL10.alGenBuffers(buf);
				bufferHandle = buf.get(0);
				
				// using a FileInputStream here causes a null pointer exception!
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
				WaveData waveFile = WaveData.create(in);
				in.close();
				
				AL10.alBufferData(bufferHandle, waveFile.format, waveFile.data, waveFile.samplerate);
				waveFile.dispose();
			} catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Deletes sound from memory (NOTE: Probably shouldn't be called during runtime!)
	 */
	public void shutdown(){
		AL10.alDeleteBuffers(bufferHandle);
	}
}
