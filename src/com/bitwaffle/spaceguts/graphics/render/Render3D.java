package com.bitwaffle.spaceguts.graphics.render;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.bitwaffle.spaceguts.entities.DynamicEntity;
import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.entities.Entity;
import com.bitwaffle.spaceguts.entities.Light;
import com.bitwaffle.spaceguts.graphics.glsl.GLSLProgram;
import com.bitwaffle.spaceguts.graphics.glsl.GLSLShader;
import com.bitwaffle.spaceguts.graphics.glsl.ShaderTypes;
import com.bitwaffle.spaceguts.graphics.model.Material;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.DisplayHelper;
import com.bitwaffle.spaceguts.util.MatrixHelper;
import com.bitwaffle.spaceguts.util.QuaternionHelper;
import com.bitwaffle.spaceout.resources.Paths;


/**
 * Handles all 3D rendering
 * @author TranquilMarmot
 * @see Graphics
 */
public class Render3D {
	private static final String VERTEX_SHADER = "main.vert", FRAGMENT_SHADER = "main.frag";
	/** ModelView and Projection matrices */
	public static Matrix4f projection, modelview;
	
	private static Matrix4f oldModelview = new Matrix4f();
	
	/** Draw distance and field-of-view to use for rendering */
	public static float drawDistance = 3000000.0f, fov =  45.0f;
	
	/** old aspect ratio is saved to know when to change the projection matrix */
	private static float oldAspect = (float) DisplayHelper.windowWidth
			/ (float) DisplayHelper.windowHeight;
	
	/** the shader program to use */
	public static GLSLProgram program;
	
	/** default diffuse color */
	private static final Vector3f DEFAULT_KD = new Vector3f(0.5f, 0.5f, 0.5f);
	/** default ambient color */
	private static final Vector3f DEFAULT_KA = new Vector3f(0.5f, 0.5f, 0.5f);
	/** default specular color */
	private static final Vector3f DEFAULT_KS = new Vector3f(0.8f, 0.8f, 0.8f);
	/** default shiny factor */
	private static final float DEFAULT_SHINY = 50.0f;
	
	/**
	 * Renders the 3D scene
	 */
	public static void render3DScene(){
		setUp3DRender();
		
		transformToCamera();
		
		if(Entities.skybox != null)
			drawSkybox();
		
		setUpLights();
		
		drawLights();
		
		drawDynamicEntities();
		
		drawPassiveEntities();
		
		drawPlayer();
		
		/*
		 * Modelview matrix
		 * Rotate, translate, transpose
		 * Why so confusing
		 */
	}
	
