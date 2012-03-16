package com.bitwaffle.spaceguts.entities.particles;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.graphics.shapes.Box2D;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.resources.Textures;

/**
 * A class for shooting {@link Particle}s out
 * @author TranquilMarmot
 */
public class Emitter{
	/** All the particles */
	private ArrayList<Particle> particles;
	
	/** Texture to use for drawing particle */
	private Textures particleTex;
	
	/** Used to preserve modelview */
	private static Matrix4f oldModelView = new Matrix4f();
	
	/** Box, for drawing particle */
	private static Box2D box = new Box2D(1.0f, 1.0f, Textures.PARTICLE.texture());
	
	/** The Entity that the particles are coming from */
	private DynamicEntity following;
	
	/** Offset from the center of the Entity being followed */
	private Vector3f offset;
	
	/** How much to vary the particle generation on each axis */
	private Vector3f locationVariance, velocityVariance;
	
	/** For generating random position offsets */
	private Random randy;
	
	/** Whether or not this emitter is emitting particles */
	public boolean active;
	
	/** Controls how fast the emitter emits */
	private float emitSpeed, timeSinceEmission;
	
	/** How many particles to emit per emission */
	private int particlesPerEmission;
	
	/**
	 * An object for emitting particles!
	 * @param following Entity to emit particles from
	 * @param particleTex Texture to use for Entities
	 * @param offset Offset from center of following entity
	 * @param emitSpeed How often to emit particles
	 * @param particlesPerEmission Particles to emit per emission
	 */
	public Emitter(DynamicEntity following, Textures particleTex, Vector3f offset, Vector3f locationVariance, Vector3f velocityVariance, float emitSpeed, int particlesPerEmission){
		this.following = following;
		this.particleTex = particleTex;
		this.offset = offset;
		this.locationVariance = locationVariance;
		this.velocityVariance = velocityVariance;
		this.emitSpeed = emitSpeed;
		this.particlesPerEmission = particlesPerEmission;
		particles = new ArrayList<Particle>();
		
		// seed with the time
		randy = new Random(System.nanoTime());
	}

	/**
	 * Updates all the Emitter's particles
	 * @param timeStep Amount of time since last update
	 */
	public void update(float timeStep) {
		// to avoid concurrentmodificationexception
		ArrayList<Particle> deleteList = new ArrayList<Particle>();
		
		// update particles and check for remove flag
		for(Particle p : particles){
			p.update(timeStep);
			if(p.removeFlag)
				deleteList.add(p);
		}
		
		// remove necessary particles
		if(!deleteList.isEmpty()){
			for(Particle p : deleteList)
				this.removeParticle(p);
		}
		
		// emit if the emitter is active
		if(active){
			// only emit if it's been enough time
			timeSinceEmission += timeStep;
			if(timeSinceEmission >= emitSpeed){
				// emit however many particles
				for(int i = 0; i < particlesPerEmission; i++)
					emitParticle();
				// reset counter
				timeSinceEmission = 0.0f;
			}
		}
	}
	
	/**
	 * @param p Particle to add to emitter
	 */
	public void addParticle(Particle p){
		particles.add(p);
	}
	
	/**
	 * @param p Particle to add to emitter
	 * @param index Index to add particle to
	 */
	public void addParticle(Particle p, int index){
		particles.add(index, p);
	}
	
	/**
	 * @param p Particles to add to emitter
	 */
	public void addParticles(ArrayList<Particle> p){
		particles.addAll(p);
	}
	
	/**
	 * @param p Remove particle from emitter
	 */
	public void removeParticle(Particle p){
		particles.remove(p);
	}
	
	/**
	 * @param index Index of particle to remove from emitter
	 */
	public void removeParticle(int index){
		particles.remove(index);
	}
	
	/**
	 * @param p Particles to remove
	 */
	public void removeParticles(ArrayList<Particle> p){
		particles.removeAll(p);
	}

	/**
	 * Draws all the particles associated with this Emitter
	 */
	public void draw() {
		// disable lighting
		Render3D.program.setUniform("Light.LightEnabled", false);
		
		// get the revese rotation of what we're following
		Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.following.rotation.negate(revQuat);
		
		// bind texture and array handle
		particleTex.texture().bind();
		GL30.glBindVertexArray(box.getVAOHandle());
		
		// draw all particles
		for(Particle p : particles){
			// amount to translate
			float transx = this.following.location.x - p.location.x;
			float transy = this.following.location.y - p.location.y;
			float transz = this.following.location.z - p.location.z;
			
			// save the modelview before we manipulate it
			oldModelView.load(Render3D.modelview);{
				Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(revQuat), Render3D.modelview);
				// translate and scale the modelview
				Render3D.modelview.translate(new Vector3f(transx, transy, transz));
				Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(Entities.camera.rotation), Render3D.modelview);
				Render3D.modelview.scale(new Vector3f(p.width, p.height, 1.0f));
				Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);

				// draw the particle
				GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
			}Render3D.modelview.load(oldModelView);
		}
		
		// re-enable lighting
		Render3D.program.setUniform("Light.LightEnabled", true);
	}
	
	/**
	 * Emit a single particle from this emitter
	 */
	public void emitParticle(){
		// offset from center of emitter
		float randXOffset, randYOffset, randZOffset;
		
		if(randy.nextBoolean()) 
			randXOffset = randy.nextFloat() * locationVariance.x;
		else
			randXOffset = randy.nextFloat() * -locationVariance.x;
		
		if(randy.nextBoolean()) 
			randYOffset = randy.nextFloat() * locationVariance.y;
		else
			randYOffset = randy.nextFloat() * -locationVariance.y;
		
		if(randy.nextBoolean()) 
			randZOffset = randy.nextFloat() * locationVariance.z;
		else
			randZOffset = randy.nextFloat() * -locationVariance.z;
		
		// place the particle at the offset for the emitter and rotate the location
		Vector3f behind = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(randXOffset + offset.x, randYOffset + offset.y, randZOffset + offset.z), this.following.rotation);
		
		// add the rotated vector to the location
		Vector3f loc = new Vector3f(this.following.location.x + behind.x, this.following.location.y + behind.y, this.following.location.z + behind.z);
		
		// figure out how much this particle's going to move
		Vector3f veloc = new Vector3f(randy.nextFloat() * velocityVariance.x, randy.nextFloat() * velocityVariance.y, randy.nextFloat() * velocityVariance.z);
		
		// rotate the velocity
		Vector3f rotVeloc = QuaternionHelper.rotateVectorByQuaternion(veloc, this.following.rotation);
		
		// get angular velocity and add it
		javax.vecmath.Vector3f angvel = new javax.vecmath.Vector3f();
		following.rigidBody.getAngularVelocity(angvel);
		rotVeloc.set(rotVeloc.x + angvel.x, rotVeloc.y + angvel.y, rotVeloc.z + angvel.z);
		
		this.addParticle(new Particle(loc, 0.4f, 0.4f, randy.nextFloat() * 0.5f, rotVeloc));
	}
}
