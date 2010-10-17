package com.openrobot.common;

public interface ServerServiceInterface {
	public void serverServiceStatusChange(String message, int status);
	
	public void serverServiceReceivedMessage(ServerService service, String message); 
}
