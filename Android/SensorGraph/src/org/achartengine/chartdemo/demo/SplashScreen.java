package org.achartengine.chartdemo.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreen extends Activity {

	protected boolean _active = true;
	protected int _splashTime = 5000; // time to display the splash screen in ms
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //setContentView(R.layout.splash);
	    setContentView(R.layout.splash);
	 
	    // thread for displaying the SplashScreen
	    Thread splashTread = new Thread() {
	        @Override
	        public void run() {
	            try {
	                int waited = 0;
	                while(_active && (waited < _splashTime)) {
	                    sleep(100);
	                    if(_active) {
	                        waited += 100;
	                    }
	                }
	            } catch(InterruptedException e) {
	                // do nothing
	            } finally {
	                finish();
	                android.content.Intent intent = new android.content.Intent(); 
	                intent.setClassName("org.achartengine.chartdemo.demo", "org.achartengine.chartdemo.demo.UpdatingChartActivity"); 
	                startActivity(new Intent(intent));
	                stop();
	            }
	        }
	    };
	    splashTread.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        _active = false;
	    }
	    return true;
	}
}
