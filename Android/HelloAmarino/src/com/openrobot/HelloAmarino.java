package com.openrobot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class HelloAmarino extends Activity {
	
	public static String EXTRA_DEVICE_ADDRESS = "dk.itu.smds.android.bt.DeviceListActivity.EXTRA_DEVICE_ADDRESS";
	
	private static final String DEVICE_ADDRESS = "00:07:80:91:32:51";
	private Button switchButton;
	
	final int DELAY = 150;
	long lastChange;
	boolean isOn = false;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lastChange = System.currentTimeMillis();
        
        Amarino.connect(this, DEVICE_ADDRESS);
        
        this.switchButton = (Button)this.findViewById(R.id.switch_button);
        this.switchButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  if (System.currentTimeMillis() - lastChange > DELAY ) {
        		  lastChange = System.currentTimeMillis();
        	   	  if (isOn) {
            		  switchButton.setText("OFF");
            	  } else {
            		  switchButton.setText("ON");
            	  }
            	  isOn = !isOn;
            	  messageArduinoManually();
        	  }
      
          }
        });
    }
    
    
    
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		// stop Amarino's background service, we don't need it any more 
		Amarino.disconnect(this, DEVICE_ADDRESS);
	}

	
	// For some reason this function raises problem with 'EXTRA_DEVICE_ADDRESS' being passed to intent. Use replacement below
	private void messageArduino() {
		 Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'C', isOn ? 1 : 0);
   	  // do some stuff....
	}
	
	private void messageArduinoManually() {
		
		Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
        intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, DEVICE_ADDRESS);
        intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_EXTRA);
        intent.putExtra(AmarinoIntent.EXTRA_FLAG, 'c');
        intent.putExtra(AmarinoIntent.EXTRA_DATA, isOn ? 1 : 0);
        this.sendBroadcast(intent);
	}
}