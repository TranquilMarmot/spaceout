package com.bitwaffle.spaceout.resources;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public enum Sounds {
	DING(Paths.SOUND_PATH.path() + "ding.wav", 1.0f, 1.0f);
	
	private int bufferHandle;
	
	public float pitch, gain;
	
	private Sounds(String file, float pitch, float gain){
		this.pitch = pitch;
		this.gain = gain;
		
		IntBuffer buf = BufferUtils.createIntBuffer(1);
		AL10.alGenBuffers(buf);
		bufferHandle = buf.get(0);
		
		initSound(file);
	}
	
	public int getHandle(){
		return bufferHandle;
	}
	
	private void initSound(String file){
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			WaveData waveFile = WaveData.create(in);
			in.close();
			
			AL10.alBufferData(bufferHandle, waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void shutdown(){
		AL10.alDeleteBuffers(bufferHandle);
	}
}
