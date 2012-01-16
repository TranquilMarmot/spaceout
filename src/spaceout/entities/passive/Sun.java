package spaceout.entities.passive;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.Light;
import spaceguts.util.resources.Textures;

/**
 * A sun that gives off light.
 * 
 * @author TranquilMarmot
 * 
 */
public class Sun extends Light {
	/** size of this sun */
	public float size;

	/** the color of this sun */
	private float[] color;

	/** call list to use to draw this sun */
	private int list;

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
		this.color = color;
		this.light = light;
		initList();
	}

	/**
	 * Initializes the call list for this sun
	 */
	private void initList() {
		Sphere sphere = new Sphere();
		sphere.setNormals(GLU.GLU_SMOOTH);
		sphere.setTextureFlag(false);

		list = GL11.glGenLists(1);
		GL11.glNewList(list, GL11.GL_COMPILE);
		{
			sphere.draw(size, 30, 30);
		}
		GL11.glEndList();
	}

	@Override
	public void draw() {
		// disable lighting to draw the sun, oh the irony
		GL11.glDisable(GL11.GL_LIGHTING);
		Textures.WHITE.getTexture().bind();
		GL11.glColor3f(color[0], color[1], color[2]);
		GL11.glCallList(list);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void update() {
	}
}