	/**
	 * Sets up for doing a 3D render
	 */
	private static void setUp3DRender(){
		// make sure we're using our shader
		program.use();
		// set the dafault material, just in case
		useDefaultMaterial();
		
		// calculate the current aspect ratio and change the projection matrix if it's changed
		float aspect = (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight;
		
		if(aspect != oldAspect){
			projection = MatrixHelper.perspective(fov, aspect, 1.0f, drawDistance);
			program.setUniform("ProjectionMatrix", projection);
			oldAspect = aspect;
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		modelview.setIdentity();
	}
	
	/**
	 * Sets the current material being used for rendering
	 * @param mat Material to use
	 */
	public static void setCurrentMaterial(Material mat){
		program.setUniform("Material.Kd" , mat.getKd());
		program.setUniform("Material.Ka", mat.getKa());
		program.setUniform("Material.Ks", mat.getKs());
		program.setUniform("Material.Shininess", mat.getShininess());
	}
	
	/**
	 * Set the current material to the default material
	 */
	public static void useDefaultMaterial(){
		program.setUniform("Material.Kd" , DEFAULT_KD);
		program.setUniform("Material.Ka", DEFAULT_KA);
		program.setUniform("Material.Ks", DEFAULT_KS);
		program.setUniform("Material.Shininess", DEFAULT_SHINY);
	}
	
	/**
	 * Transforms the ModelView matrix to represent the camera's location and rotation
	 */
	private static void transformToCamera(){
		// translate to the camera's location
		modelview.translate(new Vector3f(Entities.camera.xOffset, Entities.camera.yOffset, -Entities.camera.zoom));
		
		// reverse the camera's quaternion (we want to look OUT from the camera)
		Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.negate(Entities.camera.rotation, reverse);
		Matrix4f.mul(modelview, QuaternionHelper.toMatrix(reverse), modelview);
	}
	
	/**
	 * Sets up lights for rendering
	 */
	private static void setUpLights(){
		// FIXME only one light supported right now!
		if(Entities.lights.size() > 1)
			System.out.println("More than one light! Multiple lighting not yet implemented.");
		Light l = Entities.lights.iterator().next();
		float transX = Entities.camera.location.x - l.location.x;
		float transY = Entities.camera.location.y - l.location.y;
		float transZ = Entities.camera.location.z - l.location.z;
		
		// crazy quaternion and vector math to get the light into world coordinates
		Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.negate(Entities.camera.rotation, reverse);
		Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(transX, transY, transZ), reverse);
		
		// set uniforms
		program.setUniform("Light.LightPosition", new Vector4f(rotated.x, rotated.y, rotated.z, 0.0f));
		program.setUniform("Light.LightIntensity", l.intensity);
		program.setUniform("Light.LightEnabled", true);
	}
	
	/**
	 * Draws the skybox
	 */
	private static void drawSkybox(){
		program.setUniform("Light.LightEnabled", false);
			
		float transX = Entities.camera.location.x - Entities.skybox.location.x;
		float transY = Entities.camera.location.y - Entities.skybox.location.y;
		float transZ = Entities.camera.location.z - Entities.skybox.location.z;
		
		oldModelview.load(modelview);{
			modelview.translate(new Vector3f(transX, transY, transZ));
			
			Matrix4f.mul(modelview, QuaternionHelper.toMatrix(Entities.skybox.rotation), modelview);
			
			program.setUniform("ModelViewMatrix", modelview);
			Entities.skybox.draw();
		}modelview.load(oldModelview);
		program.setUniform("Light.LightEnabled", true);
	}
	
	/**
	 * Draws any lights
	 */
	private static void drawLights(){
		program.setUniform("Light.LightEnabled", false);
		Iterator<Light> lightIterator = Entities.lights.iterator();
		while(lightIterator.hasNext()){
			Light light = lightIterator.next();
			
			float transX = Entities.camera.location.x - light.location.x;
			float transY = Entities.camera.location.y - light.location.y;
			float transZ = Entities.camera.location.z - light.location.z;
			
			oldModelview.load(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				program.setUniform("ModelViewMatrix", modelview);
				light.draw();
			}modelview.load(oldModelview);
		}
		program.setUniform("Light.LightEnabled", true);
	}
	
	/**
	 * Draws any passive entities
	 */
	private static void drawPassiveEntities(){
		Iterator<Entity> entityIterator = Entities.passiveEntities.iterator();
		while(entityIterator.hasNext()){
			// FIXME might be a better spot to put this
			useDefaultMaterial();
			
			Entity ent = entityIterator.next();
			
			float transX = Entities.camera.location.x - ent.location.x;
			float transY = Entities.camera.location.y - ent.location.y;
			float transZ = Entities.camera.location.z - ent.location.z;
			
			oldModelview.load(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				Matrix4f.mul(modelview, QuaternionHelper.toMatrix(ent.rotation), modelview);
				
				program.setUniform("ModelViewMatrix", modelview);
				ent.draw();
			}modelview.load(oldModelview);
		}
	}
	
	/**
	 * Draws any dynamic entities
	 */
	private static void drawDynamicEntities(){
		Iterator<DynamicEntity> entityIterator = Entities.dynamicEntities.iterator();
		while(entityIterator.hasNext()){
			DynamicEntity ent = entityIterator.next();
			
			float transX = Entities.camera.location.x - ent.location.x;
			float transY = Entities.camera.location.y - ent.location.y;
			float transZ = Entities.camera.location.z - ent.location.z;
			
			oldModelview.load(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				if(Physics.drawDebug){
					ent.drawPhysicsDebug();
				}
				
				Matrix4f.mul(modelview, QuaternionHelper.toMatrix(ent.rotation), modelview);
				
				program.setUniform("ModelViewMatrix", modelview);
				ent.draw();
			}modelview.load(oldModelview);
		}
	}
	
	/**
	 * Draws the player
	 */
	private static void drawPlayer(){
		float transX = Entities.camera.location.x - Entities.player.location.x;
		float transY = Entities.camera.location.y - Entities.player.location.y;
		float transZ = Entities.camera.location.z - Entities.player.location.z;
		
		oldModelview.load(modelview);{
			modelview.translate(new Vector3f(transX, transY, transZ));
			
			Matrix4f.mul(modelview, QuaternionHelper.toMatrix(Entities.player.rotation), modelview);
			
			program.setUniform("ModelViewMatrix", modelview);
			Entities.player.draw();
		}modelview.load(oldModelview);
	}
	
	/**
	 * This must be called before any 3D rendering is done!
	 * Loads in the shaders and initializes matrices
	 */
	public static void init(){
		modelview = new Matrix4f();
		
		// create vertex shader
		GLSLShader vertShader = new GLSLShader(ShaderTypes.VERTEX);
		String vertFile = Paths.SHADER_PATH.path() + VERTEX_SHADER;
		if (!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());

		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + FRAGMENT_SHADER;
		if (!fragShader.compileShaderFromFile(fragFile)) {
			System.out.println(fragShader.log());
		}

		// create and use program
		program = new GLSLProgram();
		program.addShader(fragShader);
		program.addShader(vertShader);
		program.link();
		program.use();
		
		// calculate the current aspect ratio
		float oldAspect = (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight;
		
		projection = MatrixHelper.perspective(fov, oldAspect, 1.0f, drawDistance);
		program.setUniform("ProjectionMatrix", projection);
	}
}
