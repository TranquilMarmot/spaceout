package spaceguts.graphics.glsl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.util.ObjectArrayList;

public class GLSLModel {
	private int vaoHandle, numIndices;
	private CollisionShape collisionShape;
	
	public GLSLModel(CollisionShape collisionShape, ObjectArrayList<Vector3f> vertices, ArrayList<int[]> indices){
		this.collisionShape = collisionShape;
		
		numIndices = indices.size() * 9;
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(indices.size() * 9);
		for(int i = 0; i < indices.size(); i++){
			int[] tri = indices.get(i);
			Vector3f first = vertices.get(tri[0]);
			vertBuffer.put(first.x);
			vertBuffer.put(first.y);
			vertBuffer.put(first.z);
			
			Vector3f second = vertices.get(tri[1]);
			vertBuffer.put(second.x);
			vertBuffer.put(second.y);
			vertBuffer.put(second.z);
			
			
			Vector3f third = vertices.get(tri[2]);
			vertBuffer.put(third.x);
			vertBuffer.put(third.y);
			vertBuffer.put(third.z);
		}
		vertBuffer.rewind();
		
		IntBuffer vboHandles = BufferUtils.createIntBuffer(2);
		GL15.glGenBuffers(vboHandles);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(indices.size() * 9);
		for(int i = 0; i < colorBuffer.capacity(); i += 3){
			colorBuffer.put(0.0f);
			colorBuffer.put(1.0f);
			colorBuffer.put(0.0f);
		}
		colorBuffer.rewind();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		/*
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normals, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureCoords, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(2);
		*/
	}
	
	public CollisionShape getCollisionShape(){
		return collisionShape;
	}
	
	public void render(){
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numIndices);
	}	
}
