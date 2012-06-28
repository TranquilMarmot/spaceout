package com.bitwaffle.threaded.guts.audio;

import com.bitwaffle.threaded.guts.Runner;
import com.bitwaffle.threaded.guts.threads.EventThread;
import com.bitwaffle.threaded.guts.threads.events.ALEvent;

public class AudioThread extends EventThread<ALEvent> {

	@Override
	public void run() {
		while(!Runner.done){
			long oldTime = System.currentTimeMillis();
			
			processEvents();
			
			long newTime = System.currentTimeMillis();
			//System.out.printf("audio loop took %d milliseconds\n", newTime - oldTime);
		}
	}

}
