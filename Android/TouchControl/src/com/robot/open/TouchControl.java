package com.robot.open;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
 
public class TouchControl extends Activity {
	
	public final static int FRAME_WIDTH = 250;
	public final static int FRAME_HEIGHT = 250;
	public final static int CIRCLE_RADIUS = 40;
	
	private ThumbBall thumbBall;
	private TextView xPosTextView;
	private TextView yPosTextView;
	private FrameLayout main;
	
	private int boxCenterX;
	private int boxCenterY;
	
	private boolean isTouching = false;
	
	private Handler reboundHandler = new Handler();
	final Runnable mUpdateTimeTask = new Runnable() {
 	   public void run() {
 		   float transX = translatedX(thumbBall.x);
 		   float transY = translatedY(thumbBall.y);
 		   
	    	if (dist(boxCenterX, boxCenterY, transX, transY) > CIRCLE_RADIUS) {
	    		float newTransX = transX -= transX / 10;
	    		float newTransY = transY -= transY / 10;
	    		thumbBall.x = reTranslatedX(newTransX);
	    		thumbBall.y = reTranslatedY(newTransY);
	    		thumbBall.invalidate();
	    		reboundHandler.postDelayed(mUpdateTimeTask, 50);
	    	} else {
	    		thumbBall.x = 0;
	    		thumbBall.y = 0;
	    		thumbBall.invalidate();
	    	}
	       
	   }
	};

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
        
        main.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
            	
            	float x = e.getX();
            	float y = e.getY();
            	
            	xPosTextView.setText(Float.toString(translatedX(x)));
            	yPosTextView.setText(Float.toString(-translatedY(y)));
            	
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
    
    private boolean inCircle(float centerX, float centerY, float radius, float pointX, float pointY) {
    	double dist = dist(centerX, centerY, pointX, pointY);
    	return dist < radius;
    }
    
    private double dist(float aX, float aY, float bX, float bY) {
    	float deltaX = aX - bX;
    	float deltaY = aY - bY;
    	return Math.sqrt(deltaX * deltaX) + Math.sqrt(deltaY * deltaY);
    }
    
    private float translatedX(float oldX) {
    	return oldX - boxCenterX;
    }
    
    private float translatedY(float oldY) {
    	return oldY - boxCenterY;
    }
  
    private float reTranslatedX(float newX) {
    	return newX + boxCenterX;
    }
    
    private float reTranslatedY(float newY) { 
    	return newY + boxCenterY;
    }
}