package com.openrobot.common;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
 

public class ThumbBall extends View implements OnTouchListener {
	
	public final static int FRAME_WIDTH = 250;
	public final static int FRAME_HEIGHT = 250;
	
	 private float x;
	 private float y;
	 private int r;
	 
	 private boolean isTouching = false;
	 
	 private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	 private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	 
	 private Handler reboundHandler = new Handler();
	 private Runnable mUpdateTimeTask;
	 
	 private ThumbBallListener delegate;
	 
	 
	 public ThumbBall(Context context, float x, float y, int r) {
	        super(context);
	        constructUpdateTimeTask();
	        this.setOnTouchListener(this);
	        circlePaint.setColor(Color.CYAN);
	        linePaint.setColor(Color.MAGENTA);
	        this.x = x;
	        this.y = y;
	        this.r = r;
	    }
	 
	 @Override
	 protected void onDraw(Canvas canvas) {
	     super.onDraw(canvas);
	     canvas.drawCircle(x, y, r, circlePaint);
	     
	     canvas.drawLine(FRAME_WIDTH / 2, 0, FRAME_WIDTH / 2, FRAME_HEIGHT, linePaint);
	     canvas.drawLine(0, FRAME_HEIGHT / 2, FRAME_WIDTH, FRAME_HEIGHT / 2, linePaint);
	 }
	 
	 
	 public boolean onTouch(View v, MotionEvent e) {
     	
     	float touchX = e.getX();
     	float touchY = e.getY();
     	
     	
     	switch (e.getAction() & MotionEvent.ACTION_MASK) {
         case MotionEvent.ACTION_DOWN:
         	reboundHandler.removeCallbacks(mUpdateTimeTask);
            break;
         case MotionEvent.ACTION_UP:
         //case MotionEvent.ACTION_POINTER_UP:
         	if (isTouching) {
         		reboundHandler.removeCallbacks(mUpdateTimeTask);
                reboundHandler.postDelayed(mUpdateTimeTask, 100);
         	}
         	isTouching = false;
            break;
         case MotionEvent.ACTION_MOVE:
         	isTouching = true;
         	if (GeometryHelper.inCircle(this.x, this.y, this.r, touchX, touchY) && 
         			touchX >= 0 &&
         			touchX <= FRAME_WIDTH &&
         			touchY >= 0 &&
         			touchY <= FRAME_HEIGHT) {
         			this.x = touchX;
         			this.y = touchY;
         			this.invalidate();
                
         			updateDelegate();         
         	}
            break;
         }
     	return true;
     }
	 
	 public void zeroThumbBallPosition() {
		 this.x = boxCenterX();
		 this.y = boxCenterY();
	 }

	 private void updateDelegate() {
		 if (delegate != null) {
			 delegate.thumbBallPositionChanged(this);
		 }
	 }
	 
	 private void constructUpdateTimeTask() {
		 mUpdateTimeTask = new Runnable() {
			 public void run() {
				 int transX = translatedX((int)getX());
				 int transY = translatedY((int)getY());
			   
				 if (GeometryHelper.dist(0, 0, transX, transY) > getR() / 3) {
					 int newTransX = transX -= transX / 10;
					 int newTransY = transY -= transY / 10;
					 setX(reTranslatedX(newTransX));
					 setY(reTranslatedY(newTransY));
					 invalidate();  // given the scope does this get called on the proper instance of ThumbBall???
					 reboundHandler.postDelayed(mUpdateTimeTask, 50);
				 } else {
					 reboundHandler.removeCallbacks(mUpdateTimeTask);
					 setX(reTranslatedX(0));
					 setY(reTranslatedY(0));
					 invalidate();  // given the scope does this get called on the proper instance of ThumbBall???
				 }	 		   
				 updateDelegate();
			 }
		 };
	 }
	  
	public ThumbBallListener getDelegate() {
		return delegate;
	}

	public void setDelegate(ThumbBallListener delegate) {
		this.delegate = delegate;
	}
	
	public float getScaledY(float scale) {
		return y * scale;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public int getR() {
		return r;
	}
	
	public void setR(int r) {
		this.r = r;
	}

	public boolean isTouching() {
		return isTouching;
	}

	public void setTouching(boolean isTouching) {
		this.isTouching = isTouching;
	}

	public Paint getCirclePaint() {
		return circlePaint;
	}
	
	public void setCirclePaint(Paint circlePaint) {
		this.circlePaint = circlePaint;
	}
	
	public Paint getLinePaint() {
		return linePaint;
	}
	
	public void setLinePaint(Paint linePaint) {
		this.linePaint = linePaint;
	}

	private int boxCenterX() {
		return FRAME_WIDTH / 2;
	}
	 
	private int boxCenterY() {
		return FRAME_HEIGHT / 2;
	}
	 
	public int translatedX() {
		return this.translatedX((int)this.x);
	}
	
	public int translatedY() {
		return this.translatedY((int)this.y);
	}
	
 	public int translatedX(int oldX) {
    	return oldX - this.boxCenterX();
    }
    
    public int translatedY(int oldY) {
    	return oldY - this.boxCenterY();
    }
    
    public int reTranslatedX(int newX) {
    	return newX + this.boxCenterX();
    }
    
    public int reTranslatedY(int newY) {
    	return newY + this.boxCenterY();
    }
    
    public float translatedX(float oldX) {
    	return oldX - ((float)this.boxCenterX());
    }
    
    public float translatedY(float oldY) {
    	return oldY - ((float)this.boxCenterY());
    }
  
    public float reTranslatedX(float newX) {
    	return newX + this.boxCenterX();
    }
    
    public float reTranslatedY(float newY) { 
    	return newY + this.boxCenterY();
    }
}