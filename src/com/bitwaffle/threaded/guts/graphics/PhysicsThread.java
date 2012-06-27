package com.bitwaffle.threaded.guts.graphics;

import com.bitwaffle.threaded.guts.RenderThread;
import com.bitwaffle.threaded.guts.Runner;

public class PhysicsThread implements Runnable{
	@Override
	public void run() {
		init();
		
		while(!Runner.done){
			long oldTime = System.currentTimeMillis();
			
			//if(RenderThread.queueLock.tryLock()){
			for(int i = 0; i < 10; i++){
				
				try {
					Thread.sleep(10l);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					//System.out.printf("physics got lock\n");
					RenderThread.addEvent(new GLEvent(){
						@Override
						public void takeAction(){
							System.out.printf("|");
						}
					});
				//}
			
			//RenderThread.queueLock.unlock();
			}
			
			long newTime = System.currentTimeMillis();
			System.out.printf("physics loop took %d milliseconds\n", newTime - oldTime);
		}
	}
	
	private void init(){
		
	}
}
