package com.bitwaffle.spaceout.entities.dynamic;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.audio.SoundSource;
import com.bitwaffle.spaceguts.entities.Pickup;
import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceout.entities.player.Backpack;
import com.bitwaffle.spaceout.resources.Models;
import com.bitwaffle.spaceout.resources.Sounds;
import com.bulletphysics.collision.shapes.ConeShape;

/**
 * DIAMONDS!!!!
 * Diamonds stop once they're sent out (so they can be collected)
 * @author TranquilMarmot
 */
public class Diamond extends Pickup{
	private static final Models MODEL = Models.DIAMOND;
	private static final float MASS = 1.0f;
	private static final float RESTITUTION = 0.5f;
	private static final float CONE_RADIUS = 0.4f;
	private static final float CONE_HEIGHT =0.7f;
	final static short COL_GROUP = CollisionTypes.PICKUP;
	final static short COL_WITH = (short)(CollisionTypes.WALL | CollisionTypes.PLANET | CollisionTypes.SHIP | CollisionTypes.PICKUP);
	
	private float stopSpeed;
	
	private SoundSource beep;
	
	private boolean soundPlayed = false;

	public Diamond(Vector3f location, Quaternion rotation, float stopSpeed) {
		super(location, rotation, new ConeShape(CONE_RADIUS, CONE_HEIGHT), MASS, RESTITUTION, COL_GROUP, COL_WITH);
		this.model = MODEL.getModel();
		this.type = "Diamond";
		this.stopSpeed = stopSpeed;
		
	}
	
	@Override
	public void update(float timeStep){
		super.update(timeStep);
		
		if(following == null)
			stop(timeStep);
	}
	
	/**
	 * Gracefully stops the player
	 */
	private void stop(float timeStep) {
		javax.vecmath.Vector3f linearVelocity = new javax.vecmath.Vector3f(
				0.0f, 0.0f, 0.0f);
		rigidBody.getLinearVelocity(linearVelocity);

		float stopX = linearVelocity.x - ((linearVelocity.x / stopSpeed) * timeStep);
		float stopY = linearVelocity.y - ((linearVelocity.y / stopSpeed) * timeStep);
		float stopZ = linearVelocity.z - ((linearVelocity.z / stopSpeed) * timeStep);

		rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(stopX, stopY,
				stopZ));
	}
	
	@Override
	public void pickup(Backpack backpack){
		super.pickup(backpack);
		
		if(!soundPlayed){
			javax.vecmath.Vector3f veloc = new javax.vecmath.Vector3f();
			this.rigidBody.getLinearVelocity(veloc);
			
			// create and play sound (setting the removeFlag to true immediately after playing a sound removes it after it's played once)
			beep = new SoundSource(Sounds.DIAMOND_PICKUP, false, this.location, new Vector3f(veloc.x, veloc.y, veloc.z));
			beep.playSound();
			beep.removeFlag = true;
			soundPlayed = true;
		}
	}
}
