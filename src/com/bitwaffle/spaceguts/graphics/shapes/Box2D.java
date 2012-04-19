package com.bitwaffle.spaceguts.graphics.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Point2f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Box2D {
	private int vaoHandle;
	
	public Box2D(float width, float height, Texture texture){
		initVBO(width, height, texture);
	}
	
	private void initVBO(float width, float height, Texture texture){
		Vector3f[] vertices = new Vector3f[4];
		Point2f[] texCoords = new Point2f[4];
		
		width = width / 2;
		height = height /2;
		
		vertices[0] = new Vector3f(-width, -height, 0.0f);
		texCoords[0] = new Point2f(0.0f, 0.0f);
		
		vertices[1] = new Vector3f(width, -height, 0.0f);
		texCoords[1] = new Point2f(texture.getWidth(), 0.0f);
		
		vertices[2] = new Vector3f(width, height, 0.0f);
		texCoords[2] = new Point2f(texture.getWidth(), texture.getHeight());
		
		vertices[3] = new Vector3f(-width, height, 0.0f);
		texCoords[3] = new Point2f(0.0f, texture.getHeight());
		
		FloatBuffer vertBuf = BufferUtils.createFloatBuffer(12);
		FloatBuffer normBuf = BufferUtils.createFloatBuffer(12);
		FloatBuffer texBuf = BufferUtils.createFloatBuffer(8);
		
		for(int i = 0; i < 4; i++){
			Vector3f vert = vertices[i];
			vertBuf.put(vert.x);
			vertBuf.put(vert.y);
			vertBuf.put(vert.z);
			
			Point2f tex = texCoords[i];
			texBuf.put(tex.x);
			texBuf.put(tex.y);
			
			normBuf.put(tex.x);
			normBuf.put(tex.y);
			normBuf.put(0.0f);
		}
		
		vertBuf.rewind();
		texBuf.rewind();
		normBuf.rewind();
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		IntBuffer vboHandles = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(vboHandles);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(2);
	}

	public void draw(){
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
	}
	
	public int getVAOHandle(){
		return vaoHandle;
	}
}
