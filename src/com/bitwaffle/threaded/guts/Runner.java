package com.bitwaffle.threaded.guts;

import com.bitwaffle.threaded.guts.graphics.PhysicsThread;


public class Runner {
	public static final String VERSION = "0.0.8";
	public static boolean done = false;
	
	Thread master, render, physics;
	
	public static void main(String[] args){
		
		Runner run = new Runner();
		run.run();
	}
	
	public void run(){		
		master = new Thread(new MasterThread());
		master.setName("Master");
		
		render = new Thread(new RenderThread());
		render.setName("Render");
		
		physics = new Thread(new PhysicsThread());
		physics.setName("Physics");
		
		master.start();
		render.start();
		physics.start();
		
		while(!done){
			System.out.printf("main\n");
			
			try {
				Thread.sleep(100l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
