package com.bitwaffle.spaceout.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

public enum Sounds {
	DING(Paths.SOUND_PATH.path() + "ding.wav", 1.0f, 1.0f);
	
	private String file;
	
	private int bufferHandle, sourceHandle;
	
	public float pitch, gain;
	
	private Sounds(String file, float pitch, float gain){
		this.pitch = pitch;
		this.gain = gain;
		this.file = file;
		
		IntBuffer buf = BufferUtils.createIntBuffer(1);
		AL10.alGenBuffers(buf);
		bufferHandle = buf.get(0);
		
		IntBuffer buf2 = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(buf2);
		sourceHandle = buf.get(0);
		
		initSound();
	}
	
	private void initSound(){
		try{
			FileInputStream in = new FileInputStream(file);
			
			WaveData waveFile = WaveData.create(in);
			
			in.close();
			
			waveFile.dispose();
			
			AL10.alBufferData(bufferHandle, waveFile.format, waveFile.data, waveFile.samplerate);
			
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void playSoundAt(Vector3f location, Vector3f velocity){
		
	}
}
