package entities.dynamic;

import entities.Entities;
import graphics.model.Model;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.debug.Debug;

/**
 * Bullet that just keeps going straight
 * 
 * @author TranquilMarmot
 * 
 */
public class LaserBullet extends DynamicEntity {
	/** how long the bullet stays alive for */
	public float life = 1000.0f;

	/** how long the bullet has been alive */
	public float timeAlive = 0.0f;

	/**
	 * the last time the bullet was updated, to keep its time alive framerate
	 * independent
	 */
	private long lastUpdate;

	public LaserBullet(Vector3f location, Quaternion rotation, int model,
			float mass, float restitution) {
		super(location, rotation, model, mass, restitution);
		lastUpdate = Debug.getTime();
	}

	public LaserBullet(Vector3f location, Quaternion rotation, Model model,
			float mass, float restitution) {
		super(location, rotation, model, mass, restitution);
		lastUpdate = Debug.getTime();
	}

	@Override
	public void update() {
		super.update();

		// add the delta to the time alive
		float delta = (float) getDelta() / 10.0f;
		timeAlive += delta;

		// if the time is up, remove and delete this bullet
		if (timeAlive >= life) {
			this.cleanup();
			Entities.removeBuffer.add(this);
		}
	}

	/**
	 * @return Amount of time passed since the last update
	 */
	private int getDelta() {
		long time = Debug.getTime();
		int delta = (int) (time - lastUpdate);
		lastUpdate = time;

		return delta;
	}
}
