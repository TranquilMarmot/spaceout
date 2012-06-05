package com.bitwaffle.spaceout.entities.passive;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceout.resources.Models;


/**
 * Skybox to make it seem like there's stars everywhere. Follows an Entity
 * around, so the end of it can never be reached.
 * 
 * @author TranquilMarmot
 * 
 */
public class Skybox extends Entity {
	// the model to use for the skybox
	private Models model = Models.SKYBOX;

	// the skybox's center will always be on the entity that it is following
	public Entity following;

	/**
	 * Skybox constructor
	 * 
	 * @param following
	 *            The entity this skybox is following
	 */
	public Skybox(Entity following) {
		super();
		this.type = "skybox";

		this.following = following;

		rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
	}

	@Override
	public void update(float timeStep) {
		// keep the skybox centered on what it's following
		this.location.set(following.location);
	}

	@Override
	public void draw() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		model.getModel().getTexture().texture().bind();
		model.getModel().render();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void cleanup() {
	}

}
