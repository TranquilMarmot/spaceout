package physics.sandbox;

import java.util.Random;

import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import physics.Physics;
import util.manager.ModelManager;
import util.manager.TextureManager;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import entities.Camera;
import entities.Entities;
import entities.Skybox;
import entities.celestial.Planet;
import entities.celestial.Sun;
import entities.dynamic.DynamicEntity;
import entities.dynamic.Player;
import entities.particles.Debris;
import graphics.model.Model;

public class Sandbox {
	public static void createSandboxWorld(){
		/* BEGIN SUN */
		Vector3f sunLocation = new Vector3f(1500.0f, 1500.0f, -2.0f);
		float sunSize = 150.0f;
		int sunLight = GL11.GL_LIGHT1;
		float[] sunColor = { 1.0f, 1.0f, 0.3f };
		float[] sunAmbient = { 1.0f, 1.0f, 1.0f };
		float[] sunDiffuse = { 1.0f, 1.0f, 1.0f };
		Sun sun = new Sun(sunLocation, sunSize, sunLight, sunColor, sunAmbient, sunDiffuse);
		Entities.lights.add(sun);
		/* END SUN */
		
		/* BEGIN BOX */
		//Vector3f box0Location = new Vector3f(0.0f, -200.0f, 0.0f);
		//Vector3f box0Size = new Vector3f(500.0f, 1.00f, 500.0f);
		//createBox(box0Location, box0Size);
		/* END BOX */
		
		/* BEGIN BOX */
		//Vector3f box1Location = new Vector3f(0.0f, 200.0f, 0.0f);
		//Vector3f box1Size = new Vector3f(500.0f, 1.00f, 500.0f);
		//createBox(box1Location, box1Size);
		/* END BOX */
		
		/* BEGIN PLAYER */
		Vector3f playerLocation = new Vector3f(-107.111f, 198.284f, -65900.311f);
		Quaternion playerRotation = new Quaternion(0.002583359f, -0.0559893f, 0.9984302f, 0.00012266426f);
		float playerMass = 50.0f;
		float playerRestitution = 0.01f;
		Player player = new Player(playerLocation, playerRotation, ModelManager.WING_X, playerMass, playerRestitution);
		player.type = "dynamicPlayer";
		Entities.player = player;
		
		//addRandomSphere();
		
		Camera.createCamera();
		Entities.camera.following = player;
		
		/* END PLAYER */
		
		/* BEGIN SKYBOX  */
		Skybox skybox = new Skybox(Entities.camera);
		Entities.skybox = skybox;
		/* END SKYBOX */
		
		/* BEGIN DEBRIS */
		Debris debris = new Debris(Entities.camera, 500, 50000.0f, 420420L);
		debris.update();
		Entities.staticEntities.add(debris);
		/* END DEBRIS */
	}
	
	public static void createBox(Vector3f location, Vector3f size){
		/* BEGIN GROUND */
		float boxXSize = size.x;
		float boxYSize = size.y;
		float boxZSize = size.z;
		
		CollisionShape groundShape = new BoxShape(
				new javax.vecmath.Vector3f(boxXSize, boxYSize, boxZSize));

		int groundCallList = GL11.glGenLists(1);
		GL11.glNewList(groundCallList, GL11.GL_COMPILE);
		{
			GL11.glBegin(GL11.GL_QUADS);
			{
			    // Bottom Face
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-boxXSize, -boxYSize, -boxZSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( boxXSize, -boxYSize, -boxZSize);  // Top Left Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( boxXSize, -boxYSize,  boxZSize);  // Bottom Left Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-boxXSize, -boxYSize,  boxZSize);  // Bottom Right Of The Texture and Quad
			    // Front Face
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-boxXSize, -boxYSize,  boxZSize);  // Bottom Left Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( boxXSize, -boxYSize,  boxZSize);  // Bottom Right Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( boxXSize,  boxYSize,  boxZSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-boxXSize,  boxYSize,  boxZSize);  // Top Left Of The Texture and Quad
			    // Back Face
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-boxXSize, -boxYSize, -boxZSize);  // Bottom Right Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-boxXSize,  boxYSize, -boxZSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( boxXSize,  boxYSize, -boxZSize);  // Top Left Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( boxXSize, -boxYSize, -boxZSize);  // Bottom Left Of The Texture and Quad
			    // Right face
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( boxXSize, -boxYSize, -boxZSize);  // Bottom Right Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( boxXSize,  boxYSize, -boxZSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( boxXSize,  boxYSize,  boxZSize);  // Top Left Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( boxXSize, -boxYSize,  boxZSize);  // Bottom Left Of The Texture and Quad
			    // Left Face
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-boxXSize, -boxYSize, -boxZSize);  // Bottom Left Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-boxXSize, -boxYSize,  boxZSize);  // Bottom Right Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-boxXSize,  boxYSize,  boxZSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-boxXSize,  boxYSize, -boxZSize);  // Top Left Of The Texture and Quad
			    
