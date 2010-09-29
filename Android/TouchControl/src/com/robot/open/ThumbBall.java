package com.robot.open;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
 

public class ThumbBall extends View{

	 public float x;
	 public float y;
	 private final int r;
	 private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	 private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	 
	 public ThumbBall(Context context, float x, float y, int r) {
	        super(context);
	        mPaint.setColor(0xFF000000);
	        linePaint.setColor(Color.BLACK);
	        this.x = x;
	        this.y = y;
	        this.r = r;
	    }
	 
	 @Override
	 protected void onDraw(Canvas canvas) {
	     super.onDraw(canvas);
	     canvas.drawCircle(x, y, r, mPaint);
	     
	     canvas.drawLine(TouchControl.FRAME_WIDTH / 2, 0, TouchControl.FRAME_WIDTH / 2, TouchControl.FRAME_HEIGHT, linePaint);
	     canvas.drawLine(0, TouchControl.FRAME_HEIGHT / 2, TouchControl.FRAME_WIDTH, TouchControl.FRAME_HEIGHT / 2, linePaint);
	 }
}
