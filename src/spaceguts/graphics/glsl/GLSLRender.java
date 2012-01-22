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

import spaceguts.graphics.render.Render3D;
import spaceguts.util.DisplayHelper;
import spaceguts.util.MatrixHelper;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.resources.Paths;
import spaceguts.util.resources.Textures;

public class GLSLRender {
	static int vaoHandle = 0;

	private static GLSLProgram program;
	private static float angle = 45.0f;
	private static Matrix4f projection, modelview;
	private static GLSLModel model;
	
	public static void render(){
		program.use();
		
		Matrix4f mvp = new Matrix4f();
		Matrix4f.mul(modelview, projection, mvp);
		
		//program.setUniform("MVP", mvp);
		
		model.render();
	}
	
	public static void initGL(){
		GL11.glViewport(0, 0, DisplayHelper.windowWidth,
				DisplayHelper.windowHeight);
		
		projection = new Matrix4f();
		
		// calculate the current aspect ratio
		float aspect = (float) DisplayHelper.windowWidth
				/ (float) DisplayHelper.windowHeight;
		
		projection = MatrixHelper.perspective(45.0f, aspect, 1.0f, Render3D.drawDistance);
		
		modelview = new Matrix4f();
		
		model = GLSLModelLoader.loadObjFile(Paths.MODEL_PATH.path() + "ships/wing_x.obj", Textures.SHIP1);
		
		// create vertex shader
		GLSLShader vertShader = new GLSLShader(ShaderTypes.VERTEX);
		String vertFile = Paths.SHADER_PATH.path() + "model.vert";
		if (!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());

		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + "model.frag";
		if (!fragShader.compileShaderFromFile(fragFile)) {
			System.out.println(fragShader.log());
		}
		
		program = new GLSLProgram();
		program.addShader(vertShader);
		program.addShader(fragShader);
		program.link();
		
		program.printActiveAttribs();
		program.printActiveUniforms();
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

	public static void renderVertexArray() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		// create a new matrix
		Matrix4f rotationMatrix = new Matrix4f();
		// set it to the identity matrix
		rotationMatrix.setIdentity();
		// rotate it by angle along the Z axis
		rotationMatrix = rotationMatrix.rotate((float) Math
				.toDegrees((double) angle), new Vector3f(0.0f, 0.0f, 1.0f));
		// increase angle
		// angle += 0.05f;

		// buffer for transferring matrix to shader
		FloatBuffer rotBuffer = BufferUtils.createFloatBuffer(16);

		// create a quaternion and rotate it
		Quaternion quat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		quat = QuaternionHelper.rotateZ(quat, angle);
		QuaternionHelper.toFloatBuffer(quat, rotBuffer);

		// get the location of RotationMatrix
		int location = GL20.glGetUniformLocation(program.getHandle(),
				"RotationMatrix");

		if (location >= 0) {
			// set the rotation matrix
			GL20.glUniformMatrix4(location, false, rotBuffer);
		} else {
			System.out.println("RotationMatrix uniform not found");
		}

		// draw triangle
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
	}

	public static void initGLVertexArray() {
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

		/* BEGIN VERTEX ARRAY */
		// create vertex buffer object handles
		IntBuffer vboHandles = BufferUtils.createIntBuffer(2);
		GL15.glGenBuffers(vboHandles);

		// create poisition data
		int positionBufferHandle = vboHandles.get(0);
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(9);
		float[] positionData = { -0.8f, -0.8f, 0.0f, 0.8f, -0.8f, 0.0f, 0.0f,
				0.8f, 0.0f };
		positionBuffer.put(positionData);
		positionBuffer.rewind();
		// point the vertex buffer at the position data
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer,
				GL15.GL_STATIC_DRAW);

		// create color data
		int colorBufferHandle = vboHandles.get(1);
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(9);
		float[] colorData = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				1.0f };
		colorBuffer.put(colorData);
		colorBuffer.rewind();
		// point the vertex buffer at the color data
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer,
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

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		// 1 (VertexColor) has three elements per vertex, is of type float, is
		// not normalized, it tightly packed, and there's no offset
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
	}
}
