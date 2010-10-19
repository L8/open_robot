package com.openrobot.touchrobot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.openrobot.common.ClientSocketService;
import com.openrobot.common.ClientSocketServiceInterface;
import com.openrobot.common.ControlCommunicationConstants;
import com.openrobot.common.DialogHelper;
import com.openrobot.common.EditTextDialogInterface;
import com.openrobot.common.NetworkHelper;
import com.openrobot.common.PreferenceHelper;
import com.openrobot.common.ServerService;
import com.openrobot.common.ServerSocketByteService;
import com.openrobot.common.ServerSocketByteServiceInterface;
import com.openrobot.common.ThumbBall;
import com.openrobot.common.ThumbBallListener;

public class ControlRobotActivity extends Activity implements ThumbBallListener, 
						ClientSocketServiceInterface, ServerSocketByteServiceInterface, EditTextDialogInterface {
		
	private static final int CONTROL_CLIENT_IP_DIALOG_TAG = 2;
	private static final int CONTROL_CLIENT_PORT_DIALOG_TAG = 3;
	private static final int VIDEO_SERVER_PORT_DIALOG_TAG = 4;
	
	private static final String CONTROL_CLIENT_IP_KEY = "CONTROL_CLIENT_IP";
	private static final String CONTROL_CLIENT_PORT_KEY = "CONTROL_CLIENT_PORT";
	private static final String VIDEO_SERVER_PORT_KEY = "VIDEO_SERVER_PORT";
	
	public final static int DELAY = 150;
	public final static int CIRCLE_RADIUS = 40;
	
	private ThumbBall thumbBall;
	private TextView xPosTextView;
	private TextView yPosTextView;
	private FrameLayout main;
	private Button killButton;
	private ImageView videoImageView;
	
	private boolean shouldKill = false;
	private boolean shouldEnable = false;
	private boolean killEnabled = false;
	
	private ClientSocketService mainClientService;
	private ClientSocketService controlClientService;
	private ServerSocketByteService videoServerService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.control_robot_main);
        
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
            	makeControlConnectionRequest();
            	/*
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
            	*/
            }         
        });
        
        videoImageView = (ImageView) findViewById(R.id.video_image);
    }
    
	/*
    private void makeMainClientServiceConnection() {
		this.destroyMainClientServiceConnection();
		mainClientService = new ClientSocketService(this);
		mainClientService.makeConnection(getServerIPAddress(), getServerPortAddress(), true);
	}
	
	private void destroyMainClientServiceConnection() {
		if (mainClientService != null) {
			mainClientService.disconnect();
			mainClientService = null;
		}	
	}
	*/
	 private void makeControlClientServiceConnection(int port) {
			this.destroyControlClientServiceConnection();
			controlClientService = new ClientSocketService(this);
			controlClientService.makeConnection(this.getControlClientIP(), port, false);
		}
		
	private void destroyControlClientServiceConnection() {
		if (controlClientService != null) {
			controlClientService.disconnect();
			controlClientService = null;
		}	
	}
	
	private void makeVideoServerServiceConnection() {
		this.destroyVideoServerServiceConnection();
		videoServerService = new ServerSocketByteService(this);
		videoServerService.makeConnection(this.getVideoServerPort());
	}
	
	private void destroyVideoServerServiceConnection() {
		if (videoServerService != null) {
			videoServerService.disconnect();
			videoServerService = null;
		}	
	}
	
    
	private void makeControlConnectionRequest() {
		if (mainClientService != null && mainClientService.isConnected()) {
			String stringToSend = ControlCommunicationConstants.REQUEST_TYPE_CONTROL_CONNECTION;
			if (!mainClientService.sendStringToServer(stringToSend)) {
				Log.d("TouchControl", "ControlClientService wasn't able to send String");
			}
		}
	}
	
	
    @Override
    public void onStart() {
    	super.onStart();
    	thumbBall.setX(ThumbBall.FRAME_WIDTH / 2);
    	thumbBall.setY(ThumbBall.FRAME_HEIGHT / 2);
    	thumbBall.invalidate();
    }
    
    @Override
	protected void onStop() {
		super.onStop();
	}
    
    @Override
    protected void onDestroy() {
    	// prevents circular reference memory leak ???
		if (thumbBall != null) {
			thumbBall.setDelegate(null);
		}
		destroyVideoServerServiceConnection();
		destroyControlClientServiceConnection();
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.control_robot_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.toggle_control_client_state:
        	if (this.controlClientService != null) {
        		destroyControlClientServiceConnection();
        	} else {
        		makeControlClientServiceConnection(BeRobotActivity.DEFAULT_CONTROL_SERVER_PORT_ADDRESS);
        	}
            return true;
        case R.id.toggle_video_server_state:
        	if (this.videoServerService != null) {
        		destroyVideoServerServiceConnection();
        	} else {
        		makeVideoServerServiceConnection();
        	}
        	return true;
        case R.id.set_control_client_port:
        	this.letUserSetControlClientPort();
        	return true;
        case R.id.set_control_client_ip:
        	this.letUserSetControlClientIP();
        	return true;
        case R.id.set_video_server_port:
        	this.letUserSetVideoServerPort();
        	return true;
        case R.id.video_server_settings:
        case R.id.control_client_settings:
        case R.id.curr_control_device_ip:
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    
    	MenuItem controlClientItem = menu.findItem(R.id.toggle_control_client_state);
    	controlClientItem.setTitle(controlClientService != null ? R.string.stop_control_client : R.string.start_control_client);
    	controlClientItem.setEnabled(true);
    	
    	MenuItem serverVideoItem = menu.findItem(R.id.toggle_video_server_state);
    	serverVideoItem.setTitle(videoServerService != null ? R.string.stop_video_server : R.string.start_video_server);
    	
    	MenuItem setVideoServerPort = menu.findItem(R.id.set_video_server_port);
    	setVideoServerPort.setTitle("Video Server Port: " + Integer.toString(this.getVideoServerPort()));
    	
    	MenuItem currServerIPItem = menu.findItem(R.id.curr_control_device_ip);
    	currServerIPItem.setTitle("Device IP:  " + NetworkHelper.getLocalIpAddress());
    	currServerIPItem.setEnabled(false);
    	
    	MenuItem setControlClientPort = menu.findItem(R.id.set_control_client_port);
    	setControlClientPort.setTitle("Control Client Port: " + Integer.toString(this.getControlClientPort()));
    	
    	MenuItem setControlClientIP = menu.findItem(R.id.set_control_client_ip);
    	setControlClientIP.setTitle("Control IP: " + this.getControlClientIP());
    	
    	return true;
    }
    
    // EditTextDialogInterface
    public void dialogFinishedWithStatus(boolean positiveStatus, String endingString, int tag) {
    	if (positiveStatus) {
    		if (tag == CONTROL_CLIENT_PORT_DIALOG_TAG) {
    			this.setControlClientPort(Integer.parseInt(endingString));
    		} else if (tag == CONTROL_CLIENT_IP_DIALOG_TAG) {
    			this.setControlClientIP(endingString);
    		} else if (tag == VIDEO_SERVER_PORT_DIALOG_TAG) {
    			this.setVideoServerPort(Integer.parseInt(endingString));
    		}
    	}
    }
    
    // ThumbBallInterface
    @Override
	public void thumbBallPositionChanged(ThumbBall tb) {
		Log.d("OUTPUT", "X: " + tb.getX() + "  Y: " + tb.getY());
		xPosTextView.setText(Integer.toString(tb.translatedX()));
		yPosTextView.setText(Integer.toString(-tb.translatedY()));
		
		if (controlClientService != null && controlClientService.isConnected()) {
			Float xFloat = new Float(thumbBall.getX());
			Float yFloat = new Float(thumbBall.getY());
			
			String stringToSend = xFloat.toString() + ControlCommunicationConstants.DELIMITER + yFloat.toString();
			if (!controlClientService.sendStringToServer(stringToSend)) {
				Log.d("TouchControl", "ControlClientService wasn't able to send String");
			}
		}
	}
   
    
    // ClientSocketServiceInterface
    public void clientServiceStatusChange(ClientSocketService theService, String message, int status) {
    	Log.d("OUTPUT", message);
    }
	
	public String messageToSend() {
		Float xFloat = new Float(thumbBall.getX());
		Float yFloat = new Float(thumbBall.getY());
		return xFloat.toString() + ServerService.SERVER_DELIMITER + yFloat.toString();
	}
	
	public void clientServiceReceivedResponse(ClientSocketService theClientService, String response) {
		Log.d("OUTPUT", "Response:  " + response);
		String[] splitArray = response.split(ControlCommunicationConstants.DELIMITER);
		
		if (theClientService == mainClientService) {
			if (splitArray.length >= 1) {
				if (splitArray[0].equalsIgnoreCase(ControlCommunicationConstants.REQUEST_STATUS_FAILURE)) {
					Log.d("OUTPUT", "Failure!!!:  ");
				}
			} else if (splitArray[0].equalsIgnoreCase(ControlCommunicationConstants.REQUEST_STATUS_SUCCESS)) {
				if (splitArray.length >= 3) {
					if (splitArray[1].equalsIgnoreCase(ControlCommunicationConstants.RESPONSE_TYPE_CONTROL_CONNECTION)) {
						int port = Integer.parseInt(splitArray[2]);
						this.makeControlClientServiceConnection(port);
					}
				}
			}
		}
	}
	
	
	// ********************************
	// ServerSocketByteServiceInterface
    // ********************************
	@Override
	public void serverServiceStatusChange(ServerSocketByteService service, String message, int status) {
		
	}
	
	@Override
	public String serverServiceReceivedBitmap(ServerSocketByteService service, Bitmap theBitmap) {
		if (theBitmap != null) {
			videoImageView.setImageBitmap(theBitmap);
		}
		return null;
	}

	
    // ***************************
	// VideoServerService Settings
    // ***************************
    private int getVideoServerPort() {
    	return PreferenceHelper.getPreferenceIntForKey(this, VIDEO_SERVER_PORT_KEY, BeRobotActivity.DEFAULT_VIDEO_SERVER_PORT_ADDRESS);
    }
    
    private void setVideoServerPort(int newPort) {
    	PreferenceHelper.setPreferenceIntForKey(this, VIDEO_SERVER_PORT_KEY, newPort);
    }
    
    private void letUserSetVideoServerPort() {
    	DialogHelper.textEntryAlertDialog(this, "Video Server Port", 
    			Integer.toString(this.getVideoServerPort()), this, VIDEO_SERVER_PORT_DIALOG_TAG).show();
    }
    
    // *****************************
    // ControlClientService Settings
    // *****************************
    private int getControlClientPort() {
    	return PreferenceHelper.getPreferenceIntForKey(this, CONTROL_CLIENT_PORT_KEY, BeRobotActivity.DEFAULT_CONTROL_SERVER_PORT_ADDRESS);
    }
    
    private void setControlClientPort(int newPort) {
    	PreferenceHelper.setPreferenceIntForKey(this, CONTROL_CLIENT_PORT_KEY, newPort);
    }
    
    private void letUserSetControlClientPort() {
    	DialogHelper.textEntryAlertDialog(this, "Control Client Port", 
    			Integer.toString(this.getControlClientPort()), this, CONTROL_CLIENT_PORT_DIALOG_TAG).show();
    }
    
    private String getControlClientIP() {
    	return PreferenceHelper.getPreferenceStringForKey(this, CONTROL_CLIENT_IP_KEY, BeRobotActivity.DEFAULT_SERVER_IP_ADDRESS);
    }
    
    private void setControlClientIP(String newIP) {
    	PreferenceHelper.setPreferenceStringForKey(this, CONTROL_CLIENT_IP_KEY, newIP);
    }
    
    private void letUserSetControlClientIP() {
    	DialogHelper.textEntryAlertDialog(this, "Control Client IP", 
    			this.getControlClientIP(), this, CONTROL_CLIENT_IP_DIALOG_TAG).show();
    }

}
