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
	DING(Paths.SOUND_PATH.path() + "ding.wav", 1.0f, 1.0f),
	SPLODE(Paths.SOUND_PATH.path() + "splodion.wav", 1.0f, 1.0f),
	THRUSTER(Paths.SOUND_PATH.path() + "thruster.wav", 1.0f, 1.0f);
	
	
	/** Buffer to use for OpenAL stuff */
	private int bufferHandle;
	
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
		
		//FIXME this gain has no effect (it'll either be 1 or 0 right now- should be multiplied by current volume if not muted)
		this.gain = gain;
		
		// generate buffer handle
		IntBuffer buf = BufferUtils.createIntBuffer(1);
		AL10.alGenBuffers(buf);
		bufferHandle = buf.get(0);
		
		// initialize sound gata
		initSound(file);
	}
	
	/**
	 * @return Handle to use for sound
	 */
	public int getHandle(){
		return bufferHandle;
	}
	
	/**
	 * Initializes a sound
	 * @param file Location of sound file
	 */
	private void initSound(String file){
		try{
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
	
	/**
	 * Deletes sound from memory (NOTE: Probably shouldn't be called during runtime!)
	 */
	public void shutdown(){
		AL10.alDeleteBuffers(bufferHandle);
	}
}
