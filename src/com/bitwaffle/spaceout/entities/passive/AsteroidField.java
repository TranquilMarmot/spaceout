package com.bitwaffle.spaceout.entities.passive;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.entities.dynamic.Asteroid;
import com.bitwaffle.spaceout.resources.Models;
import com.bitwaffle.spaceout.resources.Textures;
import com.bulletphysics.linearmath.Transform;

/**
 * Creates asteroids on a timer and keeps em all within a given box
 * @author TranquilMarmot
 */
public class AsteroidField extends Entity{
	/** Random number generator */
	private Random randy;
	
	/** List of all asteroids */
	private ArrayList<Asteroid> asteroids;
	
	/** Asteroids to add to field on next update (to avoid ConcurrentModificationException) */
	private Stack<Asteroid> toAdd;
	
	/** Number of maximum asteroids */
	private int numAsteroids;
	
	/** How fast the field creates asteroids */
	private float releaseInterval, lastRelease = 0.0f;
	
	/** Min/max size of created asteroids */
	private float minSize, maxSize;
	
	/** How far/fast the asteroids go when created */
	private Vector3f range, asteroidSpeed;
	
	/**
	 * Create an asteroid field
	 * @param location Location of field
	 * @param range How far the field reaches (if asteroids get this far away from the field's location, they loop around)
	 * @param asteroidSpeed How fast the created asteroids move
	 * @param numAsteroids Maximum number of asteroids
	 * @param initialAsteroids How many asteroids to create when this field is added
	 * @param releaseInterval How fast to release new asteroids
	 * @param minSize Minimum asteroid size
	 * @param maxSize Maximum asteroid size
	 */
	public AsteroidField(Vector3f location, Vector3f range, Vector3f asteroidSpeed, int numAsteroids, int initialAsteroids, float releaseInterval, float minSize, float maxSize){
		this.numAsteroids = numAsteroids;
		asteroids = new ArrayList<Asteroid>(numAsteroids);
		toAdd = new Stack<Asteroid>();
		
		this.releaseInterval = releaseInterval;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.location = location;
		this.rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.range = range;
		this.asteroidSpeed = asteroidSpeed;
		
		randy = new Random();
		randy.setSeed(System.currentTimeMillis());
		
		for(int i = 0; i < initialAsteroids; i++)
			releaseAsteroid();
	}
	
	/**
	 * Tells the field to create a new asteroid
	 */
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
		if(randy.nextInt(100) == 42){
			System.out.println("Super secret!");
			asteroidSize *= 10.0f;
		}
		
		Quaternion asteroidRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		QuaternionHelper.rotate(asteroidRotation, new Vector3f(randy.nextInt(90), randy.nextInt(90), randy.nextInt(90)));
		
		Asteroid a = new Asteroid(new Vector3f(asteroidX, asteroidY, asteroidZ), asteroidRotation, asteroidSize, this);
		
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
		//asteroids.add(a);
	}

	@Override
	/**
	 * This goes through and makes sure all the asteroids in the field are within the field's range
	 */
	public void update(float timeStep) {
		lastRelease += timeStep;
		
		// let an asteroid go if it's time
		if(asteroids.size() <= numAsteroids && lastRelease >= releaseInterval){
			releaseAsteroid();
			lastRelease = 0.0f;
		}
		
		// to keep track of asteroids to remove from the list, to avoid ConcurrentModificationException
		Stack<Asteroid> toRemove = new Stack<Asteroid>();
		for(Asteroid a : asteroids){
			// check if it's time to remove the asteroid from the field
			if(a.removeFlag){
				toRemove.push(a);
			} else{
				// loop asteroids around if they're out of bounds
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
	
		while(!toRemove.isEmpty())
			asteroids.remove(toRemove.pop());
		
		while(!toAdd.isEmpty())
			asteroids.add(toAdd.pop());
	}
	
	/**
	 * Add an asteroid to the field
	 * @param a Asteroid to add
	 */
	public void addAsteroidToField(Asteroid a){
		toAdd.push(a);
	}

	@Override 
	public void draw() {
		Render3D.program.setUniform("Light.LightEnabled", false);
		GL11.glEnable(GL11.GL_BLEND);
		Matrix4f oldModelview = new Matrix4f();
		oldModelview.load(Render3D.modelview);
		Render3D.modelview.scale(new Vector3f(range.x * 2, range.y * 2, range.z * 2));
		Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);
		Textures.TRANSPARENT_CHECKERS.texture().bind();
		Models.BOX.getModel().render();
		Render3D.modelview.load(oldModelview);
		GL11.glDisable(GL11.GL_BLEND);
		Render3D.program.setUniform("Light.LightEnabled", true);
	}
	@Override public void cleanup() {}
}
