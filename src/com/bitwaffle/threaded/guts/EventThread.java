package com.bitwaffle.threaded.guts;

import com.bitwaffle.threaded.guts.Event;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public abstract class EventThread<T implements Event> implements Runnable{
	public static ReentrantLock queueLock;
	private LinkedList<T> eventQueue;

	public EventThread(){
		queueLock = new ReentrantLock();
		eventQueue = new LinkedList<T>();
	}

	@Override
	public abstract void run();

	public void addEvent(final Event event){
		// TODO use a thread pool here
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

		t.setName("Add " + T.class);
		t.start();
	}

	private void processEvents(){
		boolean processed = false;

		while(!processed){
			if(queueLock.tryLock()){
				try {
					Event e = eventQueue.poll();

					while(e != null){
						e.takeAction();
						e = eventQueue.poll();
					}
				} finally {
					queueLock.unlock();
					processed = true;
				}
			}
		}
	}
}
