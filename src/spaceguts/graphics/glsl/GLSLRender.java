package spaceguts.graphics.glsl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
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
import spaceguts.util.resources.Textures;

public class GLSLRender {
	static int vaoHandle = 0;

	private static GLSLProgram program;
	private static float zoom = 10;
	private static Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
	private static Matrix4f projection, modelview;
	//private static GLSLModel model;
	private static VBOTorus torus;
	private static GLSLModel model;
	private static int numTris;
	private static Vector4f lightPosition = new Vector4f(-5.0f,5.0f,-2.0f, 1.0f);
	private static Vector3f modelPosition = new Vector3f(0.0f, 0.0f, 0.0f);
	
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
		
		if(Keys.W.isPressed())
			modelPosition.y += 0.05f;
		else if(Keys.S.isPressed())
			modelPosition.y -= 0.05f;
		
		if(Keys.A.isPressed())
			modelPosition.x -= 0.05f;
		else if(Keys.D.isPressed())
			modelPosition.x += 0.05f;
		
		if(MouseManager.button0){
			rotation = QuaternionHelper.rotateY(rotation, MouseManager.dx);
			rotation = QuaternionHelper.rotateX(rotation, MouseManager.dy);
		}
		
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
		
		program.setUniform("ModelViewMatrix", modelview);
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

		//torus.render();
		model.render();
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

		//program.printActiveUniforms();
		//program.printActiveAttribs();

