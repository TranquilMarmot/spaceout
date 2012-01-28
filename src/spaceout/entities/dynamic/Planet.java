package spaceout.entities.dynamic;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.DynamicEntity;
import spaceguts.graphics.glsl.GLSLRender3D;
import spaceguts.interfaces.Health;
import spaceguts.physics.CollisionTypes;
import spaceguts.util.resources.Textures;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.SphereShape;

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
	
	private int callList;
	
	public Planet(Vector3f location, Quaternion rotation, float size,
			float mass, float restitution, Textures texture) {
		super(location, rotation, new SphereShape(size), mass, restitution, COL_GROUP, COL_WITH);
		
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.015f, 0.0f));
		
		this.type = "Planet";
		
		callList = getCallList(size);
	}
	
	private static int getCallList(float size){
		// shpere to use to draw the sphere
		Sphere drawSphere = new Sphere();
		drawSphere.setNormals(GLU.GLU_SMOOTH);
		drawSphere.setTextureFlag(true);
		
		// create a call list for the sphere
		int sphereCallList = GL11.glGenLists(1);
		GL11.glNewList(sphereCallList, GL11.GL_COMPILE);{
			drawSphere.draw(size, 10, 10);
		}GL11.glEndList();
		
		return sphereCallList;
	}
	
	@Override
	public void draw(){
		GLSLRender3D.modelview.rotate(-90, new Vector3f(1.0f, 0.0f, 0.0f));
		GL11.glCallList(callList);
		GLSLRender3D.modelview.rotate(90, new Vector3f(1.0f, 0.0f, 0.0f));
	}

	@Override
	public int getCurrentHealth() {
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
