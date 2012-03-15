package com.bitwaffle.spaceguts.entities.particles;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.resources.Textures;

/**
 * A class for shooting particles out
 * @author TranquilMarmot
 */
public class Emitter<T extends Particle> extends Entity{
	private ArrayList<T> particles;
	private Textures particleTex;
	private static Matrix4f oldModelView = new Matrix4f();
	
	public Emitter(Vector3f location, Textures particleTex){
		this.location = location;
		this.particleTex = particleTex;
		particles = new ArrayList<T>();
	}

	@Override
	public void update(float timeStep) {
		for(Particle p : particles)
			p.update(timeStep);
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
		
		//Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		//Entities.camera.rotation.negate(revQuat);
		
		particleTex.texture().bind();
		
		for(Particle p : particles){
			float transx = this.location.x - p.location.x;
			float transy = this.location.y - p.location.y;
			float transz = this.location.z - p.location.z;
			
			oldModelView.load(Render3D.modelview);{
				// translate and scale the modelview
				Render3D.modelview.translate(new Vector3f(transx, transy, transz));
				Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(Entities.camera.rotation), Render3D.modelview);
				//Render3D.modelview.scale(new Vector3f(s.size, s.size, s.size));
				Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);

				// draw the particle
				GL11.glBegin(GL11.GL_QUADS);{
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2f(0, 0);

					GL11.glTexCoord2f(particleTex.texture().getWidth(), 0);
					GL11.glVertex2f(p.width, 0);
					
					GL11.glTexCoord2f(particleTex.texture().getWidth(), particleTex.texture().getHeight());
					GL11.glVertex2f(p.width, p.height);

					GL11.glTexCoord2f(0, particleTex.texture().getHeight());
					GL11.glVertex2f(0, p.height);
				}GL11.glEnd();
			}Render3D.modelview.load(oldModelView);
		}
		
		Render3D.program.setUniform("Light.LightEnabled", true);
	}

	@Override
	public void cleanup() {
		
	}

}
