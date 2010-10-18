package com.openrobot.common;

import android.app.Activity;
import android.content.Intent;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class AmarinoService {

	private Activity activity;
	private String deviceAddress;
	
	public AmarinoService(Activity activity, String deviceAddress) {
		super();
		this.activity = activity;
		this.deviceAddress = deviceAddress;
		
		// Establish bluetooth connection with Arduino
        Amarino.connect(activity, deviceAddress);	
	}
	
	public void disconnect() {
		Amarino.disconnect(activity, deviceAddress);
	}
	
    public void sendIntToArduino(int message, char methodFlag) {
    	Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
        intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, deviceAddress);
        intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_EXTRA);
        intent.putExtra(AmarinoIntent.EXTRA_FLAG, methodFlag);
        intent.putExtra(AmarinoIntent.EXTRA_DATA, message);
        activity.sendBroadcast(intent);
    }
    
    public void sendIntArrayToArduino(int[] message, char methodFlag) {
    	Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
        intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, deviceAddress);
        intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_ARRAY_EXTRA);
        intent.putExtra(AmarinoIntent.EXTRA_FLAG, methodFlag);
        intent.putExtra(AmarinoIntent.EXTRA_DATA, message);
        activity.sendBroadcast(intent);
    }
}
