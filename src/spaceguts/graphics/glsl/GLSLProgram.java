package spaceguts.graphics.glsl;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class GLSLProgram {
	private int handle;
	private boolean linked;
	private String logString;
	
	public GLSLProgram(){
	}
	
	
	public boolean compileShaderFromFile(String fileName, ShaderTypes type){
		return false;
	}
	
	public boolean compileShaderFromString(String source, ShaderTypes type){
		return false;
	}
	
	public boolean link(){
		return false;
	}
	
	public void use(){
		
	}
	
	public String log(){
		return logString;
	}
	
	public int getHandle(){
		return handle;
	}
	
	public boolean isLinked(){
		return linked;
	}
	
	public void bindAttribLocation(int location, String name){
		
	}
	
	public void bindFragDataLocation(int location, String name){
		
	}
	
	public void setUniform(String name, float x, float y, float z){
		
	}
	
	public void setUniform(String name, Vector3f v){
		
	}
	
	public void setUniform(String name, Vector4f v){
		
	}
	
	public void setUniform(String name, Matrix4f m){
		
	}
	
	public void setUniform(String name, Matrix3f m){
		
	}
	
	public void setUniform(String name, float val){
		
	}
	
	public void setUniform(String name, int val){
		
	}
	
	public void setUniform(String name, boolean val){
		
	}
	
	public void printActiveUniforms(){
		
	}
	
	public void printActiveAttribs(){
		
	}
}
