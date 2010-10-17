package com.openrobot.common;

public class GeometryHelper {
	
	 public static boolean inCircle(float centerX, float centerY, float radius, float pointX, float pointY) {
	   	double dist = dist(centerX, centerY, pointX, pointY);
	   	return dist < radius;
	}
	    
    public static double dist(float aX, float aY, float bX, float bY) {
    	float deltaX = aX - bX;
    	float deltaY = aY - bY;
    	return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
	    
    public static double dist(int aX, int aY, int bX, int bY) {
	   	int deltaX = aX - bX;
	   	int deltaY = aY - bY;
	   	return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
}
