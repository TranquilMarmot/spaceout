package physics.sandbox;

import graphics.model.Model;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.manager.KeyboardManager;

public class DynamicPlayer extends DynamicEntity{

	public DynamicPlayer(Vector3f location, Quaternion rotation, int model,
			float mass, float restitution) {
		super(location, rotation, model, mass, restitution);
	}
	
	public DynamicPlayer(Vector3f location, Quaternion rotation, Model model,
			float mass, float restitution) {
		super(location, rotation, model, mass, restitution);
	}
	
	@Override
	public void update(){
		super.update();
		
		boolean forward = KeyboardManager.forward;
		boolean backward = KeyboardManager.backward;
		
		boolean left = KeyboardManager.left;
		boolean right = KeyboardManager.right;
		
		if(forward || backward){
			if(forward){
				System.out.println("pshhhh");
				rigidBody.applyCentralForce(new javax.vecmath.Vector3f(0.0f, 0.0f, -10000.0f));
			}
		}
	}

}
