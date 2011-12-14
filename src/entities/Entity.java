package entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.helper.QuaternionHelper;

/**
 * Class that pretty much anything in-game extends.
 * @author TranquilMarmot
 *
 */
public abstract class Entity {
	/** the entity's current location */
	public Vector3f location;

	/** quaternion representing rotation */
	public Quaternion rotation;

	/** type, used for lots of things */
	public String type;

	/**
	 * Entity constructor
	 */
	public Entity() {
		location = new Vector3f(0.0f, 0.0f, 0.0f);
		rotation = new Quaternion(1.0f, 0.0f, 0.0f, 1.0f);
		type = "entity";
	}
	
	/**
	 * Updates this entity
	 */
	public abstract void update();

	/**
	 * Draws this entity
	 */
	public abstract void draw();
	
	/**
	 * Have the entity provide any necessary clenup procedures, like gettting rid of textures
	 */
	public abstract void cleanup();

	/**
	 * Rotate this entity along the X axis
	 * 
	 * @param amount
	 *            Amount to rotate this entity
	 */
	public void rotateX(float amount) {
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(sinVal, 0.0f, 0.0f, cosVal);
		Quaternion.mul(rotation, rot, rotation);
	}

	/**
	 * Rotate this entity along the Y axis
	 * 
	 * @param amount
	 *            Amount to rotate this entity
	 */
	public void rotateY(float amount) {
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(0.0f, sinVal, 0.0f, cosVal);
		Quaternion.mul(rotation, rot, rotation);
	}

	/**
	 * Rotate this entity along the Z axis
	 * 
	 * @param amount
	 *            Amount to rotate this entity
	 */
	public void rotateZ(float amount) {
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(0.0f, 0.0f, sinVal, cosVal);
		Quaternion.mul(rotation, rot, rotation);
	}
	
	/**
	 * Move this entity along the X axis
	 * 
	 * @param amount
	 *            Amount to move this entity
	 */
	public void moveX(float amount) {
		Vector3f multi = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(amount, 0.0f, 0.0f), rotation);
		Vector3f.add(location, multi, location);
	}

	/**
	 * Move this entity along the Y axis
	 * 
	 * @param amount
	 *            Amount to move this entity
	 */
	public void moveY(float amount) {
		Vector3f multi = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(0.0f, amount, 0.0f), rotation);
		Vector3f.add(location, multi, location);
	}

	/**
	 * Move this entity along the Z axis
	 * 
	 * @param amount
	 *            Amount to move this entity
	 */
	public void moveZ(float amount) {
		Vector3f multi = QuaternionHelper.RotateVectorByQuaternion(new Vector3f(0.0f, 0.0f, amount), rotation);
		Vector3f.add(location, multi, location);
	}
}
