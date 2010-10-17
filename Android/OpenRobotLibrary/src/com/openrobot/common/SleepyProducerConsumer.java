package com.openrobot.common;


public class SleepyProducerConsumer {
	
	SleepyProducerConsumerInterface delegate;
	
	Object dataStore = null;
	Object data = null;
	
	Object waitObject = new Object();
	Object full  = new Object();
    Object empty = new Object();
    
    public SleepyProducerConsumer(SleepyProducerConsumerInterface delegate) {
    	super();
    	this.delegate = delegate;
    	(new Thread(new Producer())).start();
        (new Thread(new Consumer())).start();
    }
    
    public boolean setProducerData(Object newObject) {
    	if (dataStore != null) {
    		return false;
    	} 
    	dataStore = newObject;
    	synchronized(waitObject) {
    		waitObject.notify();	
    	}
    	return true;
    }
    
    public void push(Object d) {
        synchronized(full) {
            if (data != null)
				try {
					full.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        }
        data = d;
        synchronized(empty) {
            if (data != null) empty.notify();
        }
    }
    
    public Object pop() {
        synchronized(empty) {
            if (data == null)
				try {
					empty.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        }
        Object o = data;
        data = null;
        synchronized(full) {
            if (data == null) full.notify();
        }
        return o;
    }
    
    class Producer implements Runnable {
        public void run() {
            while (true) {
            	while (dataStore == null) {
            		System.out.println("DataStore was empty.  Attempting wait....");
            		try {
            			synchronized(waitObject) {
            				waitObject.wait();	
            			}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            	}
            	System.out.println("DataStore woken.  Commencing push...");
                Object o = dataStore;
                dataStore = null;
                push(o);
            }
        }
    }
    
    class Consumer implements Runnable {
        public void run() {
            while (true) {
                Object o = pop();
                if (delegate != null) { 
                	delegate.sleepyConsumerThreadPoppedObject(o);
                }
            }
        }
    }
}

