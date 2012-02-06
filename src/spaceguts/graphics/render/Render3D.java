package spaceguts.graphics.render;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entities;
import spaceguts.entities.Entity;
import spaceguts.entities.Light;
import spaceguts.graphics.glsl.GLSLProgram;
import spaceguts.graphics.glsl.GLSLShader;
import spaceguts.graphics.glsl.ShaderTypes;
import spaceguts.physics.Physics;
import spaceguts.util.DisplayHelper;
import spaceguts.util.MatrixHelper;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.resources.Paths;

public class Render3D {
	private static final String VERTEX_SHADER = "texture.vert", FRAGMENT_SHADER = "texture.frag";
	/** ModelView and Projection matrices */
	public static Matrix4f projection, modelview;
	
	/** Draw distance and field-of-view to use for rendering */
	public static float drawDistance = 3000000.0f, fov =  45.0f;
	
	/** old aspect ratio is saved to know when to change the projection matrix */
	private static float oldAspect = (float) DisplayHelper.windowWidth
			/ (float) DisplayHelper.windowHeight;
	
	/** the shader program to use */
	public static GLSLProgram program;
	
	private static final Vector3f DEFAULT_KD = new Vector3f(0.5f, 0.5f, 0.5f);
	private static final Vector3f DEFAULT_KA = new Vector3f(0.5f, 0.5f, 0.5f);
	private static final Vector3f DEFAULT_KS = new Vector3f(0.8f, 0.8f, 0.8f);
	private static final float DEFAULT_SHINY = 50.0f;
	
	
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
	
	private static void setUp3DRender(){
		program.use();
		useDefaultMaterial();
		
		// calculate the current aspect ratio
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
	
	public static void setCurrentMaterial(Vector3f Kd, Vector3f Ka, Vector3f Ks, float shininess){
		program.setUniform("Material.Kd" , Kd);
		program.setUniform("Material.Ka", Ka);
		program.setUniform("Material.Ks", Ks);
		program.setUniform("Material.Shininess", shininess);
	}
	
	public static void useDefaultMaterial(){
		program.setUniform("Material.Kd" , DEFAULT_KD);
		program.setUniform("Material.Ka", DEFAULT_KA);
		program.setUniform("Material.Ks", DEFAULT_KS);
		program.setUniform("Material.Shininess", DEFAULT_SHINY);
	}
	
	private static void transformToCamera(){
		modelview.translate(new Vector3f(Entities.camera.xOffset, Entities.camera.yOffset, -Entities.camera.zoom));
		
		
		Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.negate(Entities.camera.rotation, reverse);
		Matrix4f.mul(modelview, QuaternionHelper.toMatrix(reverse), modelview);
	}
	
	private static void setUpLights(){
		if(Entities.lights.size() > 1)
			System.out.println("More than one light! Multiple lighting not yet implemented.");
		Light l = Entities.lights.values().iterator().next();
		float transX = Entities.camera.location.x - l.location.x;
		float transY = Entities.camera.location.y - l.location.y;
		float transZ = Entities.camera.location.z - l.location.z;
		
		Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.negate(Entities.camera.rotation, reverse);
		Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(transX, transY, transZ), reverse);
		program.setUniform("Light.LightPosition", new Vector4f(rotated.x, rotated.y, rotated.z, 0.0f));
		program.setUniform("Light.LightIntensity", l.intensity);
		program.setUniform("Light.LightEnabled", true);
	}
	
	private static void drawSkybox(){
		program.setUniform("Light.LightEnabled", false);
			
		float transX = Entities.camera.location.x - Entities.skybox.location.x;
		float transY = Entities.camera.location.y - Entities.skybox.location.y;
		float transZ = Entities.camera.location.z - Entities.skybox.location.z;
		
		Matrix4f oldModelview = new Matrix4f(modelview);{
			modelview.translate(new Vector3f(transX, transY, transZ));
			
			Matrix4f.mul(modelview, QuaternionHelper.toMatrix(Entities.skybox.rotation), modelview);
			
			program.setUniform("ModelViewMatrix", modelview);
			Entities.skybox.draw();
		}modelview = oldModelview;
		program.setUniform("Light.LightEnabled", true);
	}
	
	private static void drawLights(){
		program.setUniform("Light.LightEnabled", false);
		Iterator<Light> lightIterator = Entities.lights.values().iterator();
		while(lightIterator.hasNext()){
			Light light = lightIterator.next();
			
			float transX = Entities.camera.location.x - light.location.x;
			float transY = Entities.camera.location.y - light.location.y;
			float transZ = Entities.camera.location.z - light.location.z;
			
			Matrix4f oldModelview = new Matrix4f(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				program.setUniform("ModelViewMatrix", modelview);
				light.draw();
			}modelview = oldModelview;
		}
		program.setUniform("Light.LightEnabled", true);
	}
	
	private static void drawPassiveEntities(){
		Iterator<Entity> entityIterator = Entities.passiveEntities.values().iterator();
		while(entityIterator.hasNext()){
			Entity ent = entityIterator.next();
			
			float transX = Entities.camera.location.x - ent.location.x;
			float transY = Entities.camera.location.y - ent.location.y;
			float transZ = Entities.camera.location.z - ent.location.z;
			
			Matrix4f oldModelview = new Matrix4f(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				Matrix4f.mul(modelview, QuaternionHelper.toMatrix(ent.rotation), modelview);
				
				program.setUniform("ModelViewMatrix", modelview);
				ent.draw();
			}modelview = oldModelview;
		}
	}
	
	private static void drawDynamicEntities(){
		Iterator<DynamicEntity> entityIterator = Entities.dynamicEntities.values().iterator();
		while(entityIterator.hasNext()){
			DynamicEntity ent = entityIterator.next();
			
			float transX = Entities.camera.location.x - ent.location.x;
			float transY = Entities.camera.location.y - ent.location.y;
			float transZ = Entities.camera.location.z - ent.location.z;
			
			Matrix4f oldModelview = new Matrix4f(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				if(Physics.drawDebug){
					ent.drawPhysicsDebug();
				}
				
				Matrix4f.mul(modelview, QuaternionHelper.toMatrix(ent.rotation), modelview);
				
				program.setUniform("ModelViewMatrix", modelview);
				ent.draw();
			}modelview = oldModelview;
		}
	}
	
	private static void drawPlayer(){
		float transX = Entities.camera.location.x - Entities.player.location.x;
		float transY = Entities.camera.location.y - Entities.player.location.y;
		float transZ = Entities.camera.location.z - Entities.player.location.z;
		
		Matrix4f oldModelview = new Matrix4f(modelview);{
			modelview.translate(new Vector3f(transX, transY, transZ));
			
			Matrix4f.mul(modelview, QuaternionHelper.toMatrix(Entities.player.rotation), modelview);
			
			program.setUniform("ModelViewMatrix", modelview);
			Entities.player.draw();
		}modelview = oldModelview;
	}
	
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
