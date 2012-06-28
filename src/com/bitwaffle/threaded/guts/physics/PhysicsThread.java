package com.bitwaffle.threaded.guts.physics;

import com.bitwaffle.threaded.guts.Runner;
import com.bitwaffle.threaded.guts.threads.EventThread;
import com.bitwaffle.threaded.guts.threads.ThreadManager;
import com.bitwaffle.threaded.guts.threads.events.GLEvent;
import com.bitwaffle.threaded.guts.threads.events.PhysicsEvent;

public class PhysicsThread extends EventThread<PhysicsEvent>{
	private PhysicsWorld world;

	public PhysicsThread(){
		this.setName("Physics");
		init();
	}

	@Override
	public void run() {
		init();
		
		while(!Runner.done){
			long oldTime = System.currentTimeMillis();
			
			processEvents();
			
			//if(RenderThread.queueLock.tryLock()){
			for(int i = 0; i < 10; i++){
				
				try {
					Thread.sleep(2l);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				ThreadManager.addEvent(new GLEvent(){
					@Override
					public void takeAction(){
						System.out.printf("|");
					}
				});
			}
			
			long newTime = System.currentTimeMillis();
			//System.out.printf("physics loop took %d milliseconds\n", newTime - oldTime);
		}
	}
	
	private void init(){
		// TODO new event-driven callback
		world = new PhysicsWorld(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f), 10, null);
	}
}
