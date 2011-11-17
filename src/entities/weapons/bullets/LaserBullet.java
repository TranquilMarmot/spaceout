package entities.weapons.bullets;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import util.Runner;
import util.debug.Debug;
import util.helper.ModelHandler;
import util.helper.QuaternionHelper;
import util.helper.TextureHandler;
import entities.Entities;
import entities.Entity;

public class LaserBullet extends Entity {
	private int model = ModelHandler.LASERBULLET;
	private int texture = TextureHandler.LASERBULLET;

	// how far the bullet has traveled
	private float traveled;

	// how far the bullet goes before it dies
	private static float life = 750.0f;

	public LaserBullet(float x, float y, float z, Quaternion direction) {
		super();
		this.location = new Vector3f(x, y, z);
		this.rotation = new Quaternion(direction.x, direction.y, direction.z,
				direction.w);

		this.zSpeed = 0.1f;

		this.type = "bullet";

		this.rotationBuffer = BufferUtils.createFloatBuffer(16);

		// bullet's rotation buffer only needs to be set once (rotation never
		// changes)
		Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		rotation.negate(revQuat);
		QuaternionHelper.toFloatBuffer(revQuat, rotationBuffer);

		this.lastUpdate = Debug.getTime();
	}

	@Override
	public void update() {
		int delta = getDelta();
		if (!Runner.paused) {
			float moveAmount = delta * zSpeed;

			if (moveAmount > 0)
				this.moveZ(moveAmount);
			else
				this.moveZ(zSpeed);

			traveled += moveAmount;
			if (traveled >= life)
				this.destroy();
		}
	}

	/**
	 * Destroys the bullet
	 */
	public void destroy() {
		Entities.removeBuffer.add(this);
	}

	@Override
	public void draw() {
		TextureHandler.getTexture(texture).bind();
		GL11.glPushMatrix();
		{
			GL11.glMultMatrix(rotationBuffer);
			GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
			GL11.glCallList(ModelHandler.getModel(model).getCallList());
		}
		GL11.glPopMatrix();
	}

}
