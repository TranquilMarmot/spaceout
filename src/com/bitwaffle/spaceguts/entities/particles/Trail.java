package com.bitwaffle.spaceguts.entities.particles;

import java.util.LinkedList;

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

public class Trail {
	protected LinkedList<TrailLink> chain;
	
	private TrailRenderer renderer;
	
	protected Textures linkTex;
	
	private static Matrix4f oldModelView = new Matrix4f();
	
	private static Box2D box;
	
	private DynamicEntity following;
	
	private Vector3f offset;
	
	private int length;
	
	public boolean active;
	
	private float updateSpeed = 0.1f;
	private float timeSinceUpdate = 0.0f;
	
	public Trail(DynamicEntity following, int length, Textures linkTexture, Vector3f offset){
		this.linkTex = linkTexture;
		box = new Box2D(1.0f, 1.0f, linkTex.texture());
		
		this.following = following;
		this.length = length;
		this.offset = offset;
		chain = new LinkedList<TrailLink>();
		renderer = new TrailRenderer(this);
	}
	
	public void update(float timeStep){
		//if(active){
			timeSinceUpdate += timeStep;
			
			if(timeSinceUpdate >= updateSpeed){
				timeSinceUpdate = 0.0f;
				if(chain.size() < length){
					addLink();
				} else{
					chain.removeFirst();
					addLink();
				}
				
				renderer.updateVBO();
			}
		//} else{
		//	if(!chain.isEmpty())
		///		chain.clear();
		//}
	}
	
	private void addLink(){
		Vector3f offsetRot = QuaternionHelper.rotateVectorByQuaternion(offset, following.rotation);
		Vector3f start = new Vector3f();
		Vector3f.add(following.location, offsetRot, start);
		
		float width = 3.0f;
		float distBehind = 10.0f;
		
		Vector3f top = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(width, 0.0f, 0.0f), this.following.rotation);
		Vector3f bottom = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(-width, 0.0f, 0.0f), this.following.rotation);
		
		Vector3f.add(start, top, top);
		Vector3f.add(start, bottom, bottom);
		
		TrailLink link = new TrailLink(top, bottom);
		
		chain.addLast(link);
	}
	
	public void draw(){
		// disable lighting and enable blending
		Render3D.program.setUniform("Light.LightEnabled", false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.negate(this.following.rotation, revQuat);
		
		oldModelView.load(Render3D.modelview);{
		
			Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(revQuat), Render3D.modelview);
			
			Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);
			renderer.draw();
		}Render3D.modelview.load(oldModelView);
		
		GL11.glDisable(GL11.GL_BLEND);
		Render3D.program.setUniform("Light.LightEnabled", true);

		
		// bind texture and array handle
		/*
		linkTex.texture().bind();
		GL30.glBindVertexArray(box.getVAOHandle());
		
		for(TrailLink l : chain){
			// amount to translate
			float transx = this.following.location.x - l.start.x;
			float transy = this.following.location.y - l.start.y;
			float transz = this.following.location.z - l.start.z;
			
			// save the modelview before we manipulate it
			oldModelView.load(Render3D.modelview);{
				Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(revQuat), Render3D.modelview);
				// translate and scale the modelview
				Render3D.modelview.translate(new Vector3f(transx, transy, transz));
				Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(Entities.camera.rotation), Render3D.modelview);
				
				
				float ang = Vector3f.angle(l.start, l.end);
				Render3D.modelview.rotate(ang, new Vector3f(0.0f, 0.0f, 1.0f));
				
				Render3D.modelview.scale(new Vector3f(l.width, l.height, 1.0f));
				Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);

				// draw the particle
				GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
			}Render3D.modelview.load(oldModelView);
		}
		*/
	}
}
