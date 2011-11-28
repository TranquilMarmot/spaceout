package entities;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.debug.Debug;
import util.helper.QuaternionHelper;
import util.manager.TextureManager;

/**
 * Class that pretty much anything in-game extends.
 * @author TranquilMarmot
 *
 */
public abstract class Entity {
	// the entity's location
	public Vector3f location;

	/** how fast this entity is moving */
	public float xSpeed, ySpeed, zSpeed;
	
	/** maximum speeds */
	public float maxXSpeed, maxYSpeed, maxZSpeed;
	
	/** acceleration speeds*/
	public float xAccel, yAccel, zAccel;
	
	/** deceleration speeds */
	public float xDecel, yDecel, zDecel;

	/** quaternion representing rotation */
	public Quaternion rotation;
	protected FloatBuffer rotationBuffer;

	/** type, used for lots of things */
	public String type;

	/** color (not really used) */
	public float[] color;

	/** texture */
	public int texture = TextureManager.WHITE;

	/** the last time that this entity was updated */
	protected long lastUpdate;

	/**
	 * Entity constructor
	 */
	public Entity() {
		location = new Vector3f(0.0f, 0.0f, 0.0f);
		rotation = new Quaternion(1.0f, 0.0f, 0.0f, 1.0f);
		type = "entity";
		color = new float[] { 1.0f, 1.0f, 1.0f };
		xSpeed = 0.0f;
		ySpeed = 0.0f;
		zSpeed = 0.0f;
	}

	/**
	 * Move this entity along the X axis
	 * @param amount Amount to move this entity
	 */
	public void moveX(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(1.0f, 0.0f, 0.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	/**
	 * Move this entity along the Y axis
	 * @param amount Amount to move this entity
	 */
	public void moveY(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(0.0f, 1.0f, 0.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	/**
	 * Move this entity along the Z axis
	 * @param amount Amount to move this entity
	 */
	public void moveZ(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(0.0f, 0.0f, 1.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	/**
	 * Rotate this entity along the X axis
	 * @param amount Amount to rotate this entity
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
	 * @param amount Amount to rotate this entity
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
	 * @param amount Amount to rotate this entity
	 */
	public void rotateZ(float amount) {
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(0.0f, 0.0f, sinVal, cosVal);
		Quaternion.mul(rotation, rot, rotation);
	}

	/**
	 * Changes the entity's z movement speed going positive
	 * @param delta The amount of time since the last update (acquired from getDelta every update)
	 */
	protected void accelerateZPos(int delta) {
		if(zSpeed < maxZSpeed){
			float newSpeed = zSpeed + (delta * zAccel);
			if(newSpeed > maxZSpeed)
				zSpeed = maxZSpeed;
			else
				zSpeed = newSpeed;
		}
	}
	
	/**
	 * Changes the entity's z movement speed going negative
	 * @param delta The amount of time since the last update (acquired from getDelta every update)
	 */
	protected void accelerateZNeg(int delta) {
		if(zSpeed > -maxZSpeed){
			float newSpeed = zSpeed - (delta * zAccel);
			if(newSpeed < -maxZSpeed)
				zSpeed = -maxZSpeed;
			else
				zSpeed = newSpeed;
		}
	}
	
	/**
	 * Decelerates the entity in the opposite direction that it's going until it reaches 0
	 * @param delta
	 */
	protected void decelerateZ(int delta){
		if (zSpeed > 0){
			float newSpeed = zSpeed - (delta * zDecel);
			if(newSpeed < 0)
				zSpeed = 0;
			else
				zSpeed = newSpeed;
		} else if(zSpeed < 0){
			float newSpeed = zSpeed + (delta * zDecel);
			if(newSpeed > 0)
				zSpeed = 0;
			else
				zSpeed = newSpeed;
		}
		// else zSpeed is zero and nothing needs to be done
	}
	
	/**
	 * Changes the entity's z movement speed going positive
	 * @param delta The amount of time since the last update (acquired from getDelta every update)
	 */
	protected void accelerateXPos(int delta) {
		if(xSpeed < maxXSpeed){
			float newSpeed = xSpeed + (delta * xAccel);
			if(newSpeed > maxXSpeed)
				xSpeed = maxXSpeed;
			else
				xSpeed = newSpeed;
		}
	}
	
	/**
	 * Changes the entity's z movement speed going negative
	 * @param delta The amount of time since the last update (acquired from getDelta every update)
	 */
	protected void accelerateXNeg(int delta) {
		if(xSpeed > -maxXSpeed){
			float newSpeed = xSpeed - (delta * xAccel);
			if(newSpeed < -maxXSpeed)
				xSpeed = -maxXSpeed;
			else
				xSpeed = newSpeed;
		}
	}
	
	/**
	 * Decelerates the entity in the opposite direction that it's going until it reaches 0
	 * @param delta
	 */
	protected void decelerateX(int delta){
		if (xSpeed > 0){
			float newSpeed = xSpeed - (delta * xDecel);
			if(newSpeed < 0)
				xSpeed = 0;
			else
				xSpeed = newSpeed;
		} else if(xSpeed < 0){
			float newSpeed = xSpeed + (delta * xDecel);
			if(newSpeed > 0)
				xSpeed = 0;
			else
				xSpeed = newSpeed;
		}
		// else zSpeed is zero and nothing needs to be done
	}
	
	/**
	 * Changes the entity's z movement speed going positive
	 * @param delta The amount of time since the last update (acquired from getDelta every update)
	 */
	protected void accelerateYPos(int delta) {
		if(ySpeed < maxYSpeed){
			float newSpeed = ySpeed + (delta * yAccel);
			if(newSpeed > maxYSpeed)
				ySpeed = maxYSpeed;
			else
				ySpeed = newSpeed;
		}
	}
	
	/**
	 * Changes the entity's z movement speed going negative
	 * @param delta The amount of time since the last update (acquired from getDelta every update)
	 */
	protected void accelerateYNeg(int delta) {
		if(ySpeed > -maxYSpeed){
			float newSpeed = ySpeed - (delta * yAccel);
			if(newSpeed < -maxYSpeed)
				ySpeed = -maxYSpeed;
			else
				ySpeed = newSpeed;
		}
	}
	
	/**
	 * Decelerates the entity in the opposite direction that it's going until it reaches 0
	 * @param delta
	 */
	protected void decelerateY(int delta){
		if (ySpeed > 0){
			float newSpeed = ySpeed - (delta * yDecel);
			if(newSpeed < 0)
				ySpeed = 0;
			else
				ySpeed = newSpeed;
		} else if(ySpeed < 0){
			float newSpeed = ySpeed + (delta * yDecel);
			if(newSpeed > 0)
				ySpeed = 0;
			else
				ySpeed = newSpeed;
		}
		// else zSpeed is zero and nothing needs to be done
	}

	/**
	 * This should be called for any entity that moves around. The amount passed
	 * into <code>move</code> should be multiplied by this.
	 * 
	 * @return The time passed since the last frame
	 */
	protected int getDelta() {
		long time = Debug.getTime();
		int delta = (int) (time - lastUpdate);
		lastUpdate = time;

		return delta;
	}

	/**
	 * Updates this entity
	 */
	public abstract void update();

	/**
	 * Draws this entity
	 */
	public abstract void draw();
}
