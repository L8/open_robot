package com.openrobot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class HelloAmarino extends Activity {
	
	private static final String DEVICE_ADDRESS = "00:07:80:91:32:51";
	
	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
	
	private Button switchButton;
	private View colorIndicator;
	
	final int DELAY = 150;
	long lastChange;
	boolean isOn = false;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lastChange = System.currentTimeMillis();
        
        registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
        
        Amarino.connect(this, DEVICE_ADDRESS);
        
        this.colorIndicator = (View)this.findViewById(R.id.ColorIndicator);
        this.colorIndicator.setBackgroundColor(Color.BLACK);
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
		
		// Housekeeping
		Amarino.disconnect(this, DEVICE_ADDRESS);
		unregisterReceiver(arduinoReceiver);
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
	
	
	public class ArduinoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String data = null;
			
			// the device address from which the data was sent, we don't need it here but to demonstrate how you retrieve it
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			Log.d("OUTPUT", "Address:  " + address);
			
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support differnt data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				
				if (data != null){
					Log.d("OUTPUT", data);
					
					try {
						// since we know that our string value is an int number we can parse it to an integer
						final int sensorReading = Integer.parseInt(data);
						if (sensorReading == 1) {
							colorIndicator.setBackgroundColor(Color.CYAN);
						} else {
							colorIndicator.setBackgroundColor(Color.BLACK);
						}
					} 
					catch (NumberFormatException e) { 
						// Data was not an integer 
					}
				}
			}
		}
	}
}