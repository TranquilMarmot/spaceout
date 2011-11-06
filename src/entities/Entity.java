package entities;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.console.Debug;
import util.helper.QuaternionHelper;
import util.helper.TextureHandler;

public abstract class Entity {
	// the entity's location
	public Vector3f location;

	// how fast the entity is moving
	public float xSpeed, ySpeed, zSpeed;
	
	public float maxXSpeed, maxYSpeed, maxZSpeed;
	
	public float xAccel, yAccel, zAccel;

	// rotation variables
	public Quaternion rotation;
	protected FloatBuffer rotationBuffer;

	// the type, used for lots of things
	public String type;

	// the entity's color
	public float[] color;

	public int texture = TextureHandler.WHITE;

	/** the last time that this entity was updated */
	private static long lastUpdate;

	public Entity() {
		location = new Vector3f(0.0f, 0.0f, 0.0f);
		rotation = new Quaternion(1.0f, 0.0f, 0.0f, 1.0f);
		type = "entity";
		color = new float[] { 1.0f, 1.0f, 1.0f };
		xSpeed = 0.0f;
		ySpeed = 0.0f;
		zSpeed = 0.0f;
	}

	public void moveX(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(1.0f, 0.0f, 0.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	public void moveY(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(0.0f, 1.0f, 0.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	public void moveZ(float amount) {
		Vector3f multi = QuaternionHelper.MulQuaternionVector(rotation,
				new Vector3f(0.0f, 0.0f, 1.0f));
		Vector3f multiMulti = new Vector3f(multi.x * amount, multi.y * amount,
				multi.z * amount);
		Vector3f.add(location, multiMulti, location);
	}

	public void rotateX(float amount) {
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(sinVal, 0.0f, 0.0f, cosVal);
		Quaternion.mul(rotation, rot, rotation);
	}

	public void rotateY(float amount) {
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(0.0f, sinVal, 0.0f, cosVal);
		Quaternion.mul(rotation, rot, rotation);
	}

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
			float newSpeed = zSpeed - (delta * zAccel);
			if(newSpeed < 0)
				zSpeed = 0;
			else
				zSpeed = newSpeed;
		} else if(zSpeed < 0){
			float newSpeed = zSpeed + (delta * zAccel);
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
			float newSpeed = xSpeed - (delta * xAccel);
			if(newSpeed < 0)
				xSpeed = 0;
			else
				xSpeed = newSpeed;
		} else if(xSpeed < 0){
			float newSpeed = xSpeed + (delta * xAccel);
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
			float newSpeed = ySpeed - (delta * yAccel);
			if(newSpeed < 0)
				ySpeed = 0;
			else
				ySpeed = newSpeed;
		} else if(ySpeed < 0){
			float newSpeed = ySpeed + (delta * yAccel);
			if(newSpeed > 0)
				ySpeed = 0;
			else
				ySpeed = newSpeed;
		}
		// else zSpeed is zero and nothing needs to be done
	}

	/**
	 * This should be called for any entity that moves around. The amount passed
	 * into move*() should be multiplied by this.
	 * 
	 * @return The time passed since the last frame
	 */
	protected static int getDelta() {
		long time = Debug.getTime();
		int delta = (int) (time - lastUpdate);
		lastUpdate = time;

		return delta;
	}

	public abstract void update();

	public abstract void draw();
}
