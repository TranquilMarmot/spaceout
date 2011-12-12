package physics.sandbox;

import java.util.Random;

import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import physics.Physics;
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
import entities.celestial.Sun;
import entities.particles.Debris;
import entities.player.Player;
import graphics.model.Model;
import graphics.model.ModelLoader;
import gui.GUI;
import gui.menu.PauseMenu;

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
		Vector3f box0Location = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f box0Size = new Vector3f(1000.0f, 0.001f, 1000.0f);
		createBox(box0Location, box0Size);
		/* END BOX */
		
		/* BEGIN BOX */
		Vector3f box1Location = new Vector3f(0.0f, 600.0f, 0.0f);
		Vector3f box1Size = new Vector3f(1000.0f, 0.001f, 1000.0f);
		createBox(box1Location, box1Size);
		/* END BOX */
		
		/* BEGIN PLAYER */
		//Entities.player = new Player();
		//Entities.player.location = new Vector3f(-46.766f, 229.257f, -2304.809f);
		//Entities.player.rotation = new Quaternion(0.002583359f, -0.0559893f, 0.9984302f, 0.00012266426f);
		
		Vector3f playerLocation = new Vector3f(-107.111f, 198.284f, -659.311f);
		Quaternion playerRotation = new Quaternion(0.002583359f, -0.0559893f, 0.9984302f, 0.00012266426f);
		Model playerModel = ModelLoader.loadObjFile("res/models/ships/ship1.obj", 1.0f, TextureManager.SHIP1);
		float playerMass = 100.0f;
		float playerRestitution = 1.0f;
		DynamicEntity player = new DynamicPlayer(playerLocation, playerRotation, playerModel, playerMass, playerRestitution);
		player.type = "dynamicPlayer";
		Entities.player = player;
		
		/* END PLAYER */
		
		
		/* BEGIN CAMERA */
		// initialize the camera
		Entities.camera = new Camera(Entities.player.location.x, Entities.player.location.y, Entities.player.location.z);
		Entities.camera.zoom = 30.0f;
		Entities.camera.yOffset = -2.5f;
		Entities.camera.xOffset = 0.1f;
		Entities.camera.following = Entities.player;
		Entities.camera.rotation = new Quaternion(0.087611444f, -0.12701073f, 0.9879578f, 0.011701694f);
		Entities.camera.vanityMode = false;
		
		
		GUI.menuUp = false;
		/* END CAMERA */
		
		/* BEGIN SKYBOX  */
		Skybox skybox = new Skybox(Entities.player, 0, 0, 0);
		Entities.entities.add(skybox);
		/* END SKYBOX */
		
		/* BEGIN DEBRIS */
		Debris debris = new Debris(Entities.player, 1000, 100000.0f, 420420L);
		debris.update();
		Entities.entities.add(debris);
		/* END DEBRIS */
		
		PauseMenu pmenu = new PauseMenu();
		GUI.addBuffer.add(pmenu);
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
			    
			    // Bottom Face
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
		
		DynamicEntity ground = new DynamicEntity(groundLocation, groundRotation, groundModel, 0.0f, 1.0f);
		ground.type = "Ground";
		ground.location = location;
		Entities.entities.add(ground);
		/* END GROUND */
	}
	
	public static void addRandomSphere(){
		Random randy = new Random();
		float sphereSize = randy.nextInt(250) / 10.0f;
		CollisionShape sphereShape = new SphereShape(sphereSize);
		
		Sphere drawSphere = new Sphere();
		drawSphere.setNormals(GLU.GLU_SMOOTH);
		drawSphere.setTextureFlag(true);
		
		int sphereCallList = GL11.glGenLists(1);
		GL11.glNewList(sphereCallList, GL11.GL_COMPILE);{
			drawSphere.draw(sphereSize, 15, 15);
		}GL11.glEndList();
		
		int sphereTexture;
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
		
		Model sphereModel = new Model(sphereShape, sphereCallList, sphereTexture);
		
		Vector3f sphereLocation = new Vector3f(0.0f + (randy.nextFloat() * 10.0f), 120.0f + (randy.nextFloat() * 10.0f), 0.0f + (randy.nextFloat() * 10.0f));
		Quaternion sphereRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		
		float sphereMass = randy.nextFloat() * 100;
		
		javax.vecmath.Vector3f fallInertia = new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f);
		sphereShape.calculateLocalInertia(sphereMass, fallInertia);
		
		DynamicEntity sphere = new DynamicEntity(sphereLocation, sphereRotation, sphereModel, sphereMass, randy.nextFloat());
		sphere.type = "Sphere";
		Entities.entities.add(sphere);
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
