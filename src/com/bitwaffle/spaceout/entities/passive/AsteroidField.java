package com.bitwaffle.spaceout.entities.passive;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.entities.dynamic.Asteroid;
import com.bulletphysics.linearmath.Transform;

public class AsteroidField extends Entity{
	Random randy;
	private ArrayList<Asteroid> asteroids;
	private int numAsteroids;
	private float releaseInterval, lastRelease = 0.0f;
	private float minSize, maxSize;
	private Vector3f range, asteroidSpeed;
	int i = 0;
	
	public AsteroidField(Vector3f location, Vector3f range, Vector3f asteroidSpeed, int numAsteroids, int initialAsteroids, float releaseInterval, float minSize, float maxSize){
		this.numAsteroids = numAsteroids;
		asteroids = new ArrayList<Asteroid>(numAsteroids);
		
		this.releaseInterval = releaseInterval;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.location = location;
		this.range = range;
		this.asteroidSpeed = asteroidSpeed;
		
		randy = new Random();
		randy.setSeed(System.currentTimeMillis());
		
		for(int i = 0; i < initialAsteroids; i++)
			releaseAsteroid();
	}
	
	public void releaseAsteroid(){
		float asteroidX = randy.nextBoolean() ?
				randy.nextFloat() * range.x :
				randy.nextFloat() * -range.x;
				
		float asteroidY = randy.nextBoolean() ?
				randy.nextFloat() * range.y :
				randy.nextFloat() * -range.y;
				
		float asteroidZ = randy.nextBoolean() ?
				randy.nextFloat() * range.z :
				randy.nextFloat() * -range.z;
				
		float asteroidSize = minSize + (randy.nextFloat() * ((maxSize - minSize) + 1));
		
		Quaternion asteroidRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		QuaternionHelper.rotate(asteroidRotation, new Vector3f(randy.nextInt(90), randy.nextInt(90), randy.nextInt(90)));
		
		Asteroid a = new Asteroid(new Vector3f(asteroidX, asteroidY, asteroidZ), asteroidRotation, asteroidSize);
		
		float impulseX = randy.nextBoolean() ?
				randy.nextFloat() * asteroidSpeed.x:
				randy.nextFloat() * asteroidSpeed.x;
				
		float impulseY = randy.nextBoolean() ?
				randy.nextFloat() * asteroidSpeed.y:
				randy.nextFloat() * -asteroidSpeed.y;
				
		float impulseZ = randy.nextBoolean() ?
				randy.nextFloat() * asteroidSpeed.z:
				randy.nextFloat() * -asteroidSpeed.z;
		
		a.rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(impulseX, impulseY, impulseZ));
		
		Entities.addDynamicEntity(a);
		asteroids.add(a);
	}

	@Override
	public void update(float timeStep) {
		lastRelease += timeStep;
		
		if(asteroids.size() <= numAsteroids && lastRelease >= releaseInterval){
			releaseAsteroid();
			lastRelease = 0.0f;
		}
		
		// FIXME this stack causes the game to freeze?
		//Stack<Asteroid> toRemove = new Stack<Asteroid>();
		for(Asteroid a : asteroids){
			a.update(timeStep);
			if(a.removeFlag){
				//toRemove.push(a);
			} else{
				Transform trans = new Transform();
				a.rigidBody.getWorldTransform(trans);
				
				if(a.location.x > this.location.x + (range.x * 2.0f))
					trans.origin.x = this.location.x - (range.x * 2.0f);
				else if(a.location.x < this.location.x - (range.x * 2.0f))
					trans.origin.x = this.location.x + (range.x * 2.0f);
				
				if(a.location.y > this.location.y + (range.y * 2.0f))
					trans.origin.y = this.location.y - (range.y * 2.0f);
				else if(a.location.y < this.location.y - (range.y * 2.0f))
					trans.origin.y = this.location.y + (range.y * 2.0f);
				
				if(a.location.z > this.location.z + (range.z * 2.0f))
					trans.origin.z = this.location.z - (range.z * 2.0f);
				else if(a.location.z < this.location.z - (range.z * 2.0f))
					trans.origin.z = this.location.z + (range.z * 2.0f);
				
				a.rigidBody.setWorldTransform(trans);
			}
		}
	
		//while(!toRemove.isEmpty())
		//	asteroids.remove(toRemove.pop());
	}

	@Override public void draw() {}
	@Override public void cleanup() {}
}
