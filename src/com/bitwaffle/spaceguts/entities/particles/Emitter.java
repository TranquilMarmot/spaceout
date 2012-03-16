package com.bitwaffle.spaceguts.entities.particles;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.graphics.shapes.Box2D;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.resources.Textures;

/**
 * A class for shooting particles out
 * @author TranquilMarmot
 */
public abstract class Emitter<T extends Particle> extends Entity{
	private ArrayList<T> particles;
	private Textures particleTex;
	private static Matrix4f oldModelView = new Matrix4f();
	private static Box2D box = new Box2D(1.0f, 1.0f, Textures.PARTICLE.texture());
	
	public Emitter(Vector3f location, Textures particleTex){
		this.location = location;
		this.particleTex = particleTex;
		particles = new ArrayList<T>();
	}

	@Override
	public void update(float timeStep) {
		ArrayList<T> deleteList = new ArrayList<T>();
		for(T p : particles){
			p.update(timeStep);
			if(p.removeFlag)
				deleteList.add(p);
		}
		
		if(!deleteList.isEmpty()){
			for(T p : deleteList)
				this.removeParticle(p);
		}
	}
	
	public void addParticle(T p){
		particles.add(p);
	}
	
	public void addParticle(T p, int index){
		particles.add(index, p);
	}
	
	public void addParticles(ArrayList<T> p){
		particles.addAll(p);
	}
	
	public void removeParticle(T p){
		particles.remove(p);
	}
	
	public void removeParticle(int index){
		particles.remove(index);
	}
	
	public void removeParticles(ArrayList<T> p){
		particles.removeAll(p);
	}

	@Override
	public void draw() {
		Render3D.program.setUniform("Light.LightEnabled", false);
		
		Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.rotation.negate(revQuat);
		
		particleTex.texture().bind();
		GL30.glBindVertexArray(box.getVAOHandle());
		
		for(T p : particles){
			float transx = this.location.x - p.location.x;
			float transy = this.location.y - p.location.y;
			float transz = this.location.z - p.location.z;
			
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
		
		Render3D.program.setUniform("Light.LightEnabled", true);
	}

	@Override
	public void cleanup() {
		
	}
}
