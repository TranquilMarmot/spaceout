package spaceout.entities.dynamic;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.physics.CollisionTypes;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;

import spaceguts.entities.DynamicEntity;
import spaceguts.graphics.model.Model;
import spaceout.Health;

/**
 * Pretty much just a dynamic entity that's represented by a sphere
 * @author TranquilMarmot
 *
 */
public class Planet extends DynamicEntity implements Health{
	final static short COL_GROUP = CollisionTypes.PLANET;
	final static short COL_WITH = (short)(CollisionTypes.SHIP | CollisionTypes.WALL | CollisionTypes.PLANET);
	
	//FIXME planets shouldnt really have health this is for shits and giggles
	int health = 100;
	
	public Planet(Vector3f location, Quaternion rotation, float size,
			float mass, float restitution, int texture) {
		super(location, rotation, makeModel(size, texture), mass, restitution, COL_GROUP, COL_WITH);
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.015f, 0.0f));
		
		this.type = "Planet";
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
			drawSphere.draw(size, 10, 10);
		}GL11.glEndList();
		
		// make the model
		return new Model(sphereShape, sphereCallList, texture);
	}
	
	@Override
	public void draw(){
			GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
			super.draw();
			GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
	}

	@Override
	public int getCurrentHealth() {
		// TODO Auto-generated method stub
		return health;
	}

	@Override
	public void hurt(int amount) {
		health -= amount;
		if(health <= 0)
			removeFlag = true;
		
	}

	@Override
	public void heal(int amount) {
		health += amount;
	}
}
