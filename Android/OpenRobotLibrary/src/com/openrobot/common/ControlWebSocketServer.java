package com.openrobot.common;

import org.json.JSONException;

public class ControlWebSocketServer extends WebSocketServer {
	private ControlWebSocketServerInterface delegate;
	public boolean isConnected = false;
	private WebSocket controlWebSocket = null;
	
	public ControlWebSocketServer(int port, ControlWebSocketServerInterface delegate) {
		super(port);
		this.delegate = delegate;
	}
	
	// Abstract Methods
    public void onClientOpen(WebSocket conn) {
    	isConnected = true;
    	    	
        System.out.println(conn + " has connected");
    }

    public void onClientClose(WebSocket conn) {
    	if (this.connections().length <= 0) { 
    		isConnected = false;
    		System.out.println("No WebSocket connections remain");
    	}
    	if (conn == controlWebSocket) {
    		controlWebSocket = null;
    	}
    	
        System.out.println(conn + " has checked out");
    }

    public void onClientMessage(WebSocket conn, String message) {
    	try {
			JSONMessage jsonMessage = new JSONMessage(message);
			
			// Check for Subscriptions
			Integer subscriptionRequest = jsonMessage.getIntForKey(JSONMessage.SUBSCRIPTION_KEY);
			if (subscriptionRequest != null) {
				if ((subscriptionRequest.intValue() & JSONMessage.SUBSCRIPTION_VALUE_CONTROL) == 1) {
					if (controlWebSocket == null) {
						controlWebSocket = conn;
					}
				}
			}
			
			if (conn == controlWebSocket) {
				String controlInstruction = jsonMessage.getStringForKey(JSONMessage.INSTRUCTION_KEY);
				if (delegate != null) {
					delegate.controlInstructionReceived(controlInstruction);
				}
				System.out.println("Control Instruction:  " + controlInstruction);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        System.out.println(conn + ": " + message);
    }
    
    public void sendMessage(JSONMessage message) {
    	try {
    		String messageString = message.toString();
    		this.sendToAll(messageString);
    		
    	} catch (Exception e) {
    		System.out.println(e.toString());
    	}
    }
    
   
    
}
