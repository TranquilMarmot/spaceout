package graphics.model;

import com.bulletphysics.collision.shapes.CollisionShape;

/**
 * A 3D model for drawing and colliding with things
 * @author TranquilMarmot
 *
 */
public class Model {
	private CollisionShape shape;
	private int callList;
	
	/**
	 * Model initializer
	 * @param shape The CollisionShape to use for the model
	 * @param callList The call list to call to draw the model
	 */
	public Model(CollisionShape shape, int callList){
		this.shape = shape;
		this.callList = callList;
	}
	
	/**
	 * Initialize a model without a CollisionShape
	 * @param callList The call list to call to draw the model
	 */
	public Model(int callList){
		this.callList = callList;
	}
	
	/**
	 * 
	 * @return The model's CollisionShape
	 */
	public CollisionShape getCollisionShape(){
		return shape;
	}
	
	/**
	 * 
	 * @return The model's call list
	 */
	public int getCallList(){
		return callList;
	}
}
