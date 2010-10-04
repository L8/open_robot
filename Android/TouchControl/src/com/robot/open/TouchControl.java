package com.robot.open;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;
 
public class TouchControl extends Activity implements ThumbBallListener, ServerServiceInterface {
	
	private static final String DEVICE_ADDRESS = "00:07:80:91:32:51";
	private static final char ARDUINO_CONTROL_INPUT_FUNCTION_FLAG = 'c';
	private static final char ARDUINO_SHOULD_KILL_FUNCTION_FLAG = 'd';
	private static final boolean BLUETOOTH_ENABLED = false; 
	
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
	
	private ClientService clientService;
	private ServerService serverService;
	boolean clientServiceIsBound;
	boolean serverServiceIsBound;
	
	private long lastChange;
	
	private Intent clientServiceIntent;
	private Intent serverServiceIntent;
	
	private ServiceConnection clientConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        clientService = ((ClientService.ClientBinder)service).getService();

	        // Tell the user about this for our demo.
	        Toast.makeText(currentContext(), R.string.client_service_connected, Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        clientService = null;
	        //Toast.makeText(Binding.this, R.string.local_service_disconnected, Toast.LENGTH_SHORT).show();
	    }
	};
	
	private ServiceConnection serverConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        serverService = ((ServerService.ServerBinder)service).getService();

	        // Tell the user about this for our demo.
	        Toast.makeText(currentContext(), R.string.server_service_connected, Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        serverService = null;
	        Toast.makeText(currentContext(), R.string.server_service_disconnected, Toast.LENGTH_SHORT).show();
	    }
	};


	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (BLUETOOTH_ENABLED) {
            // Establish bluetooth connection with Arduino
            Amarino.connect(this, DEVICE_ADDRESS);	
        }
        
        serverServiceIntent = new Intent(this, ServerService.class);
        clientServiceIntent = new Intent(this, ClientService.class);
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
        
        ((Button)this.findViewById(R.id.server_connection_button)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	makeServerConnection();
            	
            }
        });
        
        ((Button)this.findViewById(R.id.client_connection_button)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	clientService.makeConnection();
            }
        });
    }
    
    void makeServerConnection() {
    	serverService.setDelegate(this);
    	serverService.makeConnection();
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
		unbindClientService();
		unbindServerService();
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.start_server:
            bindServerService();
            return true;
        case R.id.start_client:
            bindClientService();
            return true;
        case R.id.stop_server:
            unbindServerService();
            return true;
        case R.id.stop_client:
            bindClientService();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void bindServerService() {
    	// Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(serverServiceIntent, serverConnection, Context.BIND_AUTO_CREATE);
        serverServiceIsBound = true;
    } 
    
    private void unbindServerService() {
    	if (serverServiceIsBound) {	
            // Detach our existing connection.
            //unbindService(serverConnection);  // this was causing a crash so using stopService temporarily instead
    		stopService(serverServiceIntent);
            serverServiceIsBound = false;
        }
    }
    
    private void bindClientService() {
    	// Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(clientServiceIntent, clientConnection, Context.BIND_AUTO_CREATE);
        clientServiceIsBound = true;
    }
    
    private void unbindClientService() {
    	if (clientServiceIsBound) {
            // Detach our existing connection.
            //unbindService(clientConnection);   // this was causing a crash so using stopService temporarily instead
    		stopService(clientServiceIntent);
            clientServiceIsBound = false;
        }
    }
    
    private Context currentContext() {
    	return this;
    }
    
    // ThumbBallInterface
    @Override
	public void thumbBallPositionChanged(ThumbBall tb) {
		Log.d("OUTPUT", "X: " + tb.getX() + "  Y: " + tb.getY());
		xPosTextView.setText(Integer.toString(tb.translatedX()));
		yPosTextView.setText(Integer.toString(-tb.translatedY()));
		
		this.messageArduinoIfAppropriate((int)thumbBall.getX(), (int)thumbBall.getY());
	}
    
    // ServerServiceInterface
    @Override
	public void serverServiceStatusChange(String message, int status) {
    	Log.d("OUTPUT", message);
    }
 
    private void messageArduinoIfAppropriate(int x, int y) {
    	if (shouldKill || shouldEnable) {
    		// Send unconditional kill or resume message
    		if (BLUETOOTH_ENABLED) {
    			sendIntToArduino(shouldKill ? 1 : 0, ARDUINO_SHOULD_KILL_FUNCTION_FLAG);	
    		}
			shouldKill = false;
			shouldEnable = false;
			thumbBall.zeroThumbBallPosition();
			return;
		}
    	if (System.currentTimeMillis() - lastChange > DELAY ) {
		 
			int[] values = new int[2];
			values[0] = x;   
			values[1] = y;
			
			if (BLUETOOTH_ENABLED) {
				sendIntArrayToArduino(values, ARDUINO_CONTROL_INPUT_FUNCTION_FLAG);	
			}
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