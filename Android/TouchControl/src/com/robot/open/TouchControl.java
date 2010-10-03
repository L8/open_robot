package com.robot.open;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;
 
public class TouchControl extends Activity implements ThumbBallListener {
	
	private static final String DEVICE_ADDRESS = "00:07:80:91:32:51";
	private static final char ARDUINO_CONTROL_INPUT_FUNCTION_FLAG = 'c';
	private static final char ARDUINO_SHOULD_KILL_FUNCTION_FLAG = 'd';
	
	public final static int DELAY = 150;
	public final static int FRAME_WIDTH = 250;
	public final static int FRAME_HEIGHT = 250;
	public final static int CIRCLE_RADIUS = 40;
	
	private ThumbBall thumbBall;
	private TextView xPosTextView;
	private TextView yPosTextView;
	private FrameLayout main;
	private Button killButton;
	
	private boolean shouldKill = false;
	private boolean shouldEnable = false;
	private boolean killEnabled = false;
	
	
	
	private long lastChange;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Establish bluetooth connection with Arduino
        Amarino.connect(this, DEVICE_ADDRESS);
        
        lastChange = System.currentTimeMillis();
        
        main = (FrameLayout) findViewById(R.id.main_view);
        main.setBackgroundColor(Color.CYAN);
        
        xPosTextView = (TextView)findViewById(R.id.x_position_textview);
        yPosTextView = (TextView)findViewById(R.id.y_position_textview);
        
        thumbBall = new ThumbBall(this, 
        		(main.getRight() - main.getLeft()) / 2, 
        		(main.getBottom() - main.getTop()) / 2 ,
        		CIRCLE_RADIUS);
        thumbBall.setDelegate(this);
        main.addView(thumbBall);
        
        this.killButton = (Button)this.findViewById(R.id.kill_button);
        this.killButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (killEnabled) {
            		shouldEnable = true;
            		shouldKill = false;
            		killEnabled = false;
            		killButton.setText("Stop, please");
            	} else {
            		shouldEnable = false;
            		shouldKill = true;
            		killEnabled = true;
            		killButton.setText("OK, Game On.");
            	}
            }         
        });
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	thumbBall.setX(FRAME_WIDTH / 2);
    	thumbBall.setY(FRAME_HEIGHT / 2);
    	thumbBall.invalidate();
    }
    
    @Override
	protected void onStop() {
		super.onStop();

		// Housekeeping
		Amarino.disconnect(this, DEVICE_ADDRESS);
	}
    
    @Override
    protected void onDestroy() {
    	// prevents circular reference memory leak ???
		if (thumbBall != null) {
			thumbBall.setDelegate(null);
		}
    	super.onDestroy();
    }
    
    @Override
	public void thumbBallPositionChanged(ThumbBall tb) {
		Log.d("OUTPUT", "X: " + tb.getX() + "  Y: " + tb.getY());
		xPosTextView.setText(Integer.toString(tb.translatedX()));
		yPosTextView.setText(Integer.toString(-tb.translatedY()));
		
		this.messageArduinoIfAppropriate((int)thumbBall.getX(), (int)thumbBall.getY());
	}
 
    private void messageArduinoIfAppropriate(int x, int y) {
    	if (shouldKill || shouldEnable) {
    		// Send unconditional kill or resume message
			sendIntToArduino(shouldKill ? 1 : 0, ARDUINO_SHOULD_KILL_FUNCTION_FLAG);
			shouldKill = false;
			shouldEnable = false;
			thumbBall.zeroThumbBallPosition();
			return;
		}
    	if (System.currentTimeMillis() - lastChange > DELAY ) {
		 
			int[] values = new int[2];
			values[0] = x;   
			values[1] = y;
			
			sendIntArrayToArduino(values, ARDUINO_CONTROL_INPUT_FUNCTION_FLAG);
		}
    }
    
    private void sendIntToArduino(int message, char methodFlag) {
    	lastChange = System.currentTimeMillis();
    	Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
        intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, DEVICE_ADDRESS);
        intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_EXTRA);
        intent.putExtra(AmarinoIntent.EXTRA_FLAG, methodFlag);
        intent.putExtra(AmarinoIntent.EXTRA_DATA, message);
        this.sendBroadcast(intent);
    }
    
    private void sendIntArrayToArduino(int[] message, char methodFlag) {
    	lastChange = System.currentTimeMillis();
    	Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
        intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, DEVICE_ADDRESS);
        intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_ARRAY_EXTRA);
        intent.putExtra(AmarinoIntent.EXTRA_FLAG, methodFlag);
        intent.putExtra(AmarinoIntent.EXTRA_DATA, message);
        this.sendBroadcast(intent);
    }
}