package spaceguts.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import spaceguts.entities.Entities;
import spaceguts.graphics.render.Render3D;
import spaceguts.graphics.render.Render2D;

public class Graphics {
	public static void render(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		if (Entities.entitiesExist() && Entities.camera != null)
			Render3D.render3DScene();
		
		Render2D.draw2DScene();
		
		// handle any GL errors that might occur
		int error;

		while ((error = GL11.glGetError()) != GL11.GL_NO_ERROR)
			System.out.println("Error rendering! Error number: " + error
					+ "/String: " + GLU.gluGetString(error));
	}
	
	public static void initGL(){
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearDepth(1.0f);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		Render3D.init();
	}
}
