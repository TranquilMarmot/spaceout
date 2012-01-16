package spaceout.entities.passive;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import spaceguts.entities.Entity;
import spaceguts.util.resources.Models;

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
	public void update() {
		// keep the skybox centered on what it's following
		this.location.x = following.location.x;
		this.location.y = following.location.y;
		this.location.z = following.location.z;
	}

	@Override
	public void draw() {
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		model.getModel().getTexture().texture().bind();
		GL11.glCallList(model.getModel().getCallList());
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void cleanup() {
	}

}
