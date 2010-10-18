package com.openrobot.common;

public interface ServerSocketServiceInterface {
	public void serverServiceStatusChange(ServerSocketService service, String message, int status);
	
	public String serverServiceReceivedMessage(ServerSocketService service, String message);
}
