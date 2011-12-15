package entities.celestial;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;

import util.manager.TextureManager;
import entities.Entities;
import entities.Entity;
import entities.Light;

/**
 * A sun that gives off light.
 * 
 * @author TranquilMarmot
 * @see Entity
 * @see Light
 * 
 */
public class Sun extends Light {
	/** size of this sun */
	public float size;

	/** this sun's sphere */
	private Sphere sphere;

	/** the color of this sun */
	private float[] color;

	/**
	 * Sun constructor
	 * 
	 * @param location
	 *            Location of this sun
	 * @param size
	 *            Sun's size
	 * @param color
	 *            Color of sun
	 * @param lightAmbient
	 *            Ambient light
	 * @param lightDiffuse
	 *            Diffuse light
	 */
	public Sun(Vector3f location, float size, int light, float[] color,
			float[] lightAmbient, float[] lightDiffuse) {
		super(location, light, lightAmbient, lightDiffuse);

		this.size = size;

		this.type = "sun";

		sphere = new Sphere();
		sphere.setNormals(GLU.GLU_SMOOTH);
		sphere.setTextureFlag(false);

		this.color = color;

		this.light = light;
	}

	@Override
	public void draw() {
		// disable lighting to draw the sun, oh the irony
		GL11.glDisable(GL11.GL_LIGHTING);
		TextureManager.getTexture(TextureManager.WHITE).bind();

		GL11.glColor3f(color[0], color[1], color[2]);
		sphere.draw(size, 30, 30);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void update() {
	}
}
