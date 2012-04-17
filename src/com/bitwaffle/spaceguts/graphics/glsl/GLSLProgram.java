package com.bitwaffle.spaceguts.graphics.glsl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class GLSLProgram {
	private int handle;
	private String logString;
	private boolean linked;
	
	private FloatBuffer matrix4fBuffer = BufferUtils.createFloatBuffer(16);
	private FloatBuffer matrix3fBuffer = BufferUtils.createFloatBuffer(9);

	public GLSLProgram() {
		handle = GL20.glCreateProgram();

		if (handle == 0)
			System.out.println("Error creating shader program!!!");

		linked = false;
	}

	public void addShader(GLSLShader shader) {
		GL20.glAttachShader(handle, shader.getHandle());
	}

	public boolean link() {
		if (linked)
			return true;
		if (handle <= 0)
			return false;

		GL20.glLinkProgram(handle);

		int status = GL20.glGetProgram(handle, GL20.GL_LINK_STATUS);
		if (status == GL11.GL_FALSE) {
			int length = GL20.glGetProgram(handle, GL20.GL_INFO_LOG_LENGTH);
			logString = "";

			if (length > 0) {
				logString = GL20.glGetProgramInfoLog(handle, length);
			}

			return false;
		} else {
			linked = true;
			return linked;
		}
	}
	
	public void use(){
		if(handle <= 0 || !linked)
			return;
		GL20.glUseProgram(handle);
	}
	
	public String log() { return logString; }
	public int getHandle() { return handle; }
	public boolean isLinked() { return linked; }
	
	public void bindAttribLocation(int location, String name){
		GL20.glBindAttribLocation(handle, location, name);
		
	}
	
	public void bindFragDataLocation(int location, String name){
		GL30.glBindFragDataLocation(handle, location, name);
		
	}
	
	public void setUniform(String name, float x, float y, float z){
		int loc = getUniformLocation(name);
		if(loc >= 0){
			GL20.glUniform3f(loc, x, y, z);
		} else{
			System.out.println("Uniform variable " + name + " not found!");
		}
	}
	
	public void setUniform(String name, Vector3f v){
		this.setUniform(name, v.x, v.y, v.z);
		
	}
	
	public void setUniform(String name, float x, float y, float z, float w){
		int loc = getUniformLocation(name);
		if(loc >= 0){
			GL20.glUniform4f(loc, x, y, z, w);
		} else{
			System.out.println("Uniform variable " + name + " not found!");
		}
	}
	
	public void setUniform(String name, Vector4f v){
		this.setUniform(name, v.x, v.y, v.z, v.w);
	}
	
	public void setUniform(String name, Matrix4f m){
		int loc = getUniformLocation(name);
		if(loc >= 0){
			matrix4fBuffer.clear();
			m.store(matrix4fBuffer);
			matrix4fBuffer.rewind();
			GL20.glUniformMatrix4(loc, false, matrix4fBuffer);
		} else{
			System.out.println("Uniform variable " + name + " not found!");
		}
	}
	
	public void setUniform(String name, Matrix3f m){
		int loc = getUniformLocation(name);
		if(loc >= 0){
			matrix3fBuffer.clear();
			m.store(matrix3fBuffer);
			matrix3fBuffer.rewind();
			GL20.glUniformMatrix4(loc, false, matrix3fBuffer);
		} else{
			System.out.println("Uniform variable " + name + " not found!");
		}
	}
	
	public void setUniform(String name, float val){
		int loc = getUniformLocation(name);
		if(loc >= 0){
			GL20.glUniform1f(loc, val);
		} else{
			System.out.println("Uniform variable " + name + " not found!");
		}
	}
	
	public void setUniform(String name, int val){
		int loc = getUniformLocation(name);
		if(loc >= 0){
			GL20.glUniform1i(loc, val);
		} else{
			System.out.println("Uniform variable " + name + " not found!");
		}
	}
	
	public void setUniform(String name, boolean val){
		int loc = getUniformLocation(name);
		if(loc >= 0){
			GL20.glUniform1i(loc, val ? 1 : 0);
		} else{
			System.out.println("Uniform variable " + name + " not found!");
		}
	}
	
	public void printActiveUniforms(){
		int nUniforms, location, maxLen;
		String name;
		
		maxLen = GL20.glGetProgram(handle, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
		nUniforms = GL20.glGetProgram(handle, GL20.GL_ACTIVE_UNIFORMS);
		
		System.out.println("\n Active Uniforms");
		System.out.println("------------------------------------------------");
		for(int i = 0; i < nUniforms; i++){
			name = GL20.glGetActiveUniform(handle, i, maxLen);
			location = GL20.glGetUniformLocation(handle, name);
			System.out.println("  " + location + " | " + name);
		}
	}
	
	public void printActiveAttribs(){
		int location, maxLength, nAttribs;
		String name;
		
		maxLength = GL20.glGetProgram(handle, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);
		nAttribs = GL20.glGetProgram(handle, GL20.GL_ACTIVE_ATTRIBUTES);
		
		System.out.println("\n Active Atributes");
		System.out.println("------------------------------------------------");
		for(int i = 0; i < nAttribs; i++){
			name = GL20.glGetActiveAttrib(handle, i, maxLength);
			location = GL20.glGetAttribLocation(handle, name);
			System.out.println(" " + location + " | " + name);
		}
	}
	
	public int getUniformLocation(String name){
		return GL20.glGetUniformLocation(handle, name);
	}
	
	public int getSubroutineIndex(String subroutine){
		byte[] bytes = (subroutine + "\u0000").getBytes();
		ByteBuffer phongBuf = BufferUtils.createByteBuffer(bytes.length + 1);
		phongBuf.put(bytes);
		phongBuf.rewind();
		
		return GL40.glGetSubroutineIndex(this.getHandle(), GL20.GL_VERTEX_SHADER, phongBuf);
	}
	
	public void useVertexSubRoutines(int[] subroutines){
		IntBuffer buf = BufferUtils.createIntBuffer(subroutines.length);
		buf.put(subroutines);
		buf.rewind();
		
		GL40.glUniformSubroutinesu(GL20.GL_VERTEX_SHADER, buf);
	}

	public void useFragmentSubRoutines(int[] subroutines){
		IntBuffer buf = BufferUtils.createIntBuffer(subroutines.length);
		buf.put(subroutines);
		buf.rewind();
		
		GL40.glUniformSubroutinesu(GL20.GL_FRAGMENT_SHADER, buf);
	}
}
