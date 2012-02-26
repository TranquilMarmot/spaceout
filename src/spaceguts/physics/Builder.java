package spaceguts.physics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.Camera;
import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entities;
import spaceguts.graphics.gui.GUI;
import spaceguts.graphics.gui.button.MenuButton;
import spaceguts.graphics.gui.menu.picker.Picker;
import spaceguts.input.KeyBindings;
import spaceguts.input.Keys;
import spaceguts.input.MouseManager;
import spaceguts.util.QuaternionHelper;
import spaceout.entities.dynamic.Planet;
import spaceout.resources.Models;
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
	public boolean leftGrabbed = false, rightGrabbed = false;
	
	Picker<Models> picker;
	MenuButton addButton, cancelButton;
	
	boolean modelSelected = false;
	
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
	public void update(float timeStep){
		whatsTheCameraLookingAt();
		
		if(lookingAt != null && (leftGrabbed || rightGrabbed))
			grabLogic(timeStep);
		
		if(Keys.P.isPressed())
			addRandomSphere();
		
		if(Keys.TAB.pressedOnce())
			openPickerMenu();
	}
	
	private void openPickerMenu(){
		picker = new Picker<Models>(-50, -20, 20, 200, Models.values(), Textures.MENU_PICKER_ACTIVE, Textures.MENU_PICKER_MOUSEOVER, Textures.MENU_PICKER_PRESSED, Textures.MENU_PICKER_SELECTED);
		
		addButton = new MenuButton("Add", 119, 28, 115, -17);
		addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(picker.itemHasBeenSelected()){
					addDynamicEntity(picker.getSelectedItem());
				}
			}
		});
		
		
		
		cancelButton = new MenuButton("Cancel", 119, 28, 115, 17);
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				GUI.removeGUIObject(picker);
				GUI.removeGUIObject(addButton);
				GUI.removeGUIObject(cancelButton);
				
				GUI.menuUp = false;
			}
		});
		
		
		GUI.addGUIObject(picker);
		GUI.addGUIObject(addButton);
		GUI.addGUIObject(cancelButton);
		
		GUI.menuUp = true;
	}
	
	private void addDynamicEntity(Models model){
		
	}
	
	
	/**
	 * Figures out what's at the center of the camera.
	 * Moves what's being looked at if the left mouse button is down,
	 * rotates what's being looked at if the right mouse button is down
	 */
	private void whatsTheCameraLookingAt(){
		if(!leftGrabbed && !rightGrabbed){
			// check if anything has been grabbed
			ClosestRayResultCallback cameraRay = camera.rayTestAtCenter();
			
			if(cameraRay.hasHit()){
				lookingAt = (DynamicEntity) cameraRay.collisionObject.getUserPointer();
				
				// grab the entity if the mouse button is down
				if(!leftGrabbed && MouseManager.button0){
						// stop any linear velocity
						lookingAt.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
						leftGrabbed = true;
				}
				
				if(!rightGrabbed && MouseManager.button1){
						// stop any angular velocity
						lookingAt.rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
						rightGrabbed = true;
				}
			} else {
				// looking at nothing
				lookingAt = null;
			}
		}
	}
	
	/**
	 * Moves or rotates what is grabbed
	 * @param timeStep Amount of time passed (gotten from camera)
	 */
	private void grabLogic(float timeStep){
		// activate the rigid body if it's not active (since we're directly modifying its world transform and not just it's velocities)
		if(!lookingAt.rigidBody.isActive())
			lookingAt.rigidBody.activate();
		
		// move logic
		if(leftGrabbed){
			if(!MouseManager.button0){
				leftGrabbed = false;
			} else{
				// how far to move what we're looking at
				float dx = MouseManager.dx * 10;
				float dy = -MouseManager.dy * 10;
				float dz = MouseManager.wheel / 10;
				
				// rotate the distance by the camera's rotation
				Vector3f impulse = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(dx, dy, dz), Entities.camera.rotation);
				
				// get the entity's transform
				Transform trans = new Transform();
				lookingAt.rigidBody.getWorldTransform(trans);
				
				trans.origin.add(new javax.vecmath.Vector3f(impulse.x, impulse.y, impulse.z));
				
				// set the transform to the new location
				lookingAt.rigidBody.setWorldTransform(trans);
				
				// set the location so that it gets rendered properly (otherwise, the rendering location wouldn't be update until the end of the next physics world tick, which would be one frame too late)
				lookingAt.location.set(trans.origin.x, trans.origin.y, trans.origin.z);
			}
			
			// check for right grab
			if(MouseManager.button1){
				lookingAt.rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
				rightGrabbed = true;
			}
		}
		
		// rotate logic
		if(rightGrabbed){
			if(!MouseManager.button1){
				rightGrabbed = false;
			} else if(MouseManager.dx != 0 || MouseManager.dy != 0 || MouseManager.wheel != 0){
					Transform trans = new Transform();
					lookingAt.rigidBody.getWorldTransform(trans);
					
					Quat4f rot = new Quat4f();
					trans.getRotation(rot);
					
					// how much rotation to apply
					Vector3f amount = new Vector3f(-MouseManager.dy * 10, MouseManager.dx * 10, MouseManager.wheel / 10);
					
					// rotate amount by the camera's rotation
					Vector3f impulse = QuaternionHelper.rotateVectorByQuaternion(amount, Entities.camera.rotation);
					
					// rotate entity's rotation by our rotated amount
					Quaternion newRot = QuaternionHelper.rotate(new Quaternion(rot.x, rot.y, rot.z, rot.w), impulse);
					
					// set the rotation to the new rotation
					trans.setRotation(new Quat4f(newRot.x, newRot.y, newRot.z, newRot.w));
					
					// set the entity's world transform
					lookingAt.rigidBody.setWorldTransform(trans);
					
					// set the rotation so that it gets rendered properly (otherwise, the rendering rotation wouldn't be update until the end of the next physics world tick, which would be one frame too late)
					lookingAt.rotation.set(newRot.x, newRot.y, newRot.z, newRot.w);
			}
			
			// check for left grab
			if(MouseManager.button0){
				lookingAt.rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
				leftGrabbed = true;
			}
		}
		
		// move the entity with the camera
		moveEntityWithCamera(timeStep);
	}
	
	/**
	 * Moves the grabbed entity with the camera. Note that this is nearly identical to the <code>freeMode(float timeStep)</code> method in the {@link Camera} class.
	 * @param timeStep Amount of time passed (gotten from camera)
	 */
	private void moveEntityWithCamera(float timeStep){
		timeStep *= 100;
		Transform trans = new Transform();
		lookingAt.rigidBody.getWorldTransform(trans);
		
		Vector3f location = new Vector3f(trans.origin.x, trans.origin.y, trans.origin.z);
		
		// check for forward and backward movement
		boolean forward = KeyBindings.CONTROL_FORWARD.isPressed();
		boolean backward = KeyBindings.CONTROL_BACKWARD.isPressed();
		// control forward and backward movement
		if (forward) 
			location = QuaternionHelper.moveZ(camera.rotation, location, camera.speed * timeStep);
		if (backward)
			location = QuaternionHelper.moveZ(camera.rotation, location, -camera.speed * timeStep);

		// check for left and right movement
		boolean left = KeyBindings.CONTROL_LEFT.isPressed();
		boolean right = KeyBindings.CONTROL_RIGHT.isPressed();
		// control strafing left and right
		if (left)
			location = QuaternionHelper.moveX(camera.rotation, location, camera.speed * timeStep);
		if (right)
			location = QuaternionHelper.moveX(camera.rotation, location, -camera.speed * timeStep);

		// handle going up/down
		boolean up = KeyBindings.CONTROL_ASCEND.isPressed();
		boolean down = KeyBindings.CONTROL_DESCEND.isPressed();
		if (up)
			location = QuaternionHelper.moveY(camera.rotation, location, -camera.speed * timeStep);
		if (down)
			location = QuaternionHelper.moveY(camera.rotation, location, camera.speed * timeStep);
		
		trans.origin.set(location.x, location.y, location.z);
		
		lookingAt.location.set(location);
		
		lookingAt.rigidBody.setWorldTransform(trans);
	}
	
	/**
	 * Adds a random sphere to the world, right in front of the camera
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
