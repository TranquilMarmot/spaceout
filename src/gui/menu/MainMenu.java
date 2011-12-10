package gui.menu;

import entities.Camera;
import entities.Entities;
import entities.celestial.Sun;
import graphics.model.Model;
import gui.GUI;
import gui.GUIObject;
import gui.button.MenuButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import physics.testShapes.DynamicEntity;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;

import util.Runner;
import util.debug.Debug;
import util.helper.DisplayHelper;
import util.manager.TextureManager;

/**
 * Main menu to show when the game first runs
 * 
 * @author TranquilMarmot
 * 
 */
public class MainMenu extends GUIObject {
	/** button image path */
	private static final String BUTTON_IMAGE_PATH = "res/images/gui/Menu/Button/";

	private static final int spaceoutYOffset = -200;
	private static final int spaceoutScale = 2;

	/** XML path */
	private static final String XML_PATH = "res/XML/";

	/** whether or not we're done with the main menu */
	public static boolean done;

	/** button to press to start the game */
	private MenuButton loadMenuButton;

	/** button to quit */
	private MenuButton quitButton;

	private Texture background;
	private Texture spaceout;

	/**
	 * Main menu constructor. Creates a startButton that loads from an XML file.
	 */
	public MainMenu() {
		super(0, 0);

		// initialize the background images
		initImages();

		// grab the background
		background = TextureManager.getTexture(TextureManager.BACKGROUND1);

		done = false;

		// create the button to go to the load menu
		loadMenuButton = new MenuButton(BUTTON_IMAGE_PATH, "Start Sim", 238,
				55, 0, -40);
		loadMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/* BEGIN PHYSICS DEBUG WORLD CREATION */
				/* BEGIN SUN */
				Vector3f sunLocation = new Vector3f(0.0f, -1000.0f, 1000.0f);
				float sunSize = 10.0f;
				int sunLight = GL11.GL_LIGHT1;
				float[] sunColor = { 1.0f, 1.0f, 0.3f };
				float[] sunAmbient = { 1.0f, 1.0f, 1.0f };
				float[] sunDiffuse = { 1.0f, 1.0f, 1.0f };
				Sun sun = new Sun(sunLocation, sunSize, sunLight, sunColor, sunAmbient, sunDiffuse);
				Entities.lights.add(sun);
				/* END SUN */
				
				
				/* BEGIN GROUND */
				CollisionShape groundShape = new BoxShape(
						new javax.vecmath.Vector3f(50.0f, 50.0f, 50.0f));

				int groundCallList = GL11.glGenLists(1);
				GL11.glNewList(groundCallList, GL11.GL_COMPILE);
				{
					GL11.glTranslatef(0.0f, 0.75f, 0.0f);
					GL11.glBegin(GL11.GL_QUADS);
					{
					    // Bottom Face
					    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-50.0f, -50.0f, -50.0f);  // Top Right Of The Texture and Quad
					    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( 50.0f, -50.0f, -50.0f);  // Top Left Of The Texture and Quad
					    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( 50.0f, -50.0f,  50.0f);  // Bottom Left Of The Texture and Quad
					    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-50.0f, -50.0f,  50.0f);  // Bottom Right Of The Texture and Quad
					    // Front Face
					    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-50.0f, -50.0f,  50.0f);  // Bottom Left Of The Texture and Quad
					    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( 50.0f, -50.0f,  50.0f);  // Bottom Right Of The Texture and Quad
					    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( 50.0f,  50.0f,  50.0f);  // Top Right Of The Texture and Quad
					    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-50.0f,  50.0f,  50.0f);  // Top Left Of The Texture and Quad
					    // Back Face
					    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-50.0f, -50.0f, -50.0f);  // Bottom Right Of The Texture and Quad
					    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-50.0f,  50.0f, -50.0f);  // Top Right Of The Texture and Quad
					    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( 50.0f,  50.0f, -50.0f);  // Top Left Of The Texture and Quad
					    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( 50.0f, -50.0f, -50.0f);  // Bottom Left Of The Texture and Quad
					    // Right face
					    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( 50.0f, -50.0f, -50.0f);  // Bottom Right Of The Texture and Quad
					    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( 50.0f,  50.0f, -50.0f);  // Top Right Of The Texture and Quad
					    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( 50.0f,  50.0f,  50.0f);  // Top Left Of The Texture and Quad
					    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( 50.0f, -50.0f,  50.0f);  // Bottom Left Of The Texture and Quad
					    // Left Face
					    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-50.0f, -50.0f, -50.0f);  // Bottom Left Of The Texture and Quad
					    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-50.0f, -50.0f,  50.0f);  // Bottom Right Of The Texture and Quad
					    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-50.0f,  50.0f,  50.0f);  // Top Right Of The Texture and Quad
					    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-50.0f,  50.0f, -50.0f);  // Top Left Of The Texture and Quad
					}
					GL11.glEnd();
				}
				GL11.glEndList();
				
				int groundTexture = TextureManager.WHITE;
				
				Model groundModel = new Model(groundShape, groundCallList, groundTexture);
				
				Vector3f groundLocation = new Vector3f(0.0f, 50.0f, 0.0f);
				Quaternion groundRotation = new Quaternion(0.0f, 0.0f, 0.05f, 1.0f);
				
				DynamicEntity ground = new DynamicEntity(groundLocation, groundRotation, groundModel, 0.0f);
				ground.type = "Ground";
				Entities.entities.add(ground);
				/* END GROUND */
				
				/* BEGIN SPHERE */
				CollisionShape sphereShape = new SphereShape(1);
				
				Sphere drawSphere = new Sphere();
				drawSphere.setNormals(GLU.GLU_SMOOTH);
				drawSphere.setTextureFlag(true);
				
				int sphereCallList = GL11.glGenLists(1);
				GL11.glNewList(sphereCallList, GL11.GL_COMPILE);{
					drawSphere.draw(1, 24, 24);
				}GL11.glEndList();
				
				int sphereTexture = TextureManager.EARTH;
				
				Model sphereModel = new Model(sphereShape, sphereCallList, sphereTexture);
				
				Vector3f sphereLocation = new Vector3f(0.0f, 120.0f, 0.0f);
				Quaternion sphereRotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
				
				float sphereMass = 1.0f;
				
				javax.vecmath.Vector3f fallInertia = new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f);
				sphereShape.calculateLocalInertia(sphereMass, fallInertia);
				
				DynamicEntity sphere = new DynamicEntity(sphereLocation, sphereRotation, sphereModel, sphereMass);
				sphere.type = "Sphere";
				Entities.entities.add(sphere);
				/* END SPHERE */
				
				/* BEGIN CAMERA */
				// initialize the camera
				Entities.camera = new Camera(sphere.location.x, sphere.location.y, sphere.location.z);
				Entities.camera.zoom = 100.0f;
				Entities.camera.yOffset = 0.0f;
				Entities.camera.xOffset = 0.0f;
				Entities.camera.following = sphere;
				Entities.camera.rotation = new Quaternion(0.98061955f, -0.042101286f, 0.0016744693f, -0.042101286f);
				Entities.camera.vanityMode = true;
				
				
				GUI.menuUp = false;
				/* END CAMERA */
				
				/* END PHYSICS DEBUG WORLD CREATION */

				// create the load menu
				// LoadMenu lmenu = new LoadMenu(0, 0, XML_PATH);
				// GUI.addBuffer.add(lmenu);

				// let the GUI know that a menu is up
				// GUI.menuUp = true;

				// done with the main menu
				done = true;
			}
		});

		// create the main menu quit button
		quitButton = new MenuButton(BUTTON_IMAGE_PATH, "Exit", 238, 55, 0, 50);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// end the game
				Runner.done = true;
			}
		});

		// let the GUI know that a menu is up
		GUI.menuUp = true;
	}

	private void initImages() {
		TextureManager.initTexture(TextureManager.BACKGROUND1);
		TextureManager.initTexture(TextureManager.BACKGROUND2);
		try {
			spaceout = TextureLoader.getTexture("PNG", new FileInputStream(
					"res/images/gui/Menu/spaceout.png"), GL11.GL_NEAREST);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		// update the buttons
		loadMenuButton.update();
		quitButton.update();

		// remove the main menu if we're done with it
		if (done) {
			GUI.removeBuffer.add(this);
		}
	}

	@Override
	public void draw() {
		// draw the background
		background.bind();
		GL11.glBegin(GL11.GL_QUADS);
		{

			/*
			 * currentImage.getWidth() and currentImage.getHeight() return the
			 * actual height of the texture. My best guess is that the image
			 * gets put into the smallest possible texture that has dimensions
			 * that are powers of 2 by Slick, because OpenGL can handle those
			 * much better.
			 */
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(0, 0);

			GL11.glTexCoord2f(background.getWidth(), 0);
			GL11.glVertex2i(DisplayHelper.windowWidth, 0);

			GL11.glTexCoord2f(background.getWidth(), background.getHeight());
			GL11.glVertex2i(DisplayHelper.windowWidth,
					DisplayHelper.windowHeight);

			GL11.glTexCoord2f(0, background.getHeight());
			GL11.glVertex2i(0, DisplayHelper.windowHeight);
		}
		GL11.glEnd();

		// draw 'spaceout'
		spaceout.bind();

		int x1 = (DisplayHelper.windowWidth / 2)
				- (spaceout.getImageWidth() * spaceoutScale);
		int y1 = (DisplayHelper.windowHeight / 2)
				- (spaceout.getImageHeight() * spaceoutScale) + spaceoutYOffset;

		int x2 = (DisplayHelper.windowWidth / 2)
				+ (spaceout.getImageWidth() * spaceoutScale);
		int y2 = (DisplayHelper.windowHeight / 2)
				- (spaceout.getImageHeight() * spaceoutScale) + spaceoutYOffset;

		int x3 = (DisplayHelper.windowWidth / 2)
				+ (spaceout.getImageWidth() * spaceoutScale);
		int y3 = (DisplayHelper.windowHeight / 2)
				+ (spaceout.getImageHeight() * spaceoutScale) + spaceoutYOffset;

		int x4 = (DisplayHelper.windowWidth / 2)
				- (spaceout.getImageWidth() * spaceoutScale);
		int y4 = (DisplayHelper.windowHeight / 2)
				+ (spaceout.getImageHeight() * spaceoutScale) + spaceoutYOffset;

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x1, y1);

			GL11.glTexCoord2f(spaceout.getWidth(), 0);
			GL11.glVertex2i(x2, y2);

			GL11.glTexCoord2f(spaceout.getWidth(), spaceout.getHeight());
			GL11.glVertex2i(x3, y3);

			GL11.glTexCoord2f(0, spaceout.getHeight());
			GL11.glVertex2i(x4, y4);
		}
		GL11.glEnd();

		// draw the buttons
		loadMenuButton.draw();
		quitButton.draw();

		Debug.drawVersion();
	}
}
