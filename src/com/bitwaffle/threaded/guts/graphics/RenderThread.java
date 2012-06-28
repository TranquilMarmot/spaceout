package com.bitwaffle.threaded.guts.graphics;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.bitwaffle.threaded.guts.Runner;
import com.bitwaffle.threaded.guts.threads.EventThread;
import com.bitwaffle.threaded.guts.threads.events.GLEvent;

public class RenderThread extends EventThread<GLEvent>{
	
	public RenderThread(){
		this.setName("Render");
	}
	
	@Override
	public void run() {
		init();
		
		while(!Runner.done){
			long oldTime = System.currentTimeMillis();
			
			processEvents();
			
			DisplayHelper.resizeWindow();
			Display.update();
			
			long newTime = System.currentTimeMillis();
			//System.out.printf("render loop took %d milliseconds\n", newTime - oldTime);
		}
		cleanup();
	}
	
	public void init(){
		DisplayHelper.createWindow();
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		
		// enable depth testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		// enable multisampling
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearDepth(1.0f);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	}
	
	public void cleanup(){
		Display.destroy();
		DisplayHelper.frame.dispose();
	}
}
