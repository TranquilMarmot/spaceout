package com.bitwaffle.spaceguts.audio;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceout.resources.Sounds;

public class SoundSource {
	private Sounds sound;
	
	private FloatBuffer posBuf, velBuf;
	
	private int sourceHandle;
	
	public SoundSource(Sounds sound, Vector3f location, Vector3f velocity){
		this.sound = sound;
		
		posBuf = BufferUtils.createFloatBuffer(3);
		velBuf = BufferUtils.createFloatBuffer(3);
		
		setLocation(location);
		setVelocity(velocity);
		
		IntBuffer buf = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(buf);
		sourceHandle = buf.get(0);
		
		initBuffers();
	}
	
	public void initBuffers(){
		AL10.alSourcei(sourceHandle, AL10.AL_BUFFER,   sound.getHandle());
		AL10.alSourcef(sourceHandle, AL10.AL_PITCH,    sound.pitch);
		AL10.alSourcef(sourceHandle, AL10.AL_GAIN,     sound.gain);
		AL10.alSource (sourceHandle, AL10.AL_POSITION, posBuf);
		AL10.alSource (sourceHandle, AL10.AL_VELOCITY, velBuf);
	}
	
	public void playSound(){
		AL10.alSourcePlay(sourceHandle);
	}
	
	public void setLocation(Vector3f location){
		posBuf.clear();
		posBuf.put(location.x);
		posBuf.put(location.y);
		posBuf.put(location.z);
		posBuf.rewind();
		AL10.alSource (sourceHandle, AL10.AL_POSITION, posBuf);
	}
	
	public void setVelocity(Vector3f velocity){
		velBuf.clear();
		velBuf.put(velocity.x);
		velBuf.put(velocity.y);
		velBuf.put(velocity.z);
		velBuf.rewind();
		AL10.alSource (sourceHandle, AL10.AL_VELOCITY, velBuf);
	}
	
	public void shutdown(){
		AL10.alDeleteSources(sourceHandle);
	}
}
