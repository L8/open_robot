package com.openrobot.common;

import android.graphics.Bitmap;

public interface ServerSocketByteServiceInterface {
	public void serverServiceStatusChange(ServerSocketByteService service, String message, int status);
	
	public String serverServiceReceivedBitmap(ServerSocketByteService service, Bitmap theBitmap);
}
