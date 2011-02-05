package com.phinominal.webbedsocketmain;

import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.openrobot.common.ControlWebSocketServer;
import com.openrobot.common.ControlWebSocketServerInterface;
import com.openrobot.common.NetworkHelper;
import com.openrobot.common.VideoWebSocketServer;


public class SocketFeet extends Activity implements ControlWebSocketServerInterface {

    private SurfaceView mSurfaceView;
	private ControlWebSocketServer controlWebSocketServer;
	private VideoWebSocketServer videoWebSocketServer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Window lWin = getWindow();
        lWin.setFormat(PixelFormat.TRANSLUCENT);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // needs to be called before setContentView to be applied
        lWin.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceCamera);
        
        mSurfaceView.setOnClickListener(new OnClickListener() {
            public void onClick(View aView) {
               System.out.println("Surface view was clicked");
            }
        });
        
        Log.d("OUT", "IP Address:  " + NetworkHelper.getLocalIpAddress());
        
        int videoPort = 8886;
        videoWebSocketServer = new VideoWebSocketServer(videoPort, mSurfaceView);
        videoWebSocketServer.start();
        Log.d("OUT", "VideoWebSocketServer started on port: " + videoWebSocketServer.getPort());
        
        int controlPort = 8887;
        controlWebSocketServer = new ControlWebSocketServer(controlPort, this);
        controlWebSocketServer.start();
        Log.d("OUT", "ControlWebSocketServer started on port: " + controlWebSocketServer.getPort());    
    }   
    
    @Override
    public void onDestroy() {
    	
    	try {
    		if (videoWebSocketServer != null) {
				videoWebSocketServer.stop();
    		}
    		if (controlWebSocketServer != null) {
    			controlWebSocketServer.stop();
    		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	}
    }
    
    // ControlWebSocketServerInterface
    public void controlInstructionReceived(String controlString) {
    	
    }
}
