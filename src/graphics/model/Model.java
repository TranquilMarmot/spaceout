package graphics.model;

import com.bulletphysics.collision.shapes.CollisionShape;

public class Model {
	private CollisionShape shape;
	private int callList;
	
	public Model(CollisionShape shape, int callList){
		this.shape = shape;
		this.callList = callList;
	}
	
	public CollisionShape getCollisionShape(){
		return shape;
	}
	
	public int getCallList(){
		return callList;
	}
}
