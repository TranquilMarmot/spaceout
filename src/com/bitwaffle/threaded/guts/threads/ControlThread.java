package com.bitwaffle.threaded.guts.threads;

import org.lwjgl.input.Mouse;

import com.bitwaffle.threaded.guts.Runner;
import com.bitwaffle.threaded.guts.threads.events.ControlEvent;
import com.bitwaffle.threaded.guts.threads.events.PhysicsEvent;


public class ControlThread extends EventThread<ControlEvent> {
	@Override
	public void run() {
		while(!Runner.done){
			/*
			try {
				Thread.sleep(100l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/

			final int dx = Mouse.getDX();
			if(dx != 0){
				ThreadManager.addEvent(new PhysicsEvent(){
					@Override
					public void takeAction(){
						System.out.println("MOUSE DX " + dx);
					}
				});
			}
		}
	}
}