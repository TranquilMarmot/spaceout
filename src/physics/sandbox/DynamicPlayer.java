package physics.sandbox;

import javax.vecmath.Quat4f;

import entities.Entities;
import graphics.model.Model;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import physics.Physics;

import com.bulletphysics.linearmath.Transform;

import util.helper.QuaternionHelper;
import util.manager.KeyboardManager;
import util.manager.MouseManager;

public class DynamicPlayer extends DynamicEntity{
	public float xAccel = 1000.0f;
	public float yAccel = 1000.0f;
	public float zAccel = 1000.0f;
	

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
		
		if(forward || backward){
			if(forward){
				Vector3f vec = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, zAccel), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x, vec.y, vec.z));
			}
			if(backward){
				Vector3f vec = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, -zAccel), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x, vec.y, vec.z));
			}
		}
		
		boolean left = KeyboardManager.left;
		boolean right = KeyboardManager.right;
		
		if (left || right) {
			if (left) {
				Vector3f vec = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(xAccel, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x, vec.y, vec.z));
			}
			if (right) {
				Vector3f vec = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(-xAccel, 0.0f, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x, vec.y, vec.z));
			}
		}
		
		boolean ascend = KeyboardManager.ascend;
		boolean descend = KeyboardManager.descend;
		
		if(ascend || descend){
			if(ascend){
				Vector3f vec = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(0.0f, -yAccel, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x, vec.y, vec.z));
			}
			if(descend){
				Vector3f vec = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(0.0f, yAccel, 0.0f), rotation);
				rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x, vec.y, vec.z));
			}
		}
		
		if(!Entities.camera.vanityMode){
			Transform worldTransform = new Transform();
			rigidBody.getWorldTransform(worldTransform);
			
			rotateX(MouseManager.dy);
			rotateY(MouseManager.dx);
			
			boolean rollRight = KeyboardManager.rollRight;
			boolean rollLeft = KeyboardManager.rollLeft;
			
			if(rollRight)
				rotateZ(-2.0f);
			if(rollLeft)
				rotateZ(2.0f);
			
			Transform newTransform = new Transform();
			newTransform.set(worldTransform);
			newTransform.setRotation(new Quat4f(rotation.x, rotation.y, rotation.z, rotation.w));
			
			rigidBody.setWorldTransform(newTransform);
		}
	}

}
