package com.openrobot.common;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;


public class ServerSocketByteService {
	
	public static final int SERVER_SERVICE_STATUS_LISTENING = 1;
	public static final int SERVER_SERVICE_STATUS_CONNECTED = 2;
	public static final int SERVER_SERVICE_STATUS_CHARACTER_EXCEPTION = 3;
	public static final int SERVER_SERVICE_STATUS_CONNECTION_INTERRUPTED = 4;
	public static final int SERVER_SERVICE_STATUS_CONNECTION_NOT_DETECTED = 5;
	public static final int SERVER_SERVICE_STATUS_GENERIC_ERROR = 6;
	
	public static final String SERVER_DELIMITER = "  ";
	
    public static String SERVERIP = "10.0.2.15";   	// default ip...
    private int serverPort;

    private static final String TAG = "ServerService";
    private Handler handler = new Handler();
    private ServerSocket serverSocket;
    private OutputStreamWriter out;

    private ServerSocketByteServiceInterface delegate;
	
    public ServerSocketByteService(ServerSocketByteServiceInterface delegate) {
    	super();
    	this.delegate = delegate;
		SERVERIP = NetworkHelper.getLocalIpAddress();	
    }
	
    public void disconnect() {
		try {
			// make sure to close the socket upon exiting
			 if (serverSocket != null) {
				 serverSocket.close();	 
			 }
			 if (out != null) {
				 out.flush();
	             out.close();
			 }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
	
	public void makeConnection(int port) {
		this.serverPort = port;
		Thread fst = new Thread(new ServerThread());
		fst.start();
	}
	
	private void handleInput(final Bitmap inputBitmap) {
       
	    handler.post(new Runnable() {
	            
	    	public void run() {
	    		if (delegate != null) {
	    			String response = delegate.serverServiceReceivedBitmap(getThis(), inputBitmap );
	    			if (response != null) {
	    				try {
		    				out.write(response);
		    				out.flush();  // Don't forget to flush!	
		    			} catch (IOException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}        	
	    			}	        		
	    		}
	        }
	    });
    }
    
    private void postStatus(final String statusMessage, final int status) {
    	handler.post(new Runnable() {          
            public void run() {
            	if (delegate != null) {
            		delegate.serverServiceStatusChange(null, statusMessage, status);
            	}
            }
    	});
    }
    
    public ServerSocketByteServiceInterface getDelegate() {
		return delegate;
	}

	public void setDelegate(ServerSocketByteServiceInterface delegate) {
		this.delegate = delegate;
	}
	
	private ServerSocketByteService getThis() {
		return this;
	}
	
	class ServerThread implements Runnable {

        public void run() {
            try {
                if (SERVERIP != null) {
                	
                	postStatus("Listening on IP: " + SERVERIP, SERVER_SERVICE_STATUS_LISTENING);
                    Log.d(TAG, "Got IP, now listening...");
                    
                    serverSocket = new ServerSocket(serverPort);
                    while (true) {
                        // listen for incoming clients
                        Socket client = serverSocket.accept();
                        
                        postStatus("Connected.", SERVER_SERVICE_STATUS_CONNECTED);
                        
                        try {
                        	Bitmap bitmap; 
                        	while (true) {
	                        	if ( (bitmap = BitmapFactory.decodeStream(client.getInputStream())) != null) {
	                        		if (bitmap != null) {
	                        			Log.d("OUTPUT", "bitmap was not null");
	                        			handleInput(bitmap);
	                            	}
	                        	}
                        	}
                        } catch (UnsupportedEncodingException e) {
                        	postStatus("This VM does not support the Latin-1 character set.", SERVER_SERVICE_STATUS_CHARACTER_EXCEPTION);
                        	Log.d("StreamError:  ", "This VM does not support the Latin-1 character set.");
                        } catch (Exception e) {
                        	postStatus("Oops. Connection interrupted. Please reconnect.", SERVER_SERVICE_STATUS_CONNECTION_INTERRUPTED);
                            e.printStackTrace();
                        }
                    }
                } else {      
                    postStatus("Couldn't detect internet connection.", SERVER_SERVICE_STATUS_CONNECTION_NOT_DETECTED);                        
                }
            } catch (Exception e) {
            	postStatus("Error", SERVER_SERVICE_STATUS_GENERIC_ERROR);
                e.printStackTrace();
            }
        }
    }
}



