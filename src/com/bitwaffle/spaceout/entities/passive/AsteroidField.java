package com.bitwaffle.spaceout.entities.passive;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.entities.dynamic.Asteroid;

public class AsteroidField extends Entity{
	Random randy;
	private ArrayList<Asteroid> asteroids;
	private Stack<Asteroid> asteroidsToAdd;
	private Stack<Asteroid> asteroidsToRemove;
	private int numAsteroids;
	private float releaseInterval, lastRelease = 0.0f;
	private float minSize, maxSize;
	private Vector3f range, asteroidSpeed;
	int i = 0;
	
	public AsteroidField(Vector3f location, Vector3f range, Vector3f asteroidSpeed, int numAsteroids, int initialAsteroids, float releaseInterval, float minSize, float maxSize){
		this.numAsteroids = numAsteroids;
		asteroids = new ArrayList<Asteroid>(numAsteroids);
		asteroidsToAdd = new Stack<Asteroid>();
		asteroidsToRemove = new Stack<Asteroid>();
		
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
				randy.nextFloat() * -asteroidSpeed.x;
				
		float impulseY = randy.nextBoolean() ?
				randy.nextFloat() * asteroidSpeed.y:
				randy.nextFloat() * -asteroidSpeed.y;
				
		float impulseZ = randy.nextBoolean() ?
				randy.nextFloat() * asteroidSpeed.z:
				randy.nextFloat() * -asteroidSpeed.z;
		
		a.rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(impulseX, impulseY, impulseZ));
		
		Entities.addDynamicEntity(a);
		asteroidsToAdd.push(a);
		
		System.out.printf(":%d size %f at %f,%f,%f\n", i++, asteroidSize, asteroidX, asteroidY, asteroidZ);
	}

	@Override
	public void update(float timeStep) {
		lastRelease += timeStep;
		
		if(asteroids.size() <= numAsteroids && lastRelease >= releaseInterval){
			releaseAsteroid();
			lastRelease = 0.0f;
		}
		
		for(Asteroid a : asteroids){
			if(a.removeFlag){
				asteroidsToRemove.push(a);
			}
		}
		
		while(!asteroidsToAdd.isEmpty()){
			Asteroid add = asteroidsToAdd.pop();
			asteroids.add(add);
		}
		
		while(!asteroidsToRemove.isEmpty()){
			Asteroid remove = asteroidsToRemove.pop();
			asteroids.remove(remove);
		}
	}

	@Override public void draw() {}
	@Override public void cleanup() {}
}
