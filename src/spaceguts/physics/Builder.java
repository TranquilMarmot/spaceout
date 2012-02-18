package spaceguts.physics;

import java.util.Random;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.Camera;
import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entities;
import spaceguts.input.Keys;
import spaceguts.input.MouseManager;
import spaceguts.util.QuaternionHelper;
import spaceout.entities.dynamic.Planet;
import spaceout.resources.Textures;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.linearmath.Transform;

/**
 * A class to manage interacting with the world ('creative mode')
 * @author TranquilMarmot
 *
 */
public class Builder {
	/** Camera object for this builder */
	private Camera camera;
	
	/** What the builder is looking at */
	public DynamicEntity lookingAt;
	/** whether or not what the builder is looking at has been grabbed */
	public boolean entityGrabbed = false;
	
	/**
	 * Builder constructor
	 * @param camera Camera that this builder will see from
	 */
	public Builder(Camera camera){
		this.camera = camera;
	}
	
	/**
	 * Updates the builder (called when the camera is in builder mode)
	 */
	public void update(){
		whatsTheCameraLookingAt();
		
		if(Keys.P.isPressed())
			addRandomSphere();
	}
	
	
	/**
	 * Figures out what's at the center of the camera.
	 * Moves what's being looked at if the left mouse button is down,
	 * rotates what's being looked at if the right mouse button is down
	 */
	private void whatsTheCameraLookingAt(){
		// check if anything has been grabbed
		ClosestRayResultCallback cameraRay = camera.rayTestAtCenter();
		if(cameraRay.hasHit() && !entityGrabbed){
			lookingAt = (DynamicEntity) cameraRay.collisionObject.getUserPointer();
			
			if((MouseManager.button0 || MouseManager.button1) & !entityGrabbed){
				lookingAt.rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
				lookingAt.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
				entityGrabbed = true;
			}
		} else if(!MouseManager.button0 && !MouseManager.button1){
			lookingAt = null;
			entityGrabbed = false;
		}
		
		
		if(entityGrabbed){
			// move entity
			if(MouseManager.button0){
				Vector3f impulse = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(MouseManager.dx * 10, -MouseManager.dy * 10, MouseManager.wheel / 10), Entities.camera.rotation);
				
				Transform trans = new Transform();
				lookingAt.rigidBody.getWorldTransform(trans);
				
				trans.origin.add(new javax.vecmath.Vector3f(impulse.x, impulse.y, impulse.z));
				
				lookingAt.rigidBody.setWorldTransform(trans);
			}
			
			// rotate entity FIXME doesn't work properly
			if(MouseManager.button1){
				if(MouseManager.dx != 0 || MouseManager.dy != 0 || MouseManager.wheel != 0){
					Transform trans = new Transform();
					lookingAt.rigidBody.getWorldTransform(trans);
					
					Quat4f rot = new Quat4f();
					trans.getRotation(rot);
					
					Vector3f impulse = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(MouseManager.dx * 10, -MouseManager.dy * 10, MouseManager.wheel / 10), Entities.camera.rotation);
					
					Quaternion newRot = QuaternionHelper.rotate(new Quaternion(rot.x, rot.y, rot.z, rot.w), impulse);
					
					trans.setRotation(new Quat4f(newRot.x, newRot.y, newRot.z, newRot.z));
					
					lookingAt.rigidBody.setWorldTransform(trans);
				}
			}
		}
	}
	
	/**
	 * Adds a random sphere to the world
	 */
	private void addRandomSphere(){
		Random randy = new Random();
		float sphereSize = randy.nextInt(200) / 10.0f;
		
		Textures sphereTexture;
		String tex;
		
		switch(randy.nextInt(4)){
		case 0:
			sphereTexture = Textures.EARTH;
			tex = "Earth";
			break;
		case 1:
			sphereTexture = Textures.MERCURY;
			tex = "Mercury";
			break;
		case 2:
			sphereTexture = Textures.VENUS;
			tex = "Venus";
			break;
		case 3:
			sphereTexture = Textures.MARS;
			tex = "Mars";
			break;
		default:
			sphereTexture = Textures.EARTH;
			tex = "Earth";
		}
		
		float sphereX = randy.nextFloat() * 75.0f;
		float sphereY = randy.nextFloat() * 75.0f;
		float sphereZ = -100.0f;
		Vector3f sphereLocation = new Vector3f(sphereX, sphereY, sphereZ);
		
		// put the sphere right in front of the camera
		Vector3f downInFront = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, 300.0f), camera.rotation);
		
		Vector3f.add(camera.location, downInFront, downInFront);
		
		Vector3f.add(sphereLocation, downInFront, sphereLocation);
		
		Quaternion sphereRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		
		//float sphereMass = randy.nextFloat() * 10.0f;
		float sphereMass = sphereSize * 2.0f;
		
		Planet p = new Planet(sphereLocation, sphereRotation, sphereSize, sphereMass, 0.0f, sphereTexture);
		p.type = tex;
		
		Entities.addDynamicEntity(p);
	}

}
