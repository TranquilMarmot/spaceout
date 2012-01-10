package spaceguts.physics.sandbox;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.util.manager.ModelManager;
import spaceguts.util.manager.TextureManager;
import spaceguts.entities.Entities;
import spaceguts.entities.Entity;
import spaceguts.entities.dynamic.Box;
import spaceguts.entities.dynamic.Planet;
import spaceguts.entities.dynamic.Player;
import spaceguts.entities.light.Sun;
import spaceguts.entities.passive.Camera;
import spaceguts.entities.passive.Skybox;
import spaceguts.entities.passive.particles.Debris;

/**
 * Sandbox class for physics testing and fun
 * @author TranquilMarmot
 *
 */
public class Sandbox extends Entity{
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
		Vector3f box0Location = new Vector3f(0.0f, -200.0f, 0.0f);
		Quaternion box0Rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Vector3f box0Size = new Vector3f(500.0f, 1.00f, 500.0f);
		float box0Mass = 0.0f;
		float box0Restitution = 0.0f;
		Box box0 = new Box(box0Location, box0Rotation, box0Size, box0Mass, box0Restitution);
		Entities.dynamicEntities.add(box0);
		/* END BOX */
		
		/* BEGIN BOX */
		Vector3f box1Location = new Vector3f(0.0f, 200.0f, 0.0f);
		Quaternion box1Rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Vector3f box1Size = new Vector3f(500.0f, 1.00f, 500.0f);
		float box1Mass = 0.0f;
		float box1Restitution = 0.0f;
		Box box1 = new Box(box1Location, box1Rotation, box1Size, box1Mass, box1Restitution);
		Entities.dynamicEntities.add(box1);
		/* END BOX */
		
		/* BEGIN PLAYER */
		Vector3f playerLocation = new Vector3f(50.0f, 50.0f, 1000.0f);
		Quaternion playerRotation = new Quaternion(-0.0058384943f, 0.9989326f, -0.045716275f, -0.0030980944f);
		float playerMass = 50.0f;
		float playerRestitution = 0.01f;
		Player player = new Player(playerLocation, playerRotation, ModelManager.WING_X, playerMass, playerRestitution);
		player.type = "dynamicPlayer";
		Entities.player = player;
		
		Camera.createCamera();
		Entities.camera.following = player;
		
		/* END PLAYER */
		
		/* BEGIN SKYBOX  */
		Skybox skybox = new Skybox(Entities.camera);
		Entities.skybox = skybox;
		/* END SKYBOX */
		
		/* BEGIN DEBRIS */
		Debris debris = new Debris(Entities.camera, 500, 50000.0f, 420133742L);
		debris.update();
		Entities.staticEntities.add(debris);
		/* END DEBRIS */
	}
	
	private static void addRandomSphere(){
		Random randy = new Random();
		float sphereSize = randy.nextInt(200) / 10.0f;
		
		int sphereTexture = randy.nextInt(4);
		
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
		
		float sphereX = 0.0f + (randy.nextFloat() * 100.0f);
		float sphereY = 0.0f + (randy.nextFloat() * 100.0f);
		float sphereZ = 0.0f + (randy.nextFloat() * 100.0f);
		Vector3f sphereLocation = new Vector3f(sphereX, sphereY, sphereZ);
		Quaternion sphereRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		
		//float sphereMass = randy.nextFloat() * 10.0f;
		float sphereMass = sphereSize * 2.0f;
		
		Planet p = new Planet(sphereLocation, sphereRotation, sphereSize, sphereMass, 0.0f, sphereTexture);
		
		Entities.dynamicEntities.add(p);
	}

	@Override
	public void update() {
		if(Keyboard.isKeyDown(Keyboard.KEY_P))
			addRandomSphere();
	}

	@Override
	public void draw() {
	}

	@Override
	public void cleanup() {
	}
}
