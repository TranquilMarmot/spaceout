package spaceguts.graphics.glsl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import spaceguts.util.DisplayHelper;
import spaceguts.util.resources.Paths;

public class GLSLRender {
	static int vaoHandle = 0;
	public static void render(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
	}
	
	public static void initGL(){
		// set the viewport
		GL11.glViewport(0, 0, DisplayHelper.windowWidth, DisplayHelper.windowHeight);
		
		// set the clear color
		GL11.glClearColor(0.2f,0.2f,0.2f,1.0f);
		
		// create vertex shader
		GLSLShader vertShader = new GLSLShader(ShaderTypes.VERTEX);
		String vertFile = Paths.SHADER_PATH.path() + "basic.vert";
		if(!vertShader.compileShaderFromFile(vertFile))
			System.out.println(vertShader.log());
		
		// create fragment shader
		GLSLShader fragShader = new GLSLShader(ShaderTypes.FRAGMENT);
		String fragFile = Paths.SHADER_PATH.path() + "basic.frag";
		if(!fragShader.compileShaderFromFile(fragFile)){
			System.out.println(fragShader.log());
		}
		
		// create and use program
		GLSLProgram program = new GLSLProgram();
		program.addShader(fragShader);
		program.addShader(vertShader);
		program.link();
		program.use();
		
		// bind data locations
		program.bindAttribLocation(0, "VertexPosition");
		program.bindAttribLocation(1, "VertexColor");
		program.bindFragDataLocation(0, "FragColor");
		
		/* BEGIN VERTEX ARRAY */
		// create vertex buffer object handles
		IntBuffer vboHandles = BufferUtils.createIntBuffer(2);
		GL15.glGenBuffers(vboHandles);
		
		// create poisition data
		int positionBufferHandle = vboHandles.get(0);
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(9);
		float[] positionData = {
				-0.8f, -0.8f, 0.0f,
		         0.8f, -0.8f, 0.0f,
		         0.0f,  0.8f, 0.0f };
		positionBuffer.put(positionData);
		positionBuffer.rewind();
		// point the vertex buffer at the position data
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
		
		// create color data
		int colorBufferHandle = vboHandles.get(1);
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(9);
		float[] colorData = {
		        1.0f, 0.0f, 0.0f,
		        0.0f, 1.0f, 0.0f,
		        0.0f, 0.0f, 1.0f };
		colorBuffer.put(colorData);
		colorBuffer.rewind();
		// point the vertex buffer at the color data
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
		
		// generate vertex array object
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		// enable 0 (VertexPosition) and 1 (VertexColor) - see bindAttribLocation call
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		// 0 (VertexPosition) has three elements per vertex, is of type float, is not normalized, the data is tightly packed (0 stride), and there's no offset
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		// 1 (VertexColor) has three elements per vertex, is of type float, is not normalized, it tightly packed, and there's no offset
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
	}
}
