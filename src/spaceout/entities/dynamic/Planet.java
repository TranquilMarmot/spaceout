package spaceout.entities.dynamic;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.vecmath.Point2f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.DynamicEntity;
import spaceguts.graphics.glsl.GLSLModelBuilder;
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
	
	private Textures texture;
	
	//private int callList;
	private int vaoHandle, numIndices;
	
	public Planet(Vector3f location, Quaternion rotation, float size,
			float mass, float restitution, Textures texture) {
		super(location, rotation, new SphereShape(size), mass, restitution, COL_GROUP, COL_WITH);
		
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		rigidBody.setAngularVelocity(new javax.vecmath.Vector3f(0.0f, 0.015f, 0.0f));
		
		this.type = "Planet";
		this.texture = texture;
		
		//callList = getCallList(size);
		vaoHandle = buildModel(size, 100, 100);
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
	
	private int buildModel(float radius, int slices, int stacks){
		ArrayList<javax.vecmath.Vector3f> vertices = new ArrayList<javax.vecmath.Vector3f>();
		ArrayList<javax.vecmath.Vector3f> normals = new ArrayList<javax.vecmath.Vector3f>();
		ArrayList<Point2f> texCoords = new ArrayList<Point2f>();
		
		float rho, theta;
		float drho = (float)(Math.PI / (double)stacks);
		float dtheta = (float)(2.0 * Math.PI / (double)slices);
		
		float s;
		float ds = 1.0f / (float)slices;
		float dt = 1.0f / (float)stacks;
		float t = 1.0f;
		
		for(int i = 0; i < stacks; i++){
			rho = i * drho;
			s = 0.0f;
			for(int j = 0; j <= slices; j++){
				theta = (j == slices) ? 0.0f : j * dtheta;
				float x = (float)(-Math.sin((double)theta) * Math.sin((double)rho));
				float y = (float)(Math.cos((double)theta) * Math.sin((double)rho));
				float z = (float)(Math.cos((double)rho));
				
				normals.add(new javax.vecmath.Vector3f(x, y, z));
				texCoords.add(new Point2f(s, t));
				vertices.add(new javax.vecmath.Vector3f(x * radius, y * radius, z * radius));
				
				
				x = (float)(-Math.sin(theta) * Math.sin(rho + drho));
				y = (float)(Math.cos(theta) * Math.sin(rho * drho));
				z = (float)(Math.cos(rho + drho));
				
				normals.add(new javax.vecmath.Vector3f(x, y, z));
				texCoords.add(new Point2f(s, t - dt));
				s += ds;
				vertices.add(new javax.vecmath.Vector3f(x * radius, y * radius, z * radius));
			}
			t -= dt;
		}
		
		numIndices = vertices.size() * 3;
		
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
		FloatBuffer normBuffer = BufferUtils.createFloatBuffer(normals.size() * 3);
		FloatBuffer texBuffer = BufferUtils.createFloatBuffer(texCoords.size() * 2);
		
		for(int i = 0; i < vertices.size(); i++){
			javax.vecmath.Vector3f vert = vertices.get(i);
			javax.vecmath.Vector3f norm = normals.get(i);
			Point2f texCoord = texCoords.get(i);
			
			vertBuffer.put(vert.x);
			vertBuffer.put(vert.y);
			vertBuffer.put(vert.z);
			normBuffer.put(norm.x);
			normBuffer.put(norm.y);
			normBuffer.put(norm.z);
			texBuffer.put(texCoord.x);
			texBuffer.put(texCoord.y);
		}
		
		vertBuffer.rewind();
		normBuffer.rewind();
		texBuffer.rewind();
		
		int vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		IntBuffer vboHandles = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(vboHandles);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(2);
		
		return vaoHandle;
	}
	
	@Override
	public void draw(){
		GLSLRender3D.modelview.rotate(-90, new Vector3f(1.0f, 0.0f, 0.0f));
		texture.texture().bind();
		//GL11.glCallList(callList);
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numIndices);
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
