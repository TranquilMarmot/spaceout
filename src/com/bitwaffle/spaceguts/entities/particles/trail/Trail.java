package com.bitwaffle.spaceguts.entities.particles.trail;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.graphics.shapes.Box2D;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.resources.Textures;

public class Trail {
	protected LinkedList<TrailLink> chain;
	
	private TrailRenderer renderer;
	
	protected Textures linkTex;
	
	private static Matrix4f oldModelView = new Matrix4f();
	
	private DynamicEntity following;
	
	private Vector3f offset;
	
	private int length;
	
	private float width;
	
	public boolean active;
	
	private float updateSpeed = 0.05f;
	private float timeSinceUpdate = 0.0f;
	
	public Trail(DynamicEntity following, int length, float width, Textures linkTexture, Vector3f offset){
		this.linkTex = linkTexture;
		
		this.following = following;
		this.length = length;
		this.offset = offset;
		this.width = width;
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
					chain.removeLast();
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
		
		Vector3f top = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(width, 0.0f, 0.0f), this.following.rotation);
		Vector3f bottom = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(-width, 0.0f, 0.0f), this.following.rotation);
		
		Vector3f.add(start, top, top);
		Vector3f.add(start, bottom, bottom);
		
		TrailLink link = new TrailLink(top, bottom);
		
		chain.addFirst(link);
	}
	
	public void draw(){
		// disable lighting and enable blending
		Render3D.program.setUniform("Light.LightEnabled", false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		oldModelView.load(Render3D.modelview);{
			/*
			 *  Basically what we have to do here is undo all the translations
			 *  done when rendering whatever we're following is translated to inside
			 *  of Render3D. This is because all the points created for the trail
			 *  are positioned directly behind what we're following, so the translations
			 *  throw everything off.
			 */
			// to undo rotation
			Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
			Quaternion.negate(this.following.rotation, revQuat);
			Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(revQuat), Render3D.modelview);
			
			// to undo translation
			float transX = -following.location.x;
			float transY = -following.location.y;
			float transZ = -following.location.z;
			Render3D.modelview.translate(new Vector3f(transX, transY, transZ));
			
			Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);
			
			renderer.draw();
		}Render3D.modelview.load(oldModelView);
		
		// Don't forget to re-disable blending and re-enable lighting!
		GL11.glDisable(GL11.GL_BLEND);
		Render3D.program.setUniform("Light.LightEnabled", true);
	}
}
