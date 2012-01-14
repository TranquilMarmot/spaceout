package spaceguts.util.model;

import org.newdawn.slick.opengl.Texture;

import spaceguts.util.resources.ResourceLoader;
import spaceguts.util.resources.Textures;

import com.bulletphysics.collision.shapes.CollisionShape;

/**
 * A 3D model for drawing and colliding with things
 * 
 * @author TranquilMarmot
 * 
 */
public class Model {
	/** The collision shape to use for this model */
	private CollisionShape shape;

	/** The call list to use to draw this model */
	private int callList;

	/** The texture to use when drawing this model (from the TextureManager) */
	private Textures texture;

	/**
	 * Model initializer
	 * 
	 * @param shape
	 *            The CollisionShape to use for the model
	 * @param callList
	 *            The call list to call to draw the model
	 * @param texture
	 *            Which texture to use for this model (from
	 *            {@link ResourceLoader}
	 */
	public Model(CollisionShape shape, int callList, Textures texture) {
		this.shape = shape;
		this.callList = callList;
		this.texture = texture;
	}

	/**
	 * @return The model's CollisionShape
	 */
	public CollisionShape getCollisionShape() {
		return shape;
	}

	public Texture getTexture() {
		return texture.getTexture();
	}

	/**
	 * @return The model's call list
	 */
	public int getCallList() {
		return callList;
	}
}
