package com.bitwaffle.spaceguts.audio;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.util.QuaternionHelper;

public class Audio {
	static IntBuffer source;
	
	public static void init(){
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		// buffers hold sound data
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		
		// sources are points emitting sound
		source = BufferUtils.createIntBuffer(1);
		
		/** Position of the source sound. */
		FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

		/** Velocity of the source sound. */
		FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
		
		
		
		
		FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(new float[] { Entities.camera.location.x, Entities.camera.location.y, Entities.camera.location.z });

		javax.vecmath.Vector3f linvec = new javax.vecmath.Vector3f();
		Entities.camera.rigidBody.getLinearVelocity(linvec);
		FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(new float[] { linvec.x, linvec.y, linvec.z });
		
		sourcePos.rewind();
		sourceVel.rewind();
		listenerPos.rewind();
		listenerVel.rewind();
		
		Vector3f at = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		at = QuaternionHelper.rotateVectorByQuaternion(at, Entities.camera.rotation);
		up = QuaternionHelper.rotateVectorByQuaternion(up, Entities.camera.rotation);
		FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(new float[] {
				at.x, at.y, at.z,  up.x, up.y, up.z
		});
		
		listenerOri.rewind();
		
		
		AL10.alGenBuffers(buffer);
		//if(AL10.alGetError() != AL10.AL_NO_ERROR)
		//	return AL10.AL_FALSE;
		
	    //Loads the wave file from your file system
	    java.io.FileInputStream fin = null;
	    try {
	      fin = new java.io.FileInputStream("res/sounds/ding.wav");
	    } catch (java.io.FileNotFoundException ex) {
	      ex.printStackTrace();
	      //return AL10.AL_FALSE;
	    }
	    WaveData waveFile = WaveData.create(fin);
	    try {
	      fin.close();
	    } catch (java.io.IOException ex) {
	    }
		
		//WaveData waveFile = WaveData.create("res/sounds/ding.wav");
		AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		
		AL10.alGenSources(source);
		//if(AL10.alGetError() != AL10.AL_NO_ERROR)
		//	return AL10.AL_FALSE;
		
		AL10.alSourcei(source.get(0), AL10.AL_BUFFER,   buffer.get(0) );
		AL10.alSourcef(source.get(0), AL10.AL_PITCH,    1.0f          );
		AL10.alSourcef(source.get(0), AL10.AL_GAIN,     1.0f          );
		AL10.alSource (source.get(0), AL10.AL_POSITION, sourcePos     );
		AL10.alSource (source.get(0), AL10.AL_VELOCITY, sourceVel     );
		//if(AL10.alGetError() != AL10.AL_NO_ERROR)
		//	return AL10.AL_FALSE;
		
		AL10.alListener(AL10.AL_POSITION,    listenerPos);
		AL10.alListener(AL10.AL_VELOCITY,    listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
		//if(AL10.alGetError() != AL10.AL_NO_ERROR)
		//	return AL10.AL_FALSE;
		
	}
	
	/*
	public static void shutdown(){
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}
	*/
	
	public static void update(){
		AL10.alSourcePlay(source.get(0));
	}
}
