package com.openrobot.common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServerService extends Service {
	
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

    private ServerServiceInterface delegate;
	
	 /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class ServerBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }
    
 // This is the object that receives interactions from clients. 
    private final IBinder mBinder = new ServerBinder();
    
	@Override
	public IBinder onBind(Intent intent) {
		
		Toast.makeText(this, "Server Service Bound", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onBind");

		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Server Service Created", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onCreate");

		SERVERIP = NetworkHelper.getLocalIpAddress();
		
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Server Service Destroyed", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onDestroy"); 
		
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
         super.onDestroy();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		super.onStart(intent, startid);
		Toast.makeText(this, "Server Service Started", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onStart");
	}
	
	public ServerService getServerService() {
		return this;
	}
	
	public void makeConnection(int port) {
		this.serverPort = port;
		Toast.makeText(this, "Making Connection", Toast.LENGTH_SHORT).show();
		Thread fst = new Thread(new ServerThread());
		fst.start();
	}
	
    @SuppressWarnings("unused")
	private void handleInput(final String input) {
    	if (/*response desired*/false) {
    		String response = input;
    		try {
				out.write(response);
				out.flush();  // Don't forget to flush!	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
    	}
       
        handler.post(new Runnable() {
            
            public void run() {
               if (delegate != null) {
            	   delegate.serverServiceReceivedMessage(getServerService(), input);
               }
            }
        });
    }
    
    private void postStatus(final String statusMessage, final int status) {
    	handler.post(new Runnable() {          
            public void run() {
            	if (delegate != null) {
            		delegate.serverServiceStatusChange(statusMessage, status);
            	}
            }
    	});
    }
    
    public ServerServiceInterface getDelegate() {
		return delegate;
	}

	public void setDelegate(ServerServiceInterface delegate) {
		this.delegate = delegate;
	}



	public class ServerThread implements Runnable {

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

                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            OutputStream sock_out= client.getOutputStream();
                            OutputStream bout= new BufferedOutputStream(sock_out);
                            out = new OutputStreamWriter(bout, "8859_1");
                            
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                Log.d("ServerResponse:  ", line);
                                handleInput(line);                        
                            }
                            break;
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
