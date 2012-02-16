package spaceguts.entities;

import org.lwjgl.util.vector.Vector3f;

import spaceguts.input.MouseManager;
import spaceguts.util.QuaternionHelper;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;

/**
 * A class to manage methods for interacting with the world ('creative mode')
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
	}
	
	
	/**
	 * Figures out what's at the center of the camera
	 */
	private void whatsTheCameraLookingAt(){
		if(camera.freeMode){
			if(entityGrabbed){
				Vector3f impulse = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(MouseManager.dx * 250, MouseManager.dy * -250, 0.0f), Entities.camera.rotation);
				
				//System.out.println(impulse.x + " " + impulse.y + " " + impulse.z);
				
				lookingAt.rigidBody.activate();
				
				lookingAt.rigidBody.applyCentralImpulse(new javax.vecmath.Vector3f(impulse.x, impulse.y, impulse.z));
			}
		}
		
		ClosestRayResultCallback cameraRay = camera.rayTestAtCenter();
		if(cameraRay.hasHit() && !entityGrabbed){
			lookingAt = (DynamicEntity) cameraRay.collisionObject.getUserPointer();
			
			if(MouseManager.button0 & !entityGrabbed){
				entityGrabbed = true;
			}
		} else if(!MouseManager.button0){
			lookingAt = null;
			entityGrabbed = false;
		}
	}

}
