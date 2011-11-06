package entities.celestial;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import util.Runner;
import util.helper.QuaternionHelper;
import util.helper.TextureHandler;
import entities.Entity;

public class Planet extends Entity {

	protected Sphere sphere;
	public float[] color;
	public float size;

	public float dxrot = 0;
	public float dyrot = 0;
	public float dzrot = 0;

	public Planet(float x, float y, float z, int texture, String name) {
		super();
		type = name;
		this.texture = texture;

		sphere = new Sphere();
		sphere.setNormals(GLU.GLU_SMOOTH);
		sphere.setTextureFlag(true);

		this.location.x = x;
		this.location.y = y;
		this.location.z = z;

		rotationBuffer = BufferUtils.createFloatBuffer(16);
	}

	@Override
	public void update() {
		if (!Runner.paused) {
			rotateX(dxrot);
			rotateY(dyrot);
			rotateZ(dzrot);
		}
	}

	@Override
	public void draw() {
		GL11.glPushMatrix();
		{
			// bind the entity's texture before drawing
			TextureHandler.getTexture(texture).bind();
			QuaternionHelper.toFloatBuffer(rotation, rotationBuffer);
			GL11.glMultMatrix(rotationBuffer);

			// GL11.glColor3f(color[0], color[1], color[2]);
			TextureHandler.getTexture(texture).bind();

			GL11.glRotatef(90.0f, 1.0f, 1.0f, 0.0f);
			sphere.draw(size, 24, 24);
		}
		GL11.glPopMatrix();
	}

}
