package entities.celestial;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import util.Runner;
import util.helper.QuaternionHelper;
import util.manager.TextureManager;
import entities.Entity;

/**
 * A planet
 * 
 * @author TranquilMarmot
 * @see Entity
 */
public class Planet extends Entity {
	// this planet's sphere
	private Sphere sphere;
	// this planet's size
	public float size;
	
	public int texture;

	// planet's rotation variables
	public float dxrot = 0;
	public float dyrot = 0;
	public float dzrot = 0;

	/**
	 * Planet constructor
	 * 
	 * @param x
	 *            X location
	 * @param y
	 *            Y location
	 * @param z
	 *            Z location
	 * @param texture
	 *            Texture to use
	 * @param name
	 *            The planet's name
	 */
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
			TextureManager.getTexture(texture).bind();
			QuaternionHelper.toFloatBuffer(rotation, rotationBuffer);
			GL11.glMultMatrix(rotationBuffer);

			// GL11.glColor3f(color[0], color[1], color[2]);
			TextureManager.getTexture(texture).bind();

			GL11.glRotatef(90.0f, 1.0f, 1.0f, 0.0f);
			sphere.draw(size, 24, 24);
		}
		GL11.glPopMatrix();
	}
	
	@Override
	public void cleanup(){
		
	}
}
