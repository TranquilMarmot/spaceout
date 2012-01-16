package spaceguts.graphics.glsl;

import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.GL20;

public enum ShaderTypes {
	VERTEX(GL20.GL_VERTEX_SHADER),
	FRAGMENT(GL20.GL_FRAGMENT_SHADER),
	GEOMETRY(ARBGeometryShader4.GL_GEOMETRY_SHADER_ARB),
	TESS_CONTROL(0),
	TESS_EVALUATION(1);

	
	private int glInt;
	private ShaderTypes(int glInt){
		this.glInt = glInt;
	}
	
	public int getGLType(){
		return glInt;
	}
}
