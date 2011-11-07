package entities.weapons.bullets;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.Runner;
import util.helper.ModelHandler;

import entities.Entities;
import entities.Entity;

public class LaserBullet extends Entity{
	private int model = ModelHandler.LASERBULLET;
	
	// how far the bullet has traveled
	private float traveled;
	
	// how far the bullet goes before it dies
	private static float life = 30.0f;
	
	public LaserBullet(float x, float y, float z, Quaternion direction){
		super();
		this.location = new Vector3f(x, y, z);
		this.rotation = direction;
		
		this.zSpeed = 1.0f;
	}

	@Override
	public void update() {
		int delta = getDelta();
		if(!Runner.paused){
			float moveAmount = delta * zSpeed;
			this.moveZ(moveAmount);
			traveled += moveAmount;
			//if(traveled >= life)
				//this.destroy();
		}
	}
	
	/**
	 * Destroys the bullet
	 */
	public void destroy(){
		Entities.entities.remove(this);
	}

	@Override
	public void draw() {
		GL11.glCallList(ModelHandler.getCallList(model));
	}

}
