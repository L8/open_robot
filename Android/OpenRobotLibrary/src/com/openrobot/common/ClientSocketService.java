package com.openrobot.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class ClientSocketService implements SleepyProducerConsumerInterface {

	
	private static final String TAG = "ClientService";
    private String serverIpAddress;
    private int serverPort;
    private boolean connected = false;
    private boolean shouldWaitForServerResponse = false;
    private boolean currentlyWaitingForServerResponse = false;
    private Handler handler = new Handler();
    private Socket clientSocket;
    private PrintWriter out;
    private boolean shouldTransmit = true;
    private ClientSocketServiceInterface delegate;
    private SleepyProducerConsumer sleepyProducerConsumer;
    private String stringToSend = null;
	
    
    public ClientSocketService(ClientSocketServiceInterface delegate) {
    	super();
    	this.delegate = delegate;
    }
    
    public void disconnect() {
    	 try {
             // make sure to close the socket upon exiting
			 if (clientSocket != null) {
				 clientSocket.close();	 
			 }
			 if (out != null) {
				 out.flush();
	             out.close();
			 }
             
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
	
	public void makeConnection(String serverIp, int serverPort, boolean waitForServerResponse) {
		this.serverIpAddress = serverIp;
		this.serverPort = serverPort;
		this.shouldWaitForServerResponse = waitForServerResponse;
		Log.d(TAG, "makeConnection");
		
		sleepyProducerConsumer = new SleepyProducerConsumer(this);
		
		if (!serverIpAddress.equals("")) {
            Thread cThread = new Thread(new ClientThread());
            cThread.start();
        }
	}
	
	public void closeConnection() {
		connected = false;
	}
	
	public boolean sendStringToServer(String theString) {
		return sleepyProducerConsumer.setProducerData(theString);
	}

    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d(TAG, "C: Connecting to:  " + serverIpAddress);
                clientSocket = new Socket(serverAddr, serverPort);
                connected = true;
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
                String line = null;
                while (connected) {
                	if (shouldTransmit) {
                        try {
                        	if (stringToSend != null && !currentlyWaitingForServerResponse) {
                        		 // send message to Server      
                            	out.println(stringToSend);              
                                Log.d(TAG, "C: Sent.");
                                stringToSend = null;
                                if (shouldWaitForServerResponse) {
                                	currentlyWaitingForServerResponse = true;
                                }     
                        	} else if (currentlyWaitingForServerResponse && (line = in.readLine()) != null) {
                        		delegate.clientServiceReceivedResponse(getThis(), line);
                        		currentlyWaitingForServerResponse = false;
                        	} else {
                        		 Log.d(TAG, "C: Waiting on sleepyProducerConsumer.");
                                 synchronized (sleepyProducerConsumer) {
                                 	sleepyProducerConsumer.wait();	
                                 } 
                                 Log.d(TAG, "C: Woken up and proceeding to send data");	
                        	}
                            
                        } catch (Exception e) {
                            Log.d(TAG, "S: Error", e);
                            // connected = false;
                        }
                	}
                }
                clientSocket.close();
                Log.d(TAG, "C: Closed.");
            } catch (Exception e) {
                Log.e(TAG, "C: Error", e);
                connected = false;
            }
        }
    }
    
	public void sleepyConsumerThreadPoppedObject(Object theObject) {
		if (stringToSend != null) {
			Log.d(TAG, "StringToSend not null, overwriting");
		}
		stringToSend = (String) theObject;
		 synchronized (sleepyProducerConsumer) {
         	sleepyProducerConsumer.notify();	
         } 
	}
	
	private ClientSocketService getThis() {
		return this;
	}
    
    public boolean getShouldTransmit() {
    	return this.shouldTransmit;
    }
    
    public void setShouldTransmit(boolean shouldTransmit) {
    	this.shouldTransmit = shouldTransmit;
    }

	public String getServerIpAddress() {
		return serverIpAddress;
	}

	public void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public ClientSocketServiceInterface getDelegate() {
		return delegate;
	}

	public void setDelegate(ClientSocketServiceInterface delegate) {
		this.delegate = delegate;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
