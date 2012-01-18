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
		GL11.glViewport(0, 0, DisplayHelper.windowWidth, DisplayHelper.windowHeight);
		GL11.glClearColor(0.2f,0.2f,0.2f,1.0f);
		
		GLSLProgram vert1 = new GLSLProgram();
		if(!vert1.compileShaderFromFile(Paths.SHADER_PATH.path() + "basic.vert", ShaderTypes.VERTEX)){
			System.out.println(vert1.log());
		}
		
		if(!vert1.compileShaderFromFile(Paths.SHADER_PATH.path() + "basic.frag", ShaderTypes.FRAGMENT)){
			System.out.println(vert1.log());
		}
		
		vert1.link();
		vert1.use();
		
		IntBuffer vboHandles = BufferUtils.createIntBuffer(2);
		GL15.glGenBuffers(vboHandles);
		
		int positionBufferHandle = vboHandles.get(0);
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(9);
		float[] positionData = {
				-0.8f, -0.8f, 0.0f,
		         0.8f, -0.8f, 0.0f,
		         0.0f,  0.8f, 0.0f };
		positionBuffer.put(positionData);
		positionBuffer.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
		
		int colorBufferHandle = vboHandles.get(1);
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(9);
		float[] colorData = {
		        1.0f, 0.0f, 0.0f,
		        0.0f, 1.0f, 0.0f,
		        0.0f, 0.0f, 1.0f };
		colorBuffer.put(colorData);
		colorBuffer.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		FloatBuffer positionOffset = BufferUtils.createFloatBuffer(1);
		positionOffset.put(0);
		positionOffset.rewind();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		
		
	}
}
