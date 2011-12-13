package physics.sandbox;

import entities.Entities;
import graphics.model.Model;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.debug.Debug;

public class DynamicBullet extends DynamicEntity {
	public float life = 1000.0f;
	public float timeAlive = 0.0f;
	private long lastUpdate;
	private javax.vecmath.Vector3f currentSpeed = new javax.vecmath.Vector3f();

	public DynamicBullet(Vector3f location, Quaternion rotation, int model,
			float mass, float restitution) {
		super(location, rotation, model, mass, restitution);
		lastUpdate = Debug.getTime();
	}

	public DynamicBullet(Vector3f location, Quaternion rotation, Model model,
			float mass, float restitution) {
		super(location, rotation, model, mass, restitution);
		lastUpdate = Debug.getTime();
	}

	@Override
	public void update() {
		super.update();
		rigidBody.getLinearVelocity(currentSpeed);
		
		float delta = (float)getDelta() / 10.0f;
		
		timeAlive += delta;
		
		if(timeAlive >= life){
			this.cleanup();
			Entities.removeBuffer.add(this);
		}
	}
	
	private int getDelta(){
		long time = Debug.getTime();
		int delta = (int)(time - lastUpdate);
		lastUpdate = time;
		
		return delta;
	}
}
