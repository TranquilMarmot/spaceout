package spaceguts.graphics.glsl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.bulletphysics.collision.shapes.CollisionShape;

public class GLSLModel {
	private int vaoHandle, numIndices;
	private CollisionShape collisionShape;
	
	public GLSLModel(CollisionShape collisionShape, FloatBuffer vertices, FloatBuffer normals, FloatBuffer textureCoords, IntBuffer indices){
		this.collisionShape = collisionShape;
		
		numIndices = indices.capacity();
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		IntBuffer handle = BufferUtils.createIntBuffer(2);
		GL15.glGenBuffers(handle);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
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
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, handle.get(1));
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
	}
	
	public CollisionShape getCollisionShape(){
		return collisionShape;
	}
	
	public void render(){
		GL30.glBindVertexArray(vaoHandle);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, numIndices, GL11.GL_UNSIGNED_INT, 0L);
	}
	
	
}
