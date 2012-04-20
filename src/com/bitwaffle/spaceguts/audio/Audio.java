package com.bitwaffle.spaceguts.audio;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.util.QuaternionHelper;

public class Audio {
	static FloatBuffer listenerPos, listenerVel, listenerOrient;
	
	public static void init(){
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		AL10.alGetError();
		
		listenerPos = BufferUtils.createFloatBuffer(3);
		listenerVel = BufferUtils.createFloatBuffer(3);
		listenerOrient = BufferUtils.createFloatBuffer(6);
	}
	
	public static void update(){
		listenerPos.clear();
		listenerPos.put(Entities.camera.location.x);
		listenerPos.put(Entities.camera.location.y);
		listenerPos.put(Entities.camera.location.z);
		listenerPos.rewind();
		AL10.alListener(AL10.AL_POSITION, listenerPos);
		
		javax.vecmath.Vector3f linvec = new javax.vecmath.Vector3f();
		Entities.camera.rigidBody.getLinearVelocity(linvec);
		listenerVel.clear();
		listenerVel.put(linvec.x);
		listenerVel.put(linvec.y);
		listenerVel.put(linvec.z);
		listenerVel.rewind();
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		
		Vector3f at = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		at = QuaternionHelper.rotateVectorByQuaternion(at, Entities.camera.rotation);
		up = QuaternionHelper.rotateVectorByQuaternion(up, Entities.camera.rotation);
		listenerOrient.clear();
		listenerOrient.put(at.x);
		listenerOrient.put(at.y);
		listenerOrient.put(at.z);
		listenerOrient.put(up.x);
		listenerOrient.put(up.y);
		listenerOrient.put(up.z);
		listenerOrient.rewind();
		AL10.alListener(AL10.AL_ORIENTATION, listenerOrient);
	}
	
	public static void shutdown(){
		AL.destroy();
	}
}
