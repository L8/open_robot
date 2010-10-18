package com.openrobot.common;

public interface ClientSocketServiceInterface {
	
	public void clientServiceStatusChange(ClientSocketService theService, String message, int status);
	
	public String messageToSend(); 
	
	public void clientServiceReceivedResponse(ClientSocketService theClientService, String response);
}
