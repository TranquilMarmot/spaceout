package com.bitwaffle.threaded.guts.threads;

import com.bitwaffle.threaded.guts.audio.AudioThread;
import com.bitwaffle.threaded.guts.graphics.RenderThread;
import com.bitwaffle.threaded.guts.physics.PhysicsThread;
import com.bitwaffle.threaded.guts.threads.events.ALEvent;
import com.bitwaffle.threaded.guts.threads.events.ControlEvent;
import com.bitwaffle.threaded.guts.threads.events.GLEvent;
import com.bitwaffle.threaded.guts.threads.events.PhysicsEvent;

public class ThreadManager {
	private static ControlThread controlThread;
	private static RenderThread renderThread;
	private static AudioThread audioThread;
	private static PhysicsThread physicsThread;

	public static void startThreads() {
		if (controlThread == null && renderThread == null
				&& audioThread == null && physicsThread == null) {
			controlThread = new ControlThread();
			renderThread = new RenderThread();
			audioThread = new AudioThread();
			physicsThread = new PhysicsThread();

			controlThread.start();
			renderThread.start();
			audioThread.start();
			physicsThread.start();
		}
	}

	public static void addEvent(GLEvent event) {
			renderThread.addEvent(event);
	}

	public static void addEvent(PhysicsEvent event) {
		physicsThread.addEvent(event);
	}

	public static void addEvent(ALEvent event) {
		audioThread.addEvent(event);
	}
	
	public static void addEvent(ControlEvent event){
		controlThread.addEvent(event);
	}
}
