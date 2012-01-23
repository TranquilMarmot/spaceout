package spaceguts.graphics.glsl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
	
	public GLSLModel(CollisionShape collisionShape, ObjectArrayList<Vector3f> vertices, ObjectArrayList<int[]> indices){
		this.collisionShape = collisionShape;
		
		numIndices = indices.size() / 3;
		System.out.println(numIndices);
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		IntBuffer handle = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(handle);
		
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
		for(Vector3f v : vertices){
			System.out.println(v.x + " " + v.y + " " + v.z);
			vertBuffer.put(v.x);
			vertBuffer.put(v.y);
			vertBuffer.put(v.z);
		}
		vertBuffer.rewind();
		
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.size() * 3);
		for(int[] i : indices){
			System.out.println(i[0] + " " + i[1] + " " + i[2]);
			indexBuffer.put(i[0]);
			indexBuffer.put(i[1]);
			indexBuffer.put(i[2]);
		}
		indexBuffer.rewind();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(vertices.size());
		for(int i = 0; i < vertices.size(); i += 3){
			colorBuffer.put(i, 0.0f);
			colorBuffer.put(i + 1, 1.0f);
			colorBuffer.put(i + 2, 0.0f);
		}
		colorBuffer.rewind();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
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
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, handle.get(2));
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
	}
	
	public CollisionShape getCollisionShape(){
		return collisionShape;
	}
	
	public void render(){
		GL30.glBindVertexArray(vaoHandle);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, numIndices, GL11.GL_UNSIGNED_INT, 0L);
	}	
}