			    // Top Face
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-boxXSize, boxYSize, -boxZSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( boxXSize, boxYSize, -boxZSize);  // Top Left Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( boxXSize, boxYSize,  boxZSize);  // Bottom Left Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-boxXSize, boxYSize,  boxZSize);  // Bottom Right Of The Texture and Quad
			    

			}
			GL11.glEnd();
		}
		GL11.glEndList();
		
		int groundTexture = TextureManager.CHECKERS;
		
		Model groundModel = new Model(groundShape, groundCallList, groundTexture);
		
		Vector3f groundLocation = new Vector3f(location.x, location.y, location.z);
		Quaternion groundRotation = new Quaternion(0.0f, 0.0f, -0.0f, 1.0f);
		
		DynamicEntity ground = new DynamicEntity(groundLocation, groundRotation, groundModel, 0.0f, 0.0f);
		ground.type = "Ground";
		ground.location = location;
		Entities.dynamicEntities.add(ground);
		/* END GROUND */
	}
	
	public static void addRandomSphere(){
		Random randy = new Random();
		//float sphereSize = randy.nextInt(250) / 10.0f;
		float sphereSize = 637.1f;
		//CollisionShape sphereShape = new SphereShape(sphereSize);
		
		Sphere drawSphere = new Sphere();
		drawSphere.setNormals(GLU.GLU_SMOOTH);
		drawSphere.setTextureFlag(true);
		
		int sphereCallList = GL11.glGenLists(1);
		GL11.glNewList(sphereCallList, GL11.GL_COMPILE);{
			drawSphere.draw(sphereSize, 100, 100);
		}GL11.glEndList();
		
		//int sphereTexture = TextureManager.EARTH;
		/*
		switch(randy.nextInt(4)){
		case 0:
			sphereTexture = TextureManager.EARTH;
			break;
		case 1:
			sphereTexture = TextureManager.MERCURY;
			break;
		case 2:
			sphereTexture = TextureManager.VENUS;
			break;
		case 3:
			sphereTexture = TextureManager.MARS;
			break;
		default:
			sphereTexture = TextureManager.EARTH;
		}
		*/
		
		//Model sphereModel = new Model(sphereShape, sphereCallList, sphereTexture);
		
		Vector3f sphereLocation = new Vector3f(0.0f + (randy.nextFloat() * 1000.0f), 0.0f + (randy.nextFloat() * 1000.0f), 0.0f + (randy.nextFloat() * 1000.0f));
		Quaternion sphereRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		
		//float sphereMass = randy.nextFloat() * 10000000.0f;
		float sphereMass = 597360000000000000000.0f;
		
		//javax.vecmath.Vector3f fallInertia = new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f);
		//sphereShape.calculateLocalInertia(sphereMass, fallInertia);
		
		//DynamicEntity sphere = new DynamicEntity(sphereLocation, sphereRotation, sphereModel, sphereMass, 0.01f);
		//sphere.type = "Sphere";
		
		Planet p = new Planet(sphereLocation, sphereRotation, sphereSize, sphereMass, 0.01f, TextureManager.EARTH);
		
		Entities.dynamicEntities.add(p);
	}
	
	public static void helloWorld(){
		/* BEGIN TEST SHAPES */
		// CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1,
		// 0), 1);
		CollisionShape groundShape = new BoxShape(new javax.vecmath.Vector3f(50.0f, 50.0f,
				50.0f));

		CollisionShape fallShape = new SphereShape(1);

		Transform groundTransform = new Transform();
		groundTransform.setRotation(new Quat4f(0.0f, 0.0f, 0.0f, 1.0f));
		groundTransform.origin.set(0, -56, 0);

		DefaultMotionState groundMotionState = new DefaultMotionState(
				groundTransform);

		// first variable here is mass
		RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(
				0, groundMotionState, groundShape, new javax.vecmath.Vector3f(0, 0, 0));
		RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);

		Physics.dynamicsWorld.addRigidBody(groundRigidBody);

		Transform fallTransform = new Transform();
		fallTransform.setRotation(new Quat4f(0.0f, 0.0f, 0.0f, 1.0f));
		fallTransform.origin.set(0.0f, 50.0f, 0.0f);

		DefaultMotionState fallMotionState = new DefaultMotionState(
				fallTransform);

		float mass = 1.0f;
		javax.vecmath.Vector3f fallInertia = new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f);
		fallShape.calculateLocalInertia(mass, fallInertia);

		RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(
				mass, fallMotionState, fallShape, fallInertia);
		RigidBody fallRigidBody = new RigidBody(fallRigidBodyCI);
		Physics.dynamicsWorld.addRigidBody(fallRigidBody);

		/* END TEST SHAPES */

		for (int i = 0; i < 300; i++) {
			Physics.dynamicsWorld.stepSimulation(1.0f / 60.0f, 10);

			Transform trans = new Transform();
			fallRigidBody.getMotionState().getWorldTransform(trans);

			System.out.println("Sphere height: " + trans.origin.y);
		}
	}
}
