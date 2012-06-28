package com.bitwaffle.spaceguts.graphics.glsl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GLSLShader {
	private int handle;
	private String logString;
	
	public GLSLShader(ShaderTypes type){
		handle = GL20.glCreateShader(type.getGLInt());
		
		if(handle == 0){
			System.out.println("Error creating shader handle!");
		}
	}
	
	public boolean compileShaderFromFile(String fileName){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String source = "";
			String line;
			while ((line = reader.readLine()) != null) {
				source += line + "\n";
			}
			reader.close();
			return compileShaderFromString(source);
		} catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean compileShaderFromString(String source){
		GL20.glShaderSource(handle, source);
		GL20.glCompileShader(handle);
		
		int result = GL20.glGetShader(handle, GL20.GL_COMPILE_STATUS);
		if(result == GL11.GL_FALSE){
			int length = GL20.glGetShader(handle, GL20.GL_INFO_LOG_LENGTH);
			logString = "";
			if(length > 0){
				logString = GL20.glGetShaderInfoLog(handle, length);
			}
			
			return false;
		} else{
			//GL20.glAttachShader(handle, shaderHandle);
			return true;
		}
	}
	
	public String log() { return logString; }
	public int getHandle() { return handle; }
}
