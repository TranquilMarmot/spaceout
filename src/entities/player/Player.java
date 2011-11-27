package entities.player;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import util.Runner;
import util.debug.Debug;
import util.helper.QuaternionHelper;
import util.manager.KeyboardManager;
import util.manager.ModelManager;
import util.manager.MouseManager;
import util.manager.TextureManager;
import entities.Entities;
import entities.Entity;
import entities.weapons.bullets.LaserBullet;

/**
 * The player!
 * @author TranquilMarmot
 *
 */
public class Player extends Entity {
	/** the player's model */
	private int model = ModelManager.SHIP1;

	// prevent shooting too fast
	private boolean button0Down = false;

	/**
	 * Player constructor
	 */
	public Player() {
		super();
		type = "player";

		rotationBuffer = BufferUtils.createFloatBuffer(16);

		xSpeed = 0.0f;
		ySpeed = 0.0f;
		zSpeed = 0.0f;

		maxZSpeed = 75.0f;
		zAccel = 0.07f;
		zDecel = 0.04f;

		maxXSpeed = 75.0f;
		xAccel = 0.07f;
		xDecel = 0.04f;

		maxYSpeed = 75.0f;
		yAccel = 0.07f;
		yDecel = 0.04f;
	}

	@Override
	public void update() {
		/*
		 * this should be grabbed every frame by any entity that moves An
		 * entity's movement amount (speed) should be multiplied by this so that
		 * the movement isn't framerate dependent.
		 */
		int delta = getDelta();
		if (!Runner.paused) {
			boolean forward = KeyboardManager.forward;
			boolean backward = KeyboardManager.backward;

			boolean left = KeyboardManager.left;
			boolean right = KeyboardManager.right;

			// control forward and backward movement
			if (forward || backward) {
				if (forward) {
					accelerateZPos(delta);
				}
				if (backward) {
					accelerateZNeg(delta);
				}
			} else if (zSpeed != 0) {
				decelerateZ(delta);
			}

			moveZ(zSpeed);

			// control strafing left and right
			if (left || right) {
				if (left) {
					accelerateXPos(delta);
				}
				if (right) {
					accelerateXNeg(delta);
				}
			} else if (xSpeed != 0) {
				decelerateX(delta);
			}

			moveX(xSpeed);

			// handle going up/down
			boolean up = KeyboardManager.ascend;
			boolean down = KeyboardManager.descend;
			if (up || down) {
				if (up)
					accelerateYNeg(delta);
				if (down)
					accelerateYPos(delta);
			} else if (ySpeed != 0) {
				decelerateY(delta);
			}

			moveY(ySpeed);

			// apply any rotation changes
			if (!Entities.camera.vanityMode) {
				rotateX(MouseManager.dy);
				rotateY(MouseManager.dx);
			}

			// roll left/right
			boolean rollRight = KeyboardManager.rollRight;
			boolean rollLeft = KeyboardManager.rollLeft;
			if (rollRight)
				rotateZ(-delta / 10.0f);
			if (rollLeft)
				rotateZ(delta / 10.0f);

			// shoot bullets if the player clicked
			if (MouseManager.button0 && !button0Down
					&& !(Debug.consoleOn)) {
				LaserBullet bullet = new LaserBullet(this.location.x,
						this.location.y, this.location.z, this.rotation);
				Entities.addBuffer.add(bullet);
				button0Down = true;
			}

			if (!MouseManager.button0) {
				button0Down = false;
			}
		}
	}

	@Override
	public void draw() {
		TextureManager.getTexture(TextureManager.SHIP1).bind();
		GL11.glPushMatrix();
		{
			// apply reverse rotation (counteracts the camera rotation)
			Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
			rotation.negate(revQuat);
			QuaternionHelper.toFloatBuffer(revQuat, rotationBuffer);
			GL11.glMultMatrix(rotationBuffer);

			GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);

			// draw the model
			GL11.glCallList(ModelManager.getModel(model).getCallList());
		}
		GL11.glPopMatrix();
	}
}
