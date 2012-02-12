package spaceout.entities.dynamic;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.DynamicEntity;
import spaceguts.graphics.render.Render3D;
import spaceguts.graphics.shapes.VBOQuadric;
import spaceguts.physics.CollisionTypes;
import spaceguts.physics.Physics;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.resources.Textures;
import spaceout.interfaces.Health;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * Pretty much just a dynamic entity that's represented by a sphere
 * @author TranquilMarmot
 *
 */
public class Planet extends DynamicEntity implements Health{
	final static short COL_GROUP = CollisionTypes.PLANET;
	final static short COL_WITH = (short)(CollisionTypes.SHIP | CollisionTypes.WALL | CollisionTypes.PLANET);
	
	private static SphereShape shape;
	
	//FIXME planets shouldnt really have health this is for shits and giggles
	int health = 100;
	
	private Textures texture;
	
	private VBOQuadric quadric;
	
	public Planet(Vector3f location, Quaternion rotation, float size,
			float mass, float restitution, Textures texture) {
		super(location, QuaternionHelper.rotate(rotation, new Vector3f(90.0f, 0.0f, 0.0f)), getShape(size), mass, restitution, COL_GROUP, COL_WITH);
		
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.015f, 0.0f));
		
		this.type = "Planet";
		this.texture = texture;
		
		quadric = new VBOQuadric(size, 10, 10);
	}
	
	@Override
	public void draw(){
		Render3D.useDefaultMaterial();
		texture.texture().bind();
		quadric.draw();
	}
	
	private static SphereShape getShape(float size){
		shape = new SphereShape(size);
		return shape;
	}
	
	@Override
	/**
	 * Draws the physics debug info for this entity. Should be called before
	 * rotations are applied.
	 */
	public void drawPhysicsDebug() {
		Transform worldTransform = new Transform();
		rigidBody.getWorldTransform(worldTransform);

		Physics.dynamicsWorld.debugDrawObject(worldTransform, shape,
				new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
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
