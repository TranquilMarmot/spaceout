package com.bitwaffle.threaded.guts;

import com.bitwaffle.threaded.guts.threads.ThreadManager;


public class Runner {
	public static final String VERSION = "0.0.8";
	public static volatile boolean done = false;
	
	public static void main(String[] args){
		ThreadManager.startThreads();
		
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
