package com.openrobot.common;

public interface ClientServiceInterface {
	
	public void clientServiceStatusChange(String message, int status);
	
	public String messageToSend(); 
	
	public void clientServiceReceivedResponse(ClientService theClientService, String response);
}
