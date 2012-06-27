package com.bitwaffle.threaded.guts;

import org.lwjgl.input.Mouse;


public class MasterThread implements Runnable{
	@Override
	public void run() {
		while(!Runner.done){
			//System.out.printf("master\n");
			
			/*
			try {
				Thread.sleep(100l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			
			int dx = Mouse.getDX();
			if(dx != 0)
				System.out.println("MOUSE DX " + dx);
		}
	}
}