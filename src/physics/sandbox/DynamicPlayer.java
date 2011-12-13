package physics.sandbox;

import javax.vecmath.Quat4f;

import entities.Entities;
import graphics.model.Model;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import util.debug.Debug;
import util.helper.QuaternionHelper;
import util.manager.KeyboardManager;
import util.manager.ModelManager;
import util.manager.MouseManager;

public class DynamicPlayer extends DynamicEntity{
	public float xAccel = 100.0f;
	public float yAccel = 100.0f;
	public float zAccel = 100.0f;
	public float bulletSpeed = 100.0f;
	
	private boolean button0Down = false;
	

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
		
		zLogic();
		
		xLogic();
		
		yLogic();
		
		rotationLogic();
		
		if(MouseManager.button0 && !button0Down && !Debug.consoleOn){
			button0Down = true;
			shootBullet();
		}
		
		if(!MouseManager.button0)
			button0Down = false;
	}
	
	private void shootBullet(){
		Vector3f bulletLocation = new Vector3f(this.location.x, this.location.y, this.location.z);
		Quaternion bulletRotation = new Quaternion(this.rotation.x, this.rotation.y, this.rotation.z, this.rotation.w);
		
		Vector3f bulletMoveAmount = new Vector3f(0.0f, 0.0f, 10.0f);
		bulletMoveAmount = QuaternionHelper.RotateVectorByQuaternion(bulletMoveAmount, bulletRotation);
		Vector3f.add(bulletLocation, bulletMoveAmount, bulletLocation);
		
		int bulletModel = ModelManager.LASERBULLET;
		float bulletMass = 10.0f;
		float bulletRestitution = 1.0f;
		DynamicEntity bullet = new DynamicEntity(bulletLocation, bulletRotation, bulletModel, bulletMass, bulletRestitution);
		bullet.type = "bullet";
		Entities.addBuffer.add(bullet);
		javax.vecmath.Vector3f currentVelocity = new javax.vecmath.Vector3f();
		rigidBody.getInterpolationLinearVelocity(currentVelocity);
		Vector3f vec = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, currentVelocity.z + bulletSpeed), rotation);
		bullet.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(vec.x, vec.y, vec.z));
		//bullet.rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(vec.x, vec.y, vec.z));
	}
	
	private void zLogic(){
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
	}
	
	private void xLogic(){
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
	}
	
	private void yLogic(){
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
	}
	
	private void rotationLogic(){
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
