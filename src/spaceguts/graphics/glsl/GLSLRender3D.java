package spaceguts.graphics.glsl;

import java.util.Iterator;
import java.util.Stack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entities;
import spaceguts.entities.Entity;
import spaceguts.entities.Light;
import spaceguts.physics.Physics;
import spaceguts.util.DisplayHelper;
import spaceguts.util.MatrixHelper;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.resources.Paths;

public class GLSLRender3D {
	private static final String VERTEX_SHADER = "texture.vert", FRAGMENT_SHADER = "texture.frag";
	/** ModelView and Projection matrices */
	public static Matrix4f projection, modelview;
	private static Stack<Matrix4f> modelviewStack = new Stack<Matrix4f>();
	
	/** Draw distance and field-of-view to use for rendering */
	public static float drawDistance = 3000000.0f, fov =  45.0f;
	
	/** old aspect ratio is saved to know when to change the projection matrix */
	private static float oldAspect = (float) DisplayHelper.windowWidth
			/ (float) DisplayHelper.windowHeight;
	
	/** the shader program to use */
	private static GLSLProgram program;
	
	
	public static void render3DScene(){
		setUp3DRender();
		
		transformToCamera();
		
		if(Entities.skybox != null)
			drawSkybox();
		
		setUpLights();
		
		//drawLights();
		
		//drawDynamicEntities();
		
		//drawPassiveEntities();
		
		drawPlayer();
	}
	
	private static void setUp3DRender(){
		program.use();
		Vector3f Kd = new Vector3f(0.5f, 0.5f, 0.5f);
		program.setUniform("Material.Kd" , Kd);
		
		Vector3f Ka = new Vector3f(0.5f, 0.5f, 0.5f);
		program.setUniform("Material.Ka", Ka);
		
		Vector3f Ks = new Vector3f(0.8f, 0.8f, 0.8f);
		program.setUniform("Material.Ks", Ks);
		
		float shininess = 50.0f;
		program.setUniform("Material.Shininess", shininess);
		
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
	
	private static void transformToCamera(){
		modelview.translate(new Vector3f(Entities.camera.xOffset, Entities.camera.yOffset, -Entities.camera.zoom));
		Matrix4f.mul(modelview, QuaternionHelper.toMatrix(Entities.camera.rotation), modelview);
	}
	
	private static void setUpLights(){
		if(Entities.lights.size() > 1)
			System.out.println("More than one light! Multiple lighting not yet implemented.");
		Light l = Entities.lights.values().iterator().next();
		float transX = Entities.camera.location.x - l.location.x;
		float transY = Entities.camera.location.y - l.location.y;
		float transZ = Entities.camera.location.z - l.location.z;
		Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new Vector3f(transX, transY, transZ), Entities.camera.rotation);
		program.setUniform("Light.LightPosition", new Vector4f(rotated.x, rotated.y, rotated.z, 0.0f));
		program.setUniform("Light.LightIntensity", l.intensity);
		program.setUniform("Light.LightEnabled", true);
	}
	
	private static void drawSkybox(){
		program.setUniform("Light.LightEnabled", false);
			
		float transX = Entities.camera.location.x - Entities.skybox.location.x;
		float transY = Entities.camera.location.y - Entities.skybox.location.y;
		float transZ = Entities.camera.location.z - Entities.skybox.location.z;
		
		modelviewStack.push(modelview);{
			modelview.translate(new Vector3f(transX, transY, transZ));
			
			Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
			Quaternion.negate(Entities.skybox.rotation, reverse);
			Matrix4f.mul(modelview, QuaternionHelper.toMatrix(reverse), modelview);
			
			program.setUniform("ModelViewMatrix", modelview);
			Entities.skybox.draw();
		}modelview = modelviewStack.pop();
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
			
			modelviewStack.push(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				program.setUniform("ModelViewMatrix", modelview);
				light.draw();
			}modelview = modelviewStack.pop();
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
			
			modelviewStack.push(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
				Quaternion.negate(ent.rotation, reverse);
				Matrix4f.mul(modelview, QuaternionHelper.toMatrix(reverse), modelview);
				
				program.setUniform("ModelViewMatrix", modelview);
				ent.draw();
			}modelview = modelviewStack.pop();
		}
	}
	
	private static void drawDynamicEntities(){
		Iterator<DynamicEntity> entityIterator = Entities.dynamicEntities.values().iterator();
		while(entityIterator.hasNext()){
			DynamicEntity ent = entityIterator.next();
			System.out.println(ent.type);
			
			float transX = Entities.camera.location.x - ent.location.x;
			float transY = Entities.camera.location.y - ent.location.y;
			float transZ = Entities.camera.location.z - ent.location.z;
			
			modelviewStack.push(modelview);{
				modelview.translate(new Vector3f(transX, transY, transZ));
				
				if(Physics.drawDebug){
					//TODO this
				}
				
				Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
				Quaternion.negate(ent.rotation, reverse);
				Matrix4f.mul(modelview, QuaternionHelper.toMatrix(reverse), modelview);
				
				program.setUniform("ModelViewMatrix", modelview);
				ent.draw();
			}modelview = modelviewStack.pop();
		}
	}
	
	private static void drawPlayer(){
		float transX = Entities.camera.location.x - Entities.player.location.x;
		float transY = Entities.camera.location.y - Entities.player.location.y;
		float transZ = Entities.camera.location.z - Entities.player.location.z;
		
		modelviewStack.push(modelview);{
			modelview.translate(new Vector3f(transX, transY, transZ));
			
			Quaternion reverse = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
			Quaternion.negate(Entities.player.rotation, reverse);
			Matrix4f.mul(modelview, QuaternionHelper.toMatrix(reverse), modelview);
			
			program.setUniform("ModelViewMatrix", modelview);
			Entities.player.draw();
		}modelview = modelviewStack.pop();
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
