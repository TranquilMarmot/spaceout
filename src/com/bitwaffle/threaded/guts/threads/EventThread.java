package com.bitwaffle.threaded.guts.threads;

import com.bitwaffle.threaded.guts.threads.events.Event;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public abstract class EventThread<T extends Event> extends Thread{
	private static ReentrantLock queueLock;
	private LinkedList<T> eventQueue;
	ExecutorService service;

	public EventThread(){
		this.setName("Unknown EventThread");
		queueLock = new ReentrantLock();
		eventQueue = new LinkedList<T>();
		service = Executors.newSingleThreadExecutor();
	}

	@Override
	public abstract void run();

	public void addEvent(final T event){
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

		t.setName("Add event");
		service.submit(t);
	}

	protected void processEvents(){
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
