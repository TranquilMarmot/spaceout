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

public class Planet extends DynamicEntity{
	public Planet(Vector3f location, Quaternion rotation, float size,
			float mass, float restitution, int texture) {
		super(location, rotation, makeModel(size, texture, mass), mass, restitution);
	}
	
	private static Model makeModel(float size, int texture, float mass){
		CollisionShape sphereShape = new SphereShape(size);
		
		Sphere drawSphere = new Sphere();
		drawSphere.setNormals(GLU.GLU_SMOOTH);
		drawSphere.setTextureFlag(true);
		
		int sphereCallList = GL11.glGenLists(1);
		GL11.glNewList(sphereCallList, GL11.GL_COMPILE);{
			drawSphere.draw(size, 100, 100);
		}GL11.glEndList();
		
		//javax.vecmath.Vector3f fallInertia = new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f);
		//sphereShape.calculateLocalInertia(mass, fallInertia);
		
		return new Model(sphereShape, sphereCallList, texture);
	}
	
	

}
