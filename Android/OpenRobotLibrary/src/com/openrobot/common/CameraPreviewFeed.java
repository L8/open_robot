package com.openrobot.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * Source below is a modified a modified version of RobotEyes project by Charles L. Chen
 * 
 */

public class CameraPreviewFeed implements Callback {
	 	
	private CameraPreviewFeedInterface delegate;
	
	private SurfaceHolder mHolder;

    private SurfaceView mPreview;

    private Camera mCamera;

    private boolean mTorchMode;

    private Rect r;

    private int previewHeight = 0;

    private int previewWidth = 0;

    private int previewFormat = 0;

    private byte[] mCallbackBuffer;
    
    private boolean busyProcessing = false;

    private ByteArrayOutputStream out;

    
    public CameraPreviewFeed(SurfaceView surfaceView, CameraPreviewFeedInterface delegate) {
        super();
    	mTorchMode = false;
        out = new ByteArrayOutputStream();

        this.delegate = delegate;
    	mPreview = surfaceView;
        mHolder = mPreview.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		 mHolder.setFixedSize(w, h);
        // Start the preview
        Parameters params = mCamera.getParameters();
        previewHeight = params.getPreviewSize().height;
        previewWidth = params.getPreviewSize().width;
        previewFormat = params.getPreviewFormat();

        // Crop the edges of the picture to reduce the image size
        r = new Rect(100, 100, previewWidth - 100, previewHeight - 100);

        mCallbackBuffer = new byte[460800];

        mCamera.setParameters(params);
        mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
            public void onPreviewFrame(byte[] imageData, Camera arg1) {
            	if (!busyProcessing) {
            		busyProcessing = true;
            		processImage(imageData);	
            	}
            }
        });
        mCamera.addCallbackBuffer(mCallbackBuffer);
        mCamera.startPreview();
        setTorchMode(mTorchMode);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		 mCamera = Camera.open();
	        try {
	            mCamera.setPreviewDisplay(holder);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (mCamera != null) {
			  mCamera.stopPreview();
		      mCamera.release();
		      mCamera = null;	
		}
	}
	
	public void destroy() {
		this.surfaceDestroyed(null);
		delegate = null;
	}
	
	private void processImage(byte[] imageData) {
		if (this.delegate == null) {
			return;
		}
		
		try {
            YuvImage yuvImage = new YuvImage(imageData, previewFormat, previewWidth, previewHeight, null);
            yuvImage.compressToJpeg(r, 20, out); // Tweak the quality here - 20 seems pretty decent for quality + size.
            
            delegate.newImageFromCameraPreviewFeed(this, out.toByteArray());
            
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            out.reset();
            if (mCamera != null) {
                mCamera.addCallbackBuffer(mCallbackBuffer);
            }
            busyProcessing = false;
        }
		
	}
	
	public void setTorchMode(boolean on) {
        if (mCamera != null) {
            Parameters params = mCamera.getParameters();
            if (on) {
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            } else {
                params.setFlashMode(Parameters.FLASH_MODE_AUTO);
            }
            mTorchMode = on;
            mCamera.setParameters(params);
        }
    }
}
