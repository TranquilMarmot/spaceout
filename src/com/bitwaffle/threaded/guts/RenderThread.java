package com.bitwaffle.threaded.guts;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.bitwaffle.threaded.guts.graphics.DisplayHelper;
import com.bitwaffle.threaded.guts.graphics.GLEvent;

public class RenderThread implements Runnable{
	public static Lock queueLock;
	private static LinkedList<GLEvent> eventQueue;
	
	public RenderThread(){
		eventQueue = new LinkedList<GLEvent>();
		queueLock = new ReentrantLock();
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
			System.out.printf("render loop took %d milliseconds\n", newTime - oldTime);
		}
		cleanup();
	}
	
	private void processEvents(){
		boolean read = false;
		
		while(!read){
			if(queueLock.tryLock()){
				try{
					GLEvent event = eventQueue.poll();
					
					while(event != null){
						event.takeAction();
						event = eventQueue.poll();
					}
				} finally {
					queueLock.unlock();
					read = true;
				}
			}
		}
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
	
	public static void addEvent(final GLEvent event){
		Thread t = new Thread(new Runnable(){
			@Override
			public void run(){
				boolean eventAdded = false;
				
				while(!eventAdded){
					if(queueLock.tryLock()){
						eventQueue.add(event);
						eventAdded = true;
						queueLock.unlock();
					}
				}
			}
		});
		
		t.setName("GL addEvent");
		t.start();
	}
}
