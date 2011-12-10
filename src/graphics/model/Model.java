package graphics.model;

import org.newdawn.slick.opengl.Texture;

import util.manager.TextureManager;

import com.bulletphysics.collision.shapes.CollisionShape;

/**
 * A 3D model for drawing and colliding with things
 * @author TranquilMarmot
 *
 */
public class Model {
	private CollisionShape shape;
	private int callList;
	private int texture;
	
	/**
	 * Model initializer
	 * @param shape The CollisionShape to use for the model
	 * @param callList The call list to call to draw the model
	 */
	public Model(CollisionShape shape, int callList, int texture){
		this.shape = shape;
		this.callList = callList;
		this.texture = texture;
	}
	
	/**
	 * 
	 * @return The model's CollisionShape
	 */
	public CollisionShape getCollisionShape(){
		return shape;
	}
	
	public Texture getTexture(){
		return TextureManager.getTexture(texture);
	}
	
	/**
	 * 
	 * @return The model's call list
	 */
	public int getCallList(){
		return callList;
	}
}
