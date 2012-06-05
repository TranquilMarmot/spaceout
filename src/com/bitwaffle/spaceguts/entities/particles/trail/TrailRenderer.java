package com.bitwaffle.spaceguts.entities.particles.trail;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.bitwaffle.spaceout.resources.Textures;

/**
 * Handles creating and updating the vertex arrays for the trail
 * @author TranquilMarmot
 *
 */
public class TrailRenderer {
	/** Info for rendering */
	private int vaoHandle, numIndices;
	
	/** The trail we're rendering */
	private Trail trail;
	
	/** Handles for vertex buffer objects */
	IntBuffer vboHandles;
	
	private Textures texture;
	
	private FloatBuffer vertBuf, normBuf, texBuf;
	
	/**
	 * @param trail Trail to render with this renderer
	 */
	public TrailRenderer(Trail trail, Textures texture){
		this.trail = trail;
		this.texture = texture;
		initVBO();
	}
	
	/**
	 * Updates the vertex arrays to contain the most recent trail data
	 */
	public void updateVBO(){
		// clear buffers
		vertBuf.clear();
		normBuf.clear();
		texBuf.clear();
		
		// iterate through every link
		for(int i = 0; i < trail.chain.size(); i++){
			TrailLink link = trail.chain.get(i);
			
			vertBuf.put(link.top.x);
			vertBuf.put(link.top.y);
			vertBuf.put(link.top.z);
			
			// see the initVBO() method for an explanation of this
			texBuf.put((float)i * texture.texture().getWidth());
			texBuf.put(texture.texture().getHeight());
			
			normBuf.put((float)i * texture.texture().getWidth());
			normBuf.put(texture.texture().getHeight());
			
			vertBuf.put(link.bottom.x);
			vertBuf.put(link.bottom.y);
			vertBuf.put(link.bottom.z);
			
			texBuf.put((float)i * texture.texture().getWidth());
			texBuf.put(0.0f);
			
			normBuf.put((float)i * texture.texture().getWidth());
			normBuf.put(0.0f);
		}
		
		vertBuf.rewind();
		texBuf.rewind();
		normBuf.rewind();
		
		GL30.glBindVertexArray(vaoHandle);
		
		// send data to buffers
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
		// 2 indices for the first link, and 2 for every link after
		numIndices = (trail.length * 2) + 2;
		
		// 3 vertices per index, unless it's a texture coordinate
		vertBuf = BufferUtils.createFloatBuffer(numIndices * 3);
		normBuf = BufferUtils.createFloatBuffer(numIndices * 3);
		texBuf = BufferUtils.createFloatBuffer(numIndices * 2);
		
		// iterate through every link
		for(int i = 0; i < trail.chain.size(); i++){
			TrailLink link = trail.chain.get(i);
			
			vertBuf.put(link.top.x);
			vertBuf.put(link.top.y);
			vertBuf.put(link.top.z);
			
			/*
			 * In OpenGL, texture coordinates are supposed to be specified as
			 * between 0.0f and 1.0f. If the coordinates are specified as beyond
			 * 1.0f, then the textured is sampled as a loop. That is to say
			 * giving OpenGL a texture coordinate of 1.3f or 2.3f is exactly
			 * the same as specifying 0.3f.
			 * Since the trail is being rendered as a quad strip,
			 * we just give it the index of the link in the chain.
			 * So the top of the first link goes from 0.0f to 1.0f,
			 * and the second link goes from 1.0f to 2.0f and so on.
			 */
			texBuf.put((float)i * texture.texture().getWidth());
			texBuf.put(1.0f);
			
			normBuf.put((float)i * texture.texture().getWidth());
			normBuf.put(1.0f);
			
			vertBuf.put(link.bottom.x);
			vertBuf.put(link.bottom.y);
			vertBuf.put(link.bottom.z);
			
			texBuf.put((float)i * texture.texture().getWidth());
			texBuf.put(0.0f);
			
			normBuf.put((float)i * texture.texture().getWidth());
			normBuf.put(0.0f);
		}
		
		vertBuf.rewind();
		texBuf.rewind();
		normBuf.rewind();
		
		// Send the data to the buffers
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
	
	/**
	 * Draws the trail
	 */
	public void draw(){
		GL30.glBindVertexArray(vaoHandle);
		// Don't know why it be like it does, but it do
		GL11.glDrawArrays(GL11.GL_QUAD_STRIP, 0, numIndices - 1);
	}
}
