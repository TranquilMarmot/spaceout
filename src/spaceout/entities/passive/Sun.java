package spaceout.entities.passive;

import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.Light;
import spaceguts.graphics.shapes.VBOQuadric;
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
	
	private Textures texture = Textures.SUN;
	private VBOQuadric sphere;

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
	public Sun(Vector3f location, float size,
			Vector3f intensity) {
		super(location, intensity);
		sphere = new VBOQuadric(size, 15, 15);
		this.type = "sun";
	}

	@Override
	public void draw() {
		texture.texture().bind();
		sphere.draw();
	}

	@Override
	public void update() {
	}
}
