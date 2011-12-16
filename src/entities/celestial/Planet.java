package entities.celestial;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;

import entities.dynamic.DynamicEntity;
import graphics.model.Model;

/**
 * Pretty much just a dynamic entity that's represented by a sphere
 * @author TranquilMarmot
 *
 */
public class Planet extends DynamicEntity{
	public Planet(Vector3f location, Quaternion rotation, float size,
			float mass, float restitution, int texture) {
		super(location, rotation, makeModel(size, texture), mass, restitution);
	}
	
	private static Model makeModel(float size, int texture){
		// use a sphere collision shape
		CollisionShape sphereShape = new SphereShape(size);
		
		// shpere to use to draw the sphere
		Sphere drawSphere = new Sphere();
		drawSphere.setNormals(GLU.GLU_SMOOTH);
		drawSphere.setTextureFlag(true);
		
		// create a call list for the sphere
		int sphereCallList = GL11.glGenLists(1);
		GL11.glNewList(sphereCallList, GL11.GL_COMPILE);{
			drawSphere.draw(size, 100, 100);
		}GL11.glEndList();
		
		// make the model
		return new Model(sphereShape, sphereCallList, texture);
	}
}
