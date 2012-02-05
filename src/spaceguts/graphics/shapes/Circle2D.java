package spaceguts.graphics.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.vecmath.Point2f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

public class Circle2D {
	private int numIndices, vaoHandle;
	public Circle2D(float step, float size){
		initVBO(step, size);
	}
	
	private void initVBO(float step, float size){
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		ArrayList<Point2f> texCoords = new ArrayList<Point2f>();
		
		for(float angle = 0.0f; angle <= 360; angle += step){
			float x = (float) Math.sin(angle) * size;
			float y = (float) Math.cos(angle) * size;
			float z = 0.0f;
			vertices.add(new Vector3f(x, y, z));
			
			float u = (float) Math.sin(angle);
			float v = (float) Math.cos(angle);
			texCoords.add(new Point2f(u, v));
		}
		
		numIndices = vertices.size() * 3;
		
		FloatBuffer vertBuf = BufferUtils.createFloatBuffer(vertices.size() * 3);
		FloatBuffer texBuf = BufferUtils.createFloatBuffer(texCoords.size() * 2);
		
		for(int i = 0; i < vertices.size(); i++){
			Vector3f vert = vertices.get(i);
			vertBuf.put(vert.x);
			vertBuf.put(vert.y);
			vertBuf.put(vert.z);
			
			Point2f tex = texCoords.get(i);
			texBuf.put(tex.x);
			texBuf.put(tex.y);
		}
		
		vertBuf.rewind();
		texBuf.rewind();
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		IntBuffer vboHandles = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(vboHandles);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
	}
	
	public void draw(){
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, numIndices);
	}
}
