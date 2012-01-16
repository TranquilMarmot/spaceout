package spaceout.entities.dynamic;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entity;
import spaceguts.interfaces.Bullet;
import spaceguts.physics.CollisionTypes;
import spaceguts.util.resources.Models;

/**
 * Bullet that just keeps going straight
 * 
 * @author TranquilMarmot
 * 
 */
public class LaserBullet extends DynamicEntity implements Bullet{
	final static short COL_GROUP = CollisionTypes.BULLET;
	final static short COL_WITH = (short)(CollisionTypes.WALL | CollisionTypes.PLANET);
	
	private int damage;
	private Entity owner;
	
	/** how long the bullet stays alive for */
	public float life = 10.0f;

	/** how long the bullet has been alive */
	public float timeAlive = 0.0f;

	public LaserBullet(Entity owner, Vector3f location, Quaternion rotation, Models model,
			float mass, float restitution, int damage) {
		super(location, rotation, model, mass, restitution);
		this.type = "Bullet";
		this.damage = damage;
		
		this.rigidBody.setCcdMotionThreshold(5.0f);
		
		this.owner = owner;
	}

	@Override
	public void update(float timeStep) {
		// add the delta to the time alive
		timeAlive += timeStep;

		// if the time is up, remove and delete this bullet
		if (timeAlive >= life) {
			removeFlag = true;
		}
	}

	@Override
	public int getDamage() {
		return damage;
	}
	
	@Override
	public Entity getOwner(){
		return owner;
	}
}
