package com.robot.open;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;
 
public class TouchControl extends Activity {
	
	private static final String DEVICE_ADDRESS = "00:07:80:91:30:2D";
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
	
	private int boxCenterX;
	private int boxCenterY;
	
	private boolean isTouching = false;
	private boolean shouldKill = false;
	private boolean shouldEnable = false;
	private boolean killEnabled = false;
	
	
	
	private long lastChange;
	
	private Handler reboundHandler = new Handler();
	final Runnable mUpdateTimeTask = new Runnable() {
 	   public void run() {
 		  int transX = translatedX((int)thumbBall.x);
		  int transY = translatedY((int)thumbBall.y);
		   
	    	if (dist(0, 0, transX, transY) > CIRCLE_RADIUS / 3) {
	    		int newTransX = transX -= transX / 10;
	    		int newTransY = transY -= transY / 10;
	    		thumbBall.x = reTranslatedX(newTransX);
	    		thumbBall.y = reTranslatedY(newTransY);
	    		thumbBall.invalidate();
	    		reboundHandler.postDelayed(mUpdateTimeTask, 50);
	    	} else {
	    		reboundHandler.removeCallbacks(mUpdateTimeTask);
	    		thumbBall.x = reTranslatedX(0);
	    		thumbBall.y = reTranslatedY(0);
	    		thumbBall.invalidate();
	    	}
 		   
	    	messageArduinoIfAppropriate(thumbBall.x, translatedY(thumbBall.y));
	    	xPosTextView.setText(Float.toString(translatedX(thumbBall.x)));
        	yPosTextView.setText(Float.toString(-translatedY(thumbBall.y)));
	   }
	};

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Establish bluetooth connection with Arduino
        Amarino.connect(this, DEVICE_ADDRESS);
        
        lastChange = System.currentTimeMillis();
        
        main = (FrameLayout) findViewById(R.id.main_view);
        xPosTextView = (TextView)findViewById(R.id.x_position_textview);
        yPosTextView = (TextView)findViewById(R.id.y_position_textview);
        
        thumbBall = new ThumbBall(this, 
        		(main.getRight() - main.getLeft()) / 2, 
        		(main.getBottom() - main.getTop()) / 2 ,
        		CIRCLE_RADIUS);
        main.addView(thumbBall);
        
        boxCenterX = FRAME_WIDTH / 2;
        boxCenterY = FRAME_HEIGHT / 2;
        
        this.killButton = (Button)this.findViewById(R.id.kill_button);
        this.killButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (killEnabled) {
            		shouldEnable = true;
            		shouldKill = false;
            		killEnabled = false;
            		killButton.setText("Kill damnit!");
            	} else {
            		shouldEnable = false;
            		shouldKill = true;
            		killEnabled = true;
            		killButton.setText("OK, Game On.");
            	}
            	
            }
            
          });
        
        main.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
            	
            	float x = e.getX();
            	float y = e.getY();
            	
            	
            	switch (e.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                	reboundHandler.removeCallbacks(mUpdateTimeTask);
                   break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                	if (isTouching) {
                		reboundHandler.removeCallbacks(mUpdateTimeTask);
                        reboundHandler.postDelayed(mUpdateTimeTask, 100);
                	}
                	isTouching = false;
                   break;
                case MotionEvent.ACTION_MOVE:
                	isTouching = true;
                	if (inCircle(thumbBall.x, thumbBall.y, CIRCLE_RADIUS, x, y) && 
                			x >= 0 &&
                			x <= FRAME_WIDTH &&
                			y >= 0 &&
                			y <= FRAME_HEIGHT) {
                		thumbBall.x = x;
                        thumbBall.y = y;
                        thumbBall.invalidate();
                       
                        messageArduinoIfAppropriate(thumbBall.x, translatedY(-thumbBall.y));
                        xPosTextView.setText(Float.toString(translatedX(x)));
                    	yPosTextView.setText(Float.toString(-translatedY(y)));
                	}
                   break;
                }
            	return true;
            }
        });
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	thumbBall.x = FRAME_WIDTH / 2;
    	thumbBall.y = FRAME_HEIGHT / 2;
    	thumbBall.invalidate();
    	
    }
    
    @Override
	protected void onStop() {
		super.onStop();
		
		// Housekeeping
		Amarino.disconnect(this, DEVICE_ADDRESS);
	}
    
    private void messageArduinoIfAppropriate(float x, float y) {
    	if (shouldKill || shouldEnable) {
    		// Send unconditional kill or resume message
			sendIntToArduino(shouldKill ? 1 : 0, ARDUINO_SHOULD_KILL_FUNCTION_FLAG);
			shouldKill = false;
			shouldEnable = false;
			thumbBall.x = boxCenterX;
			thumbBall.y = boxCenterY;
			return;
		}
    	if (System.currentTimeMillis() - lastChange > DELAY ) {
		 
			int[] values = new int[2];
			values[0] = (int)thumbBall.x;   
			values[1] = (int)thumbBall.y;
			
			sendIntArrayToArduino(values, ARDUINO_CONTROL_INPUT_FUNCTION_FLAG);
		}
    }
    
    private void messageArduinoManually(int message) {
    	lastChange = System.currentTimeMillis();
		Intent intent = new Intent(AmarinoIntent.ACTION_SEND);
        intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, DEVICE_ADDRESS);
        intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.INT_EXTRA);
        intent.putExtra(AmarinoIntent.EXTRA_FLAG, 'c');
        intent.putExtra(AmarinoIntent.EXTRA_DATA, message);
        this.sendBroadcast(intent);
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
    
    private boolean inCircle(float centerX, float centerY, float radius, float pointX, float pointY) {
    	double dist = dist(centerX, centerY, pointX, pointY);
    	return dist < radius;
    }
    
    private double dist(float aX, float aY, float bX, float bY) {
    	float deltaX = aX - bX;
    	float deltaY = aY - bY;
    	return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
    
    private double dist(int aX, int aY, int bX, int bY) {
    	int deltaX = aX - bX;
    	int deltaY = aY - bY;
    	return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
    
    private int translatedX(int oldX) {
    	return oldX - boxCenterX;
    }
    
    private int translatedY(int oldY) {
    	return oldY - boxCenterY;
    }
    
    private int reTranslatedX(int newX) {
    	return newX + boxCenterX;
    }
    
    private int reTranslatedY(int newY) {
    	return newY + boxCenterY;
    }
    
    private float translatedX(float oldX) {
    	return oldX - ((float)boxCenterX);
    }
    
    private float translatedY(float oldY) {
    	return oldY - ((float)boxCenterY);
    }
  
    private float reTranslatedX(float newX) {
    	return newX + boxCenterX;
    }
    
    private float reTranslatedY(float newY) { 
    	return newY + boxCenterY;
    }
    
    private int combineFloatsIntoInt(float float1, float float2) {
    	int int1 = (int)float1;
    	int int2 = (int)float2;
    	
    	int int2Shifted = int2 >> 16;
    	
    	int returnInt = int1 & 0xFF00;
    	
    	returnInt |= int2Shifted;
    	
    	int decoded1 = returnInt & 0xFF00;
    	int decoded2 = returnInt << 8;
    	
    	Log.d("OUTPUT", "int1: " + int1 + ", " + Integer.toBinaryString(int1) + ",  int2: " + int2 + ", " + Integer.toBinaryString(int2) + ",  combined: " + Integer.toBinaryString(returnInt) + ",  decoded1: " + decoded1 + ",  decoded2: " + decoded2);
    	
    	return returnInt;
    }
}