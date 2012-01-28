package spaceguts.graphics.glsl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

import spaceguts.entities.Camera;
import spaceguts.graphics.gui.GUI;
import spaceguts.graphics.gui.menu.PauseMenu;
import spaceguts.graphics.render.Render2D;
import spaceguts.util.DisplayHelper;
import spaceguts.util.MatrixHelper;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.input.Keys;
import spaceguts.util.input.MouseManager;
import spaceguts.util.resources.Models;
import spaceguts.util.resources.Paths;
import spaceguts.util.resources.Textures;
import spaceout.entities.passive.Skybox;

public class GLSLRender {
	static int vaoHandle = 0;

	private static GLSLProgram program;
	private static float zoom = 10;
	private static Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
	private static Matrix4f projection, modelview;
	private static GLSLModel model, ship;
	private static Vector4f lightPosition = new Vector4f(-5.0f,5.0f,-2.0f, 0.0f);
	private static Vector3f modelPosition = new Vector3f(0.0f, 0.0f, 0.0f);
	private static boolean f1Down = false, f2Down = false, renderWhat = false;
	private static Texture saucerTexture, shipTexture;
	private static Skybox skybox;
	private static Stack<Matrix4f> modelviewStack;
	private static Sphere sphere;
	
	public static void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		program.use();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		projection = MatrixHelper.perspective(45.0f, (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight, 0.1f,
				500.0f);
		
		modelview.setIdentity();
		modelviewStack.push(modelview);
		modelview.rotate(25.0f, new Vector3f(1.0f, 0.0f, 0.5f));
		program.setUniform("ModelViewMatrix", modelview);
		program.setUniform("Light.LightEnabled", false);
		skybox.draw();
		modelview = modelviewStack.pop();
		
		//modelview.setIdentity();
		modelviewStack.push(modelview);
		modelview.translate(modelPosition);
		modelPosition.z = -zoom;
		Matrix4f.mul(modelview, QuaternionHelper.toMatrix(rotation), modelview);
		program.setUniform("ModelViewMatrix", modelview);
		modelview = modelviewStack.pop();
		
		if(MouseManager.button0)
			rotation = QuaternionHelper.rotate(rotation, new Vector3f(MouseManager.dy, -MouseManager.dx, 0.0f));
		else if(MouseManager.button2)
			modelPosition.translate(-MouseManager.dx / 10.0f, MouseManager.dy / 10.0f, 0.0f);
		if(MouseManager.button1)
			rotation = QuaternionHelper.rotate(rotation, new Vector3f(MouseManager.dy, 0.0f, -MouseManager.dx));
		
		if(Keys.RIGHT.isPressed())
			lightPosition.x += 0.25f;
		else if(Keys.LEFT.isPressed())
			lightPosition.x -= 0.25f;
		
		if(Keys.UP.isPressed())
			lightPosition.y += 0.25f;
			//rotation = QuaternionHelper.rotateX(rotation, 0.5f);
		else if(Keys.DOWN.isPressed())
			lightPosition.y -= 0.25f;
			//rotation = QuaternionHelper.rotateX(rotation, -0.5f);
		
		zoom -= MouseManager.wheel / 100;
		
		program.setUniform("ProjectionMatrix", projection);
		
		program.setUniform("Light.LightPosition", lightPosition);
		
		Vector3f lightIntensity = new Vector3f(0.9f, 0.9f, 0.9f);
		program.setUniform("Light.LightIntensity", lightIntensity);
		
		Vector3f Kd = new Vector3f(0.5f, 0.5f, 0.5f);
		program.setUniform("Material.Kd" , Kd);
		
		Vector3f Ka = new Vector3f(0.5f, 0.5f, 0.5f);
		program.setUniform("Material.Ka", Ka);
		
		Vector3f Ks = new Vector3f(0.8f, 0.8f, 0.8f);
		program.setUniform("Material.Ks", Ks);
		
		float shininess = 50.0f;
		program.setUniform("Material.Shininess", shininess);
		
		program.setUniform("Light.LightEnabled", true);
		if(renderWhat){
			ship.render();
			sphere.draw(10, 15, 15);
		}
		else{
			saucerTexture.bind();
			model.render();
		}
		
		if(Keys.F1.isPressed() && !f1Down){
			model.wireframe = !model.wireframe;
			f1Down = true;
		}
		
		if(!Keys.F1.isPressed())
			f1Down = false;
		
		if(Keys.M.isPressed() && !f2Down){
			renderWhat = !renderWhat;
			f2Down = true;
		}
		
		if(!Keys.M.isPressed())
			f2Down = false;
		
		GL20.glUseProgram(0);
		Render2D.draw2DScene();
	}

