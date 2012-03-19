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
		if(active){
			if(chain.size() < length){
				addLink();
			} else{
				chain.removeLast();
				addLink();
			}
		} else{
			chain.clear();
		}
	}
	
	private void addLink(){
		Vector3f offsetRot = QuaternionHelper.rotateVectorByQuaternion(offset, following.rotation);
		Vector3f start = new Vector3f();
		Vector3f.add(following.location, offsetRot, start);
		
		float width = 0.5f;
		
		Vector3f posWidthOffset = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, width), following.rotation);
		Vector3f negWidthOffset = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, width), following.rotation);
		Vector3f start1 = new Vector3f(), start2 = new Vector3f();
		Vector3f.add(start, posWidthOffset, start1);
		Vector3f.add(start, negWidthOffset, start2);
		
		Vector3f end1 = new Vector3f(), end2 = new Vector3f();
		if(!chain.isEmpty()){
			TrailLink first = chain.getFirst();
			end1 = first.start1;
			end2 = first.start2;
		}else{
			Vector3f behind = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, 0.5f), following.rotation);
			Vector3f.add(start1, behind, end1);
			Vector3f.add(start2, behind, end2);
		}
		
		TrailLink link = new TrailLink(start1, start2, end1, end2, 0.5f, 0.5f);
		
		chain.addFirst(link);
	}
	
	public void draw(){
		// disable lighting and enable blending
		Render3D.program.setUniform("Light.LightEnabled", false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		// get the revese rotation of what we're following
		Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		this.following.rotation.negate(revQuat);
		
		// bind texture and array handle
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
	}
}
