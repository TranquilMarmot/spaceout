package spaceguts.graphics.glsl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import spaceguts.util.DisplayHelper;
import spaceguts.util.MatrixHelper;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.input.Keys;
import spaceguts.util.input.MouseManager;
import spaceguts.util.resources.Models;
import spaceguts.util.resources.Paths;

public class GLSLRender {
	static int vaoHandle = 0;

	private static GLSLProgram program;
	private static float zoom = 10;
	private static Quaternion rotation = new Quaternion(-0.23499879f, -0.4204249f, 0.5374693f, 0.69221f);
	private static Matrix4f projection, modelview;
	//private static GLSLModel model;
	private static VBOTorus torus;
	private static GLSLModel model;
	private static int numTris;
	private static Vector4f lightPosition = new Vector4f(-5.0f,5.0f,-2.0f, 0.0f);
	private static Vector3f modelPosition = new Vector3f(0.0f, 0.0f, 0.0f);
	private static boolean f1Down = false, f2Down = false, renderWhat = false;
	private static int adsIndex, diffuseIndex;
	
	static FloatBuffer MVBuffer, projBuffer;
	
	public static void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// buffer for transferring matrix to shader
		MVBuffer.clear();
		projBuffer.clear();
		
		projection = MatrixHelper.perspective(45.0f, (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight, 0.1f,
				500.0f);
		
		modelview.setIdentity();
		modelview.translate(modelPosition);
		modelPosition.z = -zoom;
		Matrix4f.mul(modelview, QuaternionHelper.toMatrix(rotation), modelview);
		program.setUniform("ModelViewMatrix", modelview);
		
		if(Keys.W.isPressed())
			modelPosition.y += 0.05f;
		else if(Keys.S.isPressed())
			modelPosition.y -= 0.05f;
		
		if(Keys.A.isPressed())
			modelPosition.x -= 0.05f;
		else if(Keys.D.isPressed())
			modelPosition.x += 0.05f;
		
		if(MouseManager.button0)
			rotation = QuaternionHelper.rotate(rotation, new Vector3f(-MouseManager.dx, 0.0f, MouseManager.dy));
		else if(MouseManager.button1){
			rotation = QuaternionHelper.rotate(rotation, new Vector3f(0.0f, -MouseManager.dx, MouseManager.dy));
		}
		
		if(MouseManager.button2)
			zoom -= MouseManager.dy;
		
		if(Keys.RIGHT.isPressed())
			lightPosition.x++;
			//rotation = QuaternionHelper.rotateY(rotation, 0.5f);
		else if(Keys.LEFT.isPressed())
			lightPosition.x--;
			//rotation = QuaternionHelper.rotateY(rotation, -0.5f);
		
		if(Keys.UP.isPressed())
			lightPosition.y++;
			//rotation = QuaternionHelper.rotateX(rotation, 0.5f);
		else if(Keys.DOWN.isPressed())
			lightPosition.y--;
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
		
		if(renderWhat){
			model.render();
		}
		else{
			torus.render();
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
	}

	public static void initGL() {
		MVBuffer = BufferUtils.createFloatBuffer(16);
		projBuffer = BufferUtils.createFloatBuffer(16);
		
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
		String vertFile = Paths.SHADER_PATH.path() + "pervertex.vert";
		if (!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());

		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + "pervertex.frag";
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

		torus = new VBOTorus(0.7f, 0.3f, 30, 30);
		model = Models.SAUCER.getModel();
	}
	
	public static void renderPhong() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// buffer for transferring matrix to shader
		MVBuffer.clear();
		projBuffer.clear();
		
		projection = MatrixHelper.perspective(45.0f, (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight, 0.1f,
				500.0f);
		
		modelview.setIdentity();
		modelview.translate(modelPosition);
		modelPosition.z = -zoom;
		Matrix4f.mul(modelview, QuaternionHelper.toMatrix(rotation), modelview);
		
		if(Keys.W.isPressed())
			modelPosition.y += 0.05f;
		else if(Keys.S.isPressed())
			modelPosition.y -= 0.05f;
		
		if(Keys.A.isPressed())
			modelPosition.x -= 0.05f;
		else if(Keys.D.isPressed())
			modelPosition.x += 0.05f;
		
		if(MouseManager.button0)
			rotation = QuaternionHelper.rotate(rotation, new Vector3f(-MouseManager.dx, 0.0f, MouseManager.dy));
		else if(MouseManager.button1){
			rotation = QuaternionHelper.rotate(rotation, new Vector3f(0.0f, -MouseManager.dx, MouseManager.dy));
		}
		
		if(MouseManager.button2)
			zoom -= MouseManager.dy;
		
		if(Keys.RIGHT.isPressed())
			lightPosition.x++;
		else if(Keys.LEFT.isPressed())
			lightPosition.x--;
		
		if(Keys.UP.isPressed())
			lightPosition.y++;
		else if(Keys.DOWN.isPressed())
			lightPosition.y--;
		
		zoom -= MouseManager.wheel / 100;
		
		program.setUniform("ProjectionMatrix", projection);
		
		program.setUniform("Light.Position", lightPosition);
		
		Vector3f Kd = new Vector3f(0.5f, 0.5f, 0.5f);
		program.setUniform("Material.Kd" , Kd);
		
		Vector3f Ld = new Vector3f(1.0f, 1.0f, 1.0f);
		program.setUniform("Light.Ld", Ld);
		
		Vector3f Ka = new Vector3f(0.5f, 0.5f, 0.5f);
		program.setUniform("Material.Ka", Ka);
		
		Vector3f La = new Vector3f(0.4f, 0.4f, 0.4f);
		program.setUniform("Light.La", La);
		
		Vector3f Ks = new Vector3f(0.8f, 0.8f, 0.8f);
		program.setUniform("Material.Ks", Ks);
		
		Vector3f Ls = new Vector3f(1.0f, 1.0f, 1.0f);
		program.setUniform("Light.Ls", Ls);
		
		float shininess = 50.0f;
		program.setUniform("Material.Shininess", shininess);
		
		if(renderWhat){
			modelview.translate(new Vector3f(0.0f, -5.0f, 0.0f));
			program.setUniform("ModelViewMatrix", modelview);
			program.useVertexSubRoutines(new int[] { adsIndex });
			model.render();
			
			modelview.translate(new Vector3f(0.0f, 10.0f, 0.0f));
			program.setUniform("ModelViewMatrix", modelview);
			program.useVertexSubRoutines(new int[] { diffuseIndex });
			model.render();
		}
		else{
			modelview.translate(new Vector3f(0.0f, -5.0f, 0.0f));
			program.setUniform("ModelViewMatrix", modelview);
			program.useVertexSubRoutines(new int[] { adsIndex });
			torus.render();
			
			modelview.translate(new Vector3f(0.0f, 10.0f, 0.0f));
			program.setUniform("ModelViewMatrix", modelview);
			program.useVertexSubRoutines(new int[] { diffuseIndex });
			torus.render();
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
	}

	public static void initGLPhong() {
		MVBuffer = BufferUtils.createFloatBuffer(16);
		projBuffer = BufferUtils.createFloatBuffer(16);
		
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
		String vertFile = Paths.SHADER_PATH.path() + "phong.vert";
		if (!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());

		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + "phong.frag";
		if (!fragShader.compileShaderFromFile(fragFile)) {
			System.out.println(fragShader.log());
		}

		// create and use program
		program = new GLSLProgram();
		program.addShader(fragShader);
		program.addShader(vertShader);
		program.link();
		program.use();

		torus = new VBOTorus(0.7f, 0.3f, 30, 30);
		model = Models.SAUCER.getModel();
		
		adsIndex = program.getSubroutineIndex("phongModel");
		
		diffuseIndex = program.getSubroutineIndex("diffuseOnly");
		
		System.out.println(adsIndex + " " + diffuseIndex);
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
		
		numTris = indices.length;
	}
	
	public static void renderCube() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// buffer for transferring matrix to shader
		MVBuffer.clear();
		projBuffer.clear();
		
		projection = MatrixHelper.perspective(45.0f, (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight, 0.1f,
				500.0f);
		projection.store(projBuffer);
		projBuffer.rewind();
		
		modelview.setIdentity();
		modelview.translate(new Vector3f(0.0f, 0.0f, -zoom));
		Matrix4f.mul(modelview, QuaternionHelper.toMatrix(rotation), modelview);
		modelview.store(MVBuffer);
		MVBuffer.rewind();
		
		if(MouseManager.button0){
			rotation = QuaternionHelper.rotateY(rotation, MouseManager.dx);
			rotation = QuaternionHelper.rotateX(rotation, MouseManager.dy);
		}
		
		if(Keys.RIGHT.isPressed())
			rotation = QuaternionHelper.rotateY(rotation, 0.5f);
		else if(Keys.LEFT.isPressed())
			rotation = QuaternionHelper.rotateY(rotation, -0.5f);
		
		if(Keys.UP.isPressed())
			rotation = QuaternionHelper.rotateX(rotation, 0.5f);
		else if(Keys.DOWN.isPressed())
			rotation = QuaternionHelper.rotateX(rotation, -0.5f);
		
		zoom -= MouseManager.wheel / 100;
		
		program.setUniform("ModelView", modelview);
		program.setUniform("Projection", projection);

		// draw triangle
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawElements(GL11.GL_TRIANGLES, numTris, GL11.GL_UNSIGNED_INT, 0L);
	}

	public static void initGLCube() {
		MVBuffer = BufferUtils.createFloatBuffer(16);
		projBuffer = BufferUtils.createFloatBuffer(16);
		
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
		GL11.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		// create vertex shader
		GLSLShader vertShader = new GLSLShader(ShaderTypes.VERTEX);
		String vertFile = Paths.SHADER_PATH.path() + "basic_uniform.vert";
		if (!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());

		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + "basic_uniform.frag";
		if (!fragShader.compileShaderFromFile(fragFile)) {
			System.out.println(fragShader.log());
		}

		// create and use program
		program = new GLSLProgram();
		program.addShader(fragShader);
		program.addShader(vertShader);
		program.link();
		program.use();

		//program.printActiveUniforms();
		//program.printActiveAttribs();

		initCube();
	}
}