		//torus = new VBOTorus(0.7f, 0.3f, 30, 30);
		model = Models.WESCOTT.getModel();
	}
	
	public static void renderDiffuse() {
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
		
		if(MouseManager.button0){
			rotation = QuaternionHelper.rotateY(rotation, MouseManager.dx);
			rotation = QuaternionHelper.rotateX(rotation, MouseManager.dy);
		}
		
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
		
		program.setUniform("ModelViewMatrix", modelview);
		program.setUniform("ProjectionMatrix", projection);
		
		program.setUniform("Light.LightPosition", lightPosition);
		
		Vector3f Kd = new Vector3f(0.0f, 0.5f, 0.0f);
		program.setUniform("Light.Kd", Kd);
		
		Vector3f Ld = new Vector3f(1.0f, 1.0f, 1.0f);
		program.setUniform("Light.Ld", Ld);

		torus.render();
		//model.render();
	}

	public static void initGLDiffuse() {
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
		String vertFile = Paths.SHADER_PATH.path() + "diffuse.vert";
		if (!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());

		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + "diffuse.frag";
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

		torus = new VBOTorus(0.7f, 0.3f, 30, 30);
		//model =  GLSLModelLoader.loadObjFile(Paths.MODEL_PATH.path() + "ships/saucer.obj", Textures.SHIP1);
	}
	
	public static void renderModel() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// buffer for transferring matrix to shader
		MVBuffer.clear();
		projBuffer.clear();
		
		projection = MatrixHelper.perspective(45.0f, (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight, 0.1f,
				500.0f);
		
		modelview.setIdentity();
		modelview.translate(new Vector3f(0.0f, 0.0f, -zoom));
		Matrix4f.mul(modelview, QuaternionHelper.toMatrix(rotation), modelview);
		
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

		model.render();
	}

	public static void initGLModel() {
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

		model = GLSLModelLoader.loadObjFile(Paths.MODEL_PATH.path() + "ships/wing_x.obj", Textures.SHIP1);
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
			
		/*
		int[] indices =     {0,1,2,3,
		                     4,5,6,7,
		                     8,9,10,11,
		                     12,13,14,15,
		                     16,17,18,19,
		                     20,21,22,23};
		 */
		
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
	
	public static void renderTorus(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		
		Matrix4f mv = new Matrix4f();
		//Matrix4f.mul(view, modelview, mv);
		//program.setUniform("ModelViewMatrix", mv);
		
		/*
		Matrix3f norm = new Matrix3f();
		norm.m00 = mv.m00;
		norm.m01 = mv.m01;
		norm.m02 = mv.m02;
		norm.m10 = mv.m10;
		norm.m11 = mv.m11;
		norm.m12 = mv.m12;
		norm.m20 = mv.m20;
		norm.m21 = mv.m21;
		norm.m22 = mv.m22;
		program.setUniform("NormalMatrix", norm);
		*/
		
		Matrix4f mvp = new Matrix4f();
		Matrix4f.mul(projection, mv, mvp);
		program.setUniform("MVP", mvp);
		
		torus.render();
	}
	
	public static void initGLTorus(){
		GL11.glViewport(0, 0, DisplayHelper.windowWidth,
				DisplayHelper.windowHeight);
		
		GLSLShader vert = new GLSLShader(ShaderTypes.VERTEX);
		if(!vert.compileShaderFromFile(Paths.SHADER_PATH.path() + "diffuse_simple.vert"))
			System.out.println(vert.log());
		
		GLSLShader frag = new GLSLShader(ShaderTypes.FRAGMENT);
		if(!frag.compileShaderFromFile(Paths.SHADER_PATH.path() + "diffuse_simple.frag"))
			System.out.println(frag.log());
		
		program = new GLSLProgram();
		program.addShader(vert);
		program.addShader(frag);
		program.link();
		program.use();
		
		program.printActiveAttribs();
		program.printActiveUniforms();
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		torus = new VBOTorus(0.7f, 0.3f, 30, 30);
		
		modelview = new Matrix4f();
		Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		rotation = QuaternionHelper.rotateX(rotation, -35.0f);
		rotation = QuaternionHelper.rotateY(rotation, 35.0f);
		FloatBuffer rotBuf = BufferUtils.createFloatBuffer(16);
		QuaternionHelper.toFloatBuffer(rotation, rotBuf);
		modelview.load(rotBuf);
		
		//view = MatrixHelper.lookAt(new Vector3f(0.0f, 0.0f, 2.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		
		projection = new Matrix4f();
		
		/*
		program.setUniform("Kd", 0.9f, 0.5f, 0.3f);
		program.setUniform("Ld", 1.0f, 1.0f, 1.0f);
		program.setUniform("LightPosition", new Vector4f(5.0f, 5.0f, 2.0f, 1.0f));
		*/
	}

	public static void renderUniformBlock() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		//initUniformBlockBufferNew();

		// draw triangle
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		
	}

	public static void initGLUniformBlock() {
		
		Vector3f test = new Vector3f(23.4f, 14.7f, 32.5f);
		Quaternion anotherTest = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Vector3f testTest = QuaternionHelper.rotateVectorByQuaternion(test, anotherTest);
		System.out.println(testTest.x + " " + testTest.y + " " + testTest.z);
		
		
		// set the viewport
		GL11.glViewport(0, 0, DisplayHelper.windowWidth,
				DisplayHelper.windowHeight);

		// set the clear color
		GL11.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		// create vertex shader
		GLSLShader vertShader = new GLSLShader(ShaderTypes.VERTEX);
		String vertFile = Paths.SHADER_PATH.path() + "basic_uniformblock.vert";
		if (!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());

		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + "basic_uniformblock.frag";
		if (!fragShader.compileShaderFromFile(fragFile)) {
			System.out.println(fragShader.log());
		}

		// create and use program
		program = new GLSLProgram();
		program.addShader(fragShader);
		program.addShader(vertShader);
		program.link();
		program.use();
		
		program.printActiveUniforms();
		program.printActiveAttribs();
		
		initUniformBlockBufferOld();
		
		// create vertex buffer object handles
		IntBuffer vboHandles = BufferUtils.createIntBuffer(2);
		GL15.glGenBuffers(vboHandles);
		
		// create poisition data
		int positionBufferHandle = vboHandles.get(0);
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(18);
		float positionData[] = { -0.8f, -0.8f, 0.0f, 0.8f, -0.8f, 0.0f, 0.8f,
				0.8f, 0.0f, -0.8f, -0.8f, 0.0f, 0.8f, 0.8f, 0.0f, -0.8f, 0.8f,
				0.0f };
		positionBuffer.put(positionData);
		positionBuffer.rewind();
		// point the vertex buffer at the position data
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer,
				GL15.GL_STATIC_DRAW);

		// create color data
		int tcBufferHandle = vboHandles.get(1);
		FloatBuffer tcBuffer = BufferUtils.createFloatBuffer(12);
		float tcData[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
				1.0f, 1.0f, 0.0f, 1.0f };
		tcBuffer.put(tcData);
		tcBuffer.rewind();
		// point the vertex buffer at the color data
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tcBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tcBuffer,
				GL15.GL_STATIC_DRAW);

		// generate vertex array object
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);

		// enable 0 (VertexPosition) and 1 (VertexColor) - see
		// bindAttribLocation call
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		// 0 (VertexPosition) has three elements per vertex, is of type float,
		// is not normalized, the data is tightly packed (0 stride), and there's
		// no offset
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tcBufferHandle);
		// 1 (VertexColor) has three elements per vertex, is of type float, is
		// not normalized, it tightly packed, and there's no offset
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void initUniformBlockBufferNew(){
		// data
		float[] outerColor = { 0.0f, 0.0f, 1.0f, 1.0f };
		float[] innerColor = { 0.0f, 1.0f, 0.0f, 1.0f };
		float innerRadius = 0.25f, outerRadius = 0.45f;
		
		// get the location of RotationMatrix
		int innerColorLocation = GL20.glGetUniformLocation(program.getHandle(),
				"InnerColor");

		if (innerColorLocation >= 0) {
			// set the rotation matrix
			GL20.glUniform4f(innerColorLocation, innerColor[0], innerColor[1], innerColor[2], innerColor[3]);
		} else {
			System.out.println("InnerColor uniform not found");
		}
		
		// get the location of RotationMatrix
		int outerColorLocation = GL20.glGetUniformLocation(program.getHandle(),
				"OuterColor");

		if (outerColorLocation >= 0) {
			// set the rotation matrix
			GL20.glUniform4f(outerColorLocation, outerColor[0], outerColor[1], outerColor[2], outerColor[3]);
		} else {
			System.out.println("OuterColor uniform not found");
		}
		
		// get the location of RotationMatrix
		int innerRadiusLocation = GL20.glGetUniformLocation(program.getHandle(),
				"RadiusInner");

		if (innerRadiusLocation >= 0) {
			// set the rotation matrix
			GL20.glUniform1f(innerRadiusLocation, innerRadius);
		} else {
			System.out.println("InnerColor uniform not found");
		}
		
		// get the location of RotationMatrix
		int outerRadiusLocation = GL20.glGetUniformLocation(program.getHandle(),
				"RadiusOuter");

		if (outerRadiusLocation >= 0) {
			// set the rotation matrix
			GL20.glUniform1f(outerRadiusLocation, outerRadius);
		} else {
			System.out.println("OuterColor uniform not found");
		}
	}
	
	public static void initUniformBlockBufferOld(){
		// get the block's index
		int blockIndex = GL31.glGetUniformBlockIndex(program.getHandle(),
				"BlobSettings");
		
		// how many bytes is the block?
		int blockSize = GL31.glGetActiveUniformBlock(program.getHandle(),
				blockIndex, GL31.GL_UNIFORM_BLOCK_DATA_SIZE);
		// allocate a bytebuffer
		ByteBuffer blockBuffer = BufferUtils.createByteBuffer(blockSize);
		
		// this prints out 48, even though there's only 10 floats
		System.out.println(blockSize + " bytes");

		// query for offsets of each block variable
		String[] names = { "InnerColor", "OuterColor", "RadiusInner",
				"RadiusOuter" };

		// get uniform indices
		IntBuffer indices = BufferUtils.createIntBuffer(4);
		GL31.glGetUniformIndices(program.getHandle(), names, indices);

		// get uniform byte offsets
		IntBuffer offset = BufferUtils.createIntBuffer(4);
		GL31.glGetActiveUniforms(program.getHandle(), indices,
				GL31.GL_UNIFORM_OFFSET, offset);

		// data
		float[] outerColor = { 0.0f, 0.0f, 1.0f, 1.0f };
		float[] innerColor = { 0.0f, 1.0f, 0.0f, 1.0f };
		float innerRadius = 0.25f, outerRadius = 0.45f;

		// put outer color colors into the bytebuffer (add 4 * i because 4 bytes/float)
		for (int i = 0; i < innerColor.length; i++) {
			blockBuffer.putFloat(offset.get(0) + (4 * i), innerColor[i]);
		}

		// put inner colors into the bytebuffer
		for (int i = 0; i < outerColor.length; i++) {
			blockBuffer.putFloat(offset.get(1) + (4 * i), outerColor[i]);
		}

		// put inner and outer radius into bytebuffer
		blockBuffer.putFloat(offset.get(2), innerRadius);
		blockBuffer.putFloat(offset.get(3), outerRadius);
		
		blockBuffer.rewind();

		// create buffer object and copy the data
		int uboHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uboHandle);
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, blockBuffer,
				GL15.GL_DYNAMIC_DRAW);

		// bind buffer object to the uniform block
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, blockIndex, uboHandle);
	}
}
