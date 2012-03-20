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
	
	FloatBuffer vertBuf, texBuf/*, normBuf*/;
	IntBuffer vboHandles;
	
	public TrailRenderer(Trail trail){
		this.trail = trail;
		initVBO();
	}
	
	
	public void updateVBO(){
		Texture texture = trail.linkTex.texture();
		
		int numLinks = trail.chain.size();
		
		Vector3f[] vertices = new Vector3f[numLinks * 4];
		Point2f[] texCoords = new Point2f[numLinks * 4];
		
		for(int i = 0; i < numLinks * 4; i += 4){
			TrailLink link = trail.chain.get(i / 4);
			
			vertices[i] = link.start1;
			vertices[i + 1] = link.start2;
			vertices[i + 2] = link.end1;
			vertices[i + 3] = link.end2;
			
			texCoords[i] = new Point2f(0.0f, 0.0f);
			texCoords[i + 1] = new Point2f(texture.getWidth(), 0.0f);
			texCoords[i + 2] = new Point2f(texture.getWidth(), texture.getHeight());
			texCoords[i + 3] = new Point2f(0.0f, texture.getHeight());
		}
		
		/*
		vertices[0] = new Vector3f(0.0f, 0.0f, 0.0f);
		texCoords[0] = new Point2f(0.0f, 0.0f);
		
		vertices[1] = new Vector3f(width, 0.0f, 0.0f);
		texCoords[1] = new Point2f(texture.getWidth(), 0.0f);
		
		vertices[2] = new Vector3f(width, height, 0.0f);
		texCoords[2] = new Point2f(texture.getWidth(), texture.getHeight());
		
		vertices[3] = new Vector3f(0.0f, height, 0.0f);
		texCoords[3] = new Point2f(0.0f, texture.getHeight());
		*/
		
		numIndices = numLinks * 4;
		
		//vertBuf = BufferUtils.createFloatBuffer(numLinks * 4 * 3);
		//normBuf = BufferUtils.createFloatBuffer(numLinks * 4);
		//texBuf = BufferUtils.createFloatBuffer(numLinks * 4 * 2);
		
		vertBuf.clear();
		texBuf.clear();
		
		for(int i = 0; i < numLinks * 4; i++){
			Vector3f vert = vertices[i];
			vertBuf.put(vert.x);
			vertBuf.put(vert.y);
			vertBuf.put(vert.z);
			
			Point2f tex = texCoords[i];
			texBuf.put(tex.x);
			texBuf.put(tex.y);
			
			/*
			normBuf.put(tex.x);
			normBuf.put(tex.y);
			normBuf.put(0.0f);
			*/
		}
		
		vertBuf.rewind();
		texBuf.rewind();
		//normBuf.rewind();
		
		//vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		//vboHandles = BufferUtils.createIntBuffer(3);
		//GL15.glGenBuffers(vboHandles);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		/*
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		*/
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(2);
	}
	
	private void initVBO(){
		Texture texture = trail.linkTex.texture();
		
		int numLinks = trail.chain.size();
		
		Vector3f[] vertices = new Vector3f[numLinks * 4];
		Point2f[] texCoords = new Point2f[numLinks * 4];
		
		for(int i = 0; i < numLinks * 4; i += 4){
			TrailLink link = trail.chain.get(i / 4);
			
			vertices[i] = link.start1;
			vertices[i + 1] = link.start2;
			vertices[i + 2] = link.end1;
			vertices[i + 3] = link.end2;
			
			texCoords[i] = new Point2f(0.0f, 0.0f);
			texCoords[i + 1] = new Point2f(texture.getWidth(), 0.0f);
			texCoords[i + 2] = new Point2f(texture.getWidth(), texture.getHeight());
			texCoords[i + 3] = new Point2f(0.0f, texture.getHeight());
		}
		
		/*
		vertices[0] = new Vector3f(0.0f, 0.0f, 0.0f);
		texCoords[0] = new Point2f(0.0f, 0.0f);
		
		vertices[1] = new Vector3f(width, 0.0f, 0.0f);
		texCoords[1] = new Point2f(texture.getWidth(), 0.0f);
		
		vertices[2] = new Vector3f(width, height, 0.0f);
		texCoords[2] = new Point2f(texture.getWidth(), texture.getHeight());
		
		vertices[3] = new Vector3f(0.0f, height, 0.0f);
		texCoords[3] = new Point2f(0.0f, texture.getHeight());
		*/
		
		numIndices = numLinks * 4;
		
		if(numLinks == 0){
			
		} else{
			
		}
		
		vertBuf = BufferUtils.createFloatBuffer(numLinks * 4 * 3);
		//normBuf = BufferUtils.createFloatBuffer(numLinks * 4);
		texBuf = BufferUtils.createFloatBuffer(numLinks * 4 * 2);
		
		for(int i = 0; i < numLinks * 4; i++){
			Vector3f vert = vertices[i];
			vertBuf.put(vert.x);
			vertBuf.put(vert.y);
			vertBuf.put(vert.z);
			
			Point2f tex = texCoords[i];
			texBuf.put(tex.x);
			texBuf.put(tex.y);
			
			/*
			normBuf.put(tex.x);
			normBuf.put(tex.y);
			normBuf.put(0.0f);
			*/
		}
		
		vertBuf.rewind();
		texBuf.rewind();
		//normBuf.rewind();
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		vboHandles = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(vboHandles);
		
		try{
			vertBuf.get(0);
		} catch(IndexOutOfBoundsException e){
			vertBuf.put(0.0f);
		}
		
		try{
			texBuf.get(0);
		} catch(IndexOutOfBoundsException e){
			texBuf.put(0.0f);
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		/*
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuf, GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		*/
		
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
