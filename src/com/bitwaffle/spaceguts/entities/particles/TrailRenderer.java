package com.bitwaffle.spaceguts.entities.particles;

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

public class TrailRenderer {
	private int vaoHandle, numIndices;
	private Trail trail;
	
	IntBuffer vboHandles;
	
	public TrailRenderer(Trail trail){
		this.trail = trail;
		initVBO();
	}
	
	
	public void updateVBO(){
		Texture texture = trail.linkTex.texture();
		
		int numLinks = trail.chain.size();
		numIndices = (numLinks * 2) + 2;
		
		FloatBuffer vertBuf = BufferUtils.createFloatBuffer(numIndices * 3);
		FloatBuffer normBuf = BufferUtils.createFloatBuffer(numIndices * 3);
		FloatBuffer texBuf = BufferUtils.createFloatBuffer(numIndices * 2);
		
		for(int i = 0; i < numLinks; i++){
			TrailLink link = trail.chain.get(i);
			
			vertBuf.put(link.top.x);
			vertBuf.put(link.top.y);
			vertBuf.put(link.top.z);
			
			texBuf.put((float)i);
			texBuf.put(1.0f);
			
			normBuf.put((float)i);
			normBuf.put(1.0f);
			
			vertBuf.put(link.bottom.x);
			vertBuf.put(link.bottom.y);
			vertBuf.put(link.bottom.z);
			
			texBuf.put((float)i);
			texBuf.put(0.0f);
			
			normBuf.put((float)i);
			normBuf.put(0.0f);
			
			System.out.println(i + ": " + link.top + " | " + link.bottom);
		}
		System.out.println("--------------");
		
		vertBuf.rewind();
		texBuf.rewind();
		normBuf.rewind();
		
		GL30.glBindVertexArray(vaoHandle);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
	}
	
	public void initVBO(){
		Texture texture = trail.linkTex.texture();
		
		int numLinks = trail.chain.size();
		numIndices = (numLinks * 2) + 2;
		
		FloatBuffer vertBuf = BufferUtils.createFloatBuffer(numIndices * 3);
		FloatBuffer normBuf = BufferUtils.createFloatBuffer(numIndices * 3);
		FloatBuffer texBuf = BufferUtils.createFloatBuffer(numIndices * 2);
		
		for(int i = 0; i < numLinks; i++){
			TrailLink link = trail.chain.get(i);
			
			vertBuf.put(link.top.x);
			vertBuf.put(link.top.y);
			vertBuf.put(link.top.z);
			
			texBuf.put((float)i);
			texBuf.put(1.0f);
			
			normBuf.put((float)i);
			normBuf.put(1.0f);
			
			vertBuf.put(link.bottom.x);
			vertBuf.put(link.bottom.y);
			vertBuf.put(link.bottom.z);
			
			texBuf.put((float)i);
			texBuf.put(0.0f);
			
			normBuf.put((float)i);
			normBuf.put(0.0f);
		}
		
		vertBuf.rewind();
		texBuf.rewind();
		normBuf.rewind();
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		vboHandles = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(vboHandles);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(2);
	}
	
	public void draw(){
		trail.linkTex.texture().bind();
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_QUAD_STRIP, 0, numIndices);
	}
}