	public static void initGL() {
		modelview = new Matrix4f();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearDepth(500.0);
		
		projection = MatrixHelper.perspective(45.0f, (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight, 0.1f,
				500.0f);

		//projection.translate(new Vector3f(0.0f, 0.0f, -10.0f));
		// set the clear color
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		// create vertex shader
		GLSLShader vertShader = new GLSLShader(ShaderTypes.VERTEX);
		String vertFile = Paths.SHADER_PATH.path() + "texture.vert";
		if (!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());

		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + "texture.frag";
		if (!fragShader.compileShaderFromFile(fragFile)) {
			System.out.println(fragShader.log());
		}

		// create and use program
		program = new GLSLProgram();
		program.addShader(fragShader);
		program.addShader(vertShader);
		program.link();
		program.use();
		
		//program.printActiveAttribs();
		//program.printActiveUniforms();

		model = Models.SAUCER.getModel();
		ship = Models.WING_X.getModel();
		Camera c = new Camera(modelPosition);
		skybox = new Skybox(c);
		
		saucerTexture = Textures.SAUCER.texture();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, saucerTexture.getTextureID());
		
		byte[] pixels = saucerTexture.getTextureData();
		ByteBuffer pixelBuf = BufferUtils.createByteBuffer(pixels.length);
		pixelBuf.put(pixels);
		pixelBuf.rewind();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, saucerTexture.getTextureWidth(), saucerTexture.getTextureHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuf);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		
		program.setUniform("Tex1", 0);
		
		shipTexture = Textures.SHIP1.texture();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shipTexture.getTextureID());
		
		pixels = shipTexture.getTextureData();
		ByteBuffer anotherPixelBuf = BufferUtils.createByteBuffer(pixels.length);
		anotherPixelBuf.put(pixels);
		anotherPixelBuf.rewind();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, shipTexture.getTextureWidth(), shipTexture.getTextureHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, anotherPixelBuf);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		
		modelviewStack = new Stack<Matrix4f>();

		GUI.addGUIObject(new PauseMenu());
		sphere = new Sphere();
		sphere.setNormals(GLU.GLU_SMOOTH);
		sphere.setTextureFlag(true);
	}
	
		public static void initCube(){		
		// vertex coords array
		float[] vertices =   {1,1,1,  -1,1,1,  -1,-1,1,  1,-1,1,        // v0-v1-v2-v3
							  1,1,1,  1,-1,1,  1,-1,-1,  1,1,-1,        // v0-v3-v4-v5
							  1,1,1,  1,1,-1,  -1,1,-1,  -1,1,1,        // v0-v5-v6-v1
							  -1,1,1,  -1,1,-1,  -1,-1,-1,  -1,-1,1,    // v1-v6-v7-v2
							  -1,-1,-1,  1,-1,-1,  1,-1,1,  -1,-1,1,    // v7-v4-v3-v2
							  1,-1,-1,  -1,-1,-1,  -1,1,-1,  1,1,-1};   // v4-v7-v6-v5

		// normal array
		float[] normals =   {0,0,1,  0,0,1,  0,0,1,  0,0,1,             // v0-v1-v2-v3
		                     1,0,0,  1,0,0,  1,0,0, 1,0,0,              // v0-v3-v4-v5
		                     0,1,0,  0,1,0,  0,1,0, 0,1,0,              // v0-v5-v6-v1
		                     -1,0,0,  -1,0,0, -1,0,0,  -1,0,0,          // v1-v6-v7-v2
		                     0,-1,0,  0,-1,0,  0,-1,0,  0,-1,0,         // v7-v4-v3-v2
		                     0,0,-1,  0,0,-1,  0,0,-1,  0,0,-1};        // v4-v7-v6-v5

		// color array
		float[] colors =   {1,1,1,  1,1,0,  1,0,0,  1,0,1,              // v0-v1-v2-v3
		                    1,1,1,  1,0,1,  0,0,1,  0,1,1,              // v0-v3-v4-v5
		                    1,1,1,  0,1,1,  0,1,0,  1,1,0,              // v0-v5-v6-v1
		                    1,1,0,  0,1,0,  0,0,0,  1,0,0,              // v1-v6-v7-v2
		                    0,0,0,  0,0,1,  1,0,1,  1,0,0,              // v7-v4-v3-v2
		                    0,0,1,  0,0,0,  0,1,0,  0,1,1};             // v4-v7-v6-v5
		
		int[] indices = {
				0,1,2,  2,3,0,
				1,2,3,  2,3,0,
				4,5,6,  6,7,4,
				8,9,10, 10,11,8,
				12,13,14, 14,15,12,
				16,17,18, 18,19,16,
				20,21,22, 22,23,20
		};
				
		FloatBuffer vertBuf = BufferUtils.createFloatBuffer(vertices.length);
		vertBuf.put(vertices);
		vertBuf.rewind();
		
		FloatBuffer colBuf = BufferUtils.createFloatBuffer(colors.length);
		colBuf.put(colors);
		colBuf.rewind();
		
		IntBuffer indBuf = BufferUtils.createIntBuffer(indices.length);
		indBuf.put(indices);
		indBuf.rewind();
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		IntBuffer vboHandles = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(vboHandles);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indBuf, GL15.GL_STATIC_DRAW);
		
		int numTris = indices.length;
	}
}
