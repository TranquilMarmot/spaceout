package entities;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import util.helper.QuaternionHelper;
import util.manager.TextureManager;

/**
 * Skybox to make it seem like there's stars everywhere. Follows an Entity around, so the end of it can never be reached.
 * @author TranquilMarmot
 * @see Entity
 *
 */
public class Skybox extends Entity {
	public Sphere sphere;
	public float size;

	// the skybox's center will always be on the entity that it is following
	public Entity following;

	/**
	 * Skybox constructor
	 * @param following The entity this skybox is following
	 * @param size The size of this skybox
	 * @param pitch Initial pitch
	 * @param yaw Initial yaw
	 * @param roll Initial roll
	 */
	public Skybox(Entity following, float size, float pitch, float yaw,
			float roll) {
		super();
		this.type = "skybox";

		this.following = following;
		
		rotation = QuaternionHelper.getQuaternionFromAngles(pitch, yaw, roll);	

		sphere = new Sphere();
		sphere.setNormals(GLU.GLU_SMOOTH);
		sphere.setTextureFlag(true);
		
		this.size = size;
		
		rotationBuffer = BufferUtils.createFloatBuffer(16);
	}

	@Override
	public void update() {
		this.location.x = following.location.x;
		this.location.y = following.location.y;
		this.location.z = following.location.z;
	}

	@Override
	public void draw() {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
		{
			// bind the entity's texture before drawing
			TextureManager.getTexture(TextureManager.STARS).bind();
			GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
			sphere.draw(size, 6, 6);
		}
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

}
