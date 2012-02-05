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

public class VBOQuadric{
	private int vaoHandle, numIndices;
	public VBOQuadric(float radius, int slices, int stacks){
		ArrayList<javax.vecmath.Vector3f> vertices = new ArrayList<javax.vecmath.Vector3f>();
		ArrayList<javax.vecmath.Vector3f> normals = new ArrayList<javax.vecmath.Vector3f>();
		ArrayList<Point2f> texCoords = new ArrayList<Point2f>();
		
		float rho, theta;
		float drho = (float)(Math.PI / (double)stacks);
		float dtheta = (float)(2.0 * Math.PI / (double)slices);
		
		float s;
		float ds = 1.0f / (float)slices;
		float dt = 1.0f / (float)stacks;
		float t = 1.0f;
		
		for(int i = 0; i < stacks; i++){
			rho = i * drho;
			s = 0.0f;
			for(int j = 0; j <= slices; j++){
				theta = (j == slices) ? 0.0f : j * dtheta;
				float x = (float)(-Math.sin((double)theta) * Math.sin((double)rho));
				float y = (float)(Math.cos((double)theta) * Math.sin((double)rho));
				float z = (float)(Math.cos((double)rho));
				
				normals.add(new javax.vecmath.Vector3f(x, y, z));
				texCoords.add(new Point2f(s, t));
				vertices.add(new javax.vecmath.Vector3f(x * radius, y * radius, z * radius));
				
				
				x = (float)(-Math.sin(theta) * Math.sin(rho + drho));
				y = (float)(Math.cos(theta) * Math.sin(rho + drho));
				z = (float)(Math.cos(rho + drho));
				
				normals.add(new javax.vecmath.Vector3f(x, y, z));
				texCoords.add(new Point2f(s, t - dt));
				vertices.add(new javax.vecmath.Vector3f(x * radius, y * radius, z * radius));
				
				s += ds;
			}
			t -= dt;
		}
		
		numIndices = vertices.size();
		
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
		FloatBuffer normBuffer = BufferUtils.createFloatBuffer(normals.size() * 3);
		FloatBuffer texBuffer = BufferUtils.createFloatBuffer(texCoords.size() * 2);
		
		for(int i = 0; i < vertices.size(); i ++){
			javax.vecmath.Vector3f vert0 = vertices.get(i);
			javax.vecmath.Vector3f norm0 = normals.get(i);
			Point2f texCoord0 = texCoords.get(i);
			
			vertBuffer.put(vert0.x);
			vertBuffer.put(vert0.y);
			vertBuffer.put(vert0.z);
			normBuffer.put(norm0.x);
			normBuffer.put(norm0.y);
			normBuffer.put(norm0.z);
			texBuffer.put(texCoord0.x);
			texBuffer.put(texCoord0.y);
		}
		
		vertBuffer.rewind();
		normBuffer.rewind();
		texBuffer.rewind();
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		IntBuffer vboHandles = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(vboHandles);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(2);
	}
	
	public void draw(){
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_QUAD_STRIP, 0, numIndices);
	}
}
