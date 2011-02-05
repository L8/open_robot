package com.openrobot.common;

import java.io.IOException;

import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;

public class VideoWebSocketServer extends WebSocketServer implements CameraPreviewFeedInterface {
	CameraPreviewFeed cameraFeed;

	public boolean isConnected = false;
	
	public VideoWebSocketServer(int port, SurfaceView surfaceView) {
		super(port);
		cameraFeed = new CameraPreviewFeed(surfaceView, this);
	}

	// Abstract Methods
    public void onClientOpen(WebSocket conn) {
    	isConnected = true;
        try {
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d("OUT", conn + " entered the room!");
        System.out.println(conn + " entered the room!");
    }

    public void onClientClose(WebSocket conn) {
    	if (this.connections().length <= 0) { 
    		isConnected = false;
    		System.out.println("No WebSocket connections remain");
    	}
    	
        System.out.println(conn + " has checked out");
    }

    public void onClientMessage(WebSocket conn, String message) {
        System.out.println(conn + ": " + message);
    }
    
    // CameraPreviewFeedInterface
    public void newImageFromCameraPreviewFeed(CameraPreviewFeed theFeed, byte[] theImage) {
    	if (isConnected) {
    		try {	
				sendToAll(Base64.encodeToString(theImage, Base64.DEFAULT));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}
