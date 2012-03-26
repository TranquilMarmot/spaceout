package com.bitwaffle.spaceguts.entities.particles.trail;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.graphics.render.Render3D;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.resources.Textures;

/**
 * A trail that follows a {@link DynamicEntity}
 * Whenever the entity owning the Trail calls its update() and draw() methods,
 * the Trail's update() and draw() methods should be called as well.
 * @author TranquilMarmot
 *
 */
public class Trail {
	/** All the actual links */
	protected LinkedList<TrailLink> chain;
	//protected LinkedList<TrailLink> chain2;
	//protected LinkedList<TrailLink> chain3;
	//protected LinkedList<TrailLink> chain4;
	
	/** Handles the vertex array object for this trail */
	private TrailRenderer renderer;
	//private TrailRenderer renderer2;
	//private TrailRenderer renderer3;
	//private TrailRenderer renderer4;
	
	/** Texture to use for each link */
	protected Textures linkTex;
	
	/** Used for preserving the modelview matrix */
	private static Matrix4f oldModelView = new Matrix4f();
	
	/** Entity that the trail is coming from */
	private DynamicEntity following;
	
	/** Offset from the center of the entity the trail is following */
	private Vector3f offset;
	
	/** How long this trail is allowed to get */
	private int length;
	
	/** How wide each link is */
	private float width;
	
	/** How often the trail sends out a new link */
	private float updateSpeed = 0.05f;
	
	/** Counter to know when to update */
	private float timeSinceUpdate = 0.0f;
	
	/**
	 * Create a trail
	 * @param following Entity the trail is coming from
	 * @param length How long the trail is
	 * @param width How wide each link in the trail is
	 * @param linkTexture The texture to use for rendering the trail links
	 * @param offset Offset from following's center
	 */
	public Trail(DynamicEntity following, int length, float width, Textures linkTexture, Vector3f offset){
		this.linkTex = linkTexture;
		this.following = following;
		this.length = length;
		this.offset = offset;
		this.width = width;
		
		chain = new LinkedList<TrailLink>();
		//chain2 = new LinkedList<TrailLink>();
		//chain3 = new LinkedList<TrailLink>();
		//chain4 = new LinkedList<TrailLink>();
		renderer = new TrailRenderer(chain, linkTex);
		//renderer2 = new TrailRenderer(chain2, linkTex);
		//renderer3 = new TrailRenderer(chain3, linkTex);
		//renderer4 = new TrailRenderer(chain4, linkTex);
	}
	
	/**
	 * Update the trail
	 * @param timeStep How long has passed since the last update
	 */
	public void update(float timeStep){
		// update timer
		timeSinceUpdate += timeStep;
		
		// only add a link if the timer is up
		if(timeSinceUpdate >= updateSpeed){
			// reset timer
			timeSinceUpdate = 0.0f;
			
			// if we haven't reached out length, add a new link
			if(chain.size() < length){
				addLink();
			} else{
				// remove the last link (addLink() adds to the front of the list)
				chain.removeLast();
				//chain2.removeLast();
				//chain3.removeLast();
				//chain4.removeLast();
				addLink();
			}
			
			// let the renderer know that it needs to update the vertex buffers
			renderer.updateVBO();
			//renderer2.updateVBO();
			//renderer3.updateVBO();
			//renderer4.updateVBO();
		}
	}
	
	/**
	 * Adds a new link to the front of the list
	 */
	private void addLink(){
		// rotate the offset by the current rotation
		Vector3f offsetRot = QuaternionHelper.rotateVectorByQuaternion(offset, following.rotation);
		Vector3f start = new Vector3f();
		// add location to offset
		Vector3f.add(following.location, offsetRot, start);
		
		// top point for link
		Vector3f top = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(width / 2, 0.0f, 0.0f), this.following.rotation);
		
		// bottom point for link
		Vector3f bottom = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(-width / 2, 0.0f, 0.0f), this.following.rotation);
		
		// add to the starting vector
		Vector3f.add(start, top, top);
		Vector3f.add(start, bottom, bottom);
		
		// add new link to chain
		chain.addFirst(new TrailLink(top, bottom));
		
		/*
		Quaternion by90 = QuaternionHelper.rotate(this.following.rotation, new Vector3f(0.0f, 0.0f, 90.0f));
		Vector3f top2 = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(width / 2, 0.0f, 0.0f), by90);
		Vector3f bottom2 = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(-width / 2, 0.0f, 0.0f), by90);
		Vector3f.add(start, top2, top2);
		Vector3f.add(start, bottom2, bottom2);
	
		
		// add new link to chain
		chain2.addFirst(new TrailLink(top2, bottom2));
		
		Quaternion by180 = QuaternionHelper.rotate(this.following.rotation, new Vector3f(0.0f, 0.0f, 135.0f));
		Vector3f top3 = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(width / 2, 0.0f, 0.0f), by180);
		Vector3f bottom3 = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(-width / 2, 0.0f, 0.0f), by180);
		Vector3f.add(start, top3, top3);
		Vector3f.add(start, bottom3, bottom3);
	
		
		// add new link to chain
		chain3.addFirst(new TrailLink(top3, bottom3));
		
		Quaternion by270 = QuaternionHelper.rotate(this.following.rotation, new Vector3f(0.0f, 0.0f, 225.0f));
		Vector3f top4 = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(width / 2, 0.0f, 0.0f), by270);
		Vector3f bottom4 = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(-width / 2, 0.0f, 0.0f), by270);
		Vector3f.add(start, top4, top4);
		Vector3f.add(start, bottom4, bottom4);
	
		
		// add new link to chain
		chain4.addFirst(new TrailLink(top4, bottom4));
		*/
	}
	
	/**
	 * Draws the trail
	 */
	public void draw(){
		if(chain.size() > 0){
			// disable lighting and enable blending
			Render3D.program.setUniform("Light.LightEnabled", false);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			linkTex.texture().bind();
			
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
				Vector3f undo = new Vector3f();
				this.following.location.negate(undo);
				Render3D.modelview.translate(undo);
				
				// we want to translate to the last link
				TrailLink last = chain.getLast();
				
				// get the middle of the top and bottom of the last link, so it renders in the middle
				float midX = (last.top.x + last.bottom.x) / 2;
				float midY = (last.top.y + last.bottom.y) / 2;
				float midZ = (last.top.z + last.bottom.z) / 2;
				
				// offset by given amount, rotated (so that it's from the center of the entity)
				Vector3f offsetRot = QuaternionHelper.rotateVectorByQuaternion(this.offset, this.following.rotation);
				
				// to translate to the last link
				float transX = following.location.x - midX + offsetRot.x;
				float transY = following.location.y - midY + offsetRot.y;
				float transZ = following.location.z - midZ + offsetRot.z;
				Render3D.modelview.translate(new Vector3f(transX, transY, transZ));
				
				Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);
				
				renderer.draw();
				//renderer2.draw();
				//renderer3.draw();
				//renderer4.draw();
			}Render3D.modelview.load(oldModelView);
			
			// Don't forget to re-disable blending and re-enable lighting!
			GL11.glDisable(GL11.GL_BLEND);
			Render3D.program.setUniform("Light.LightEnabled", true);
		}
	}
}
