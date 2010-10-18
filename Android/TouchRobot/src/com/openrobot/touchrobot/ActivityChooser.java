package com.openrobot.touchrobot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityChooser extends Activity implements OnClickListener {
    
	Button onRobotButton;
	Button controlRobotButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        onRobotButton = (Button) this.findViewById(R.id.on_robot_activity_button);
        onRobotButton.setOnClickListener(this);
        controlRobotButton = (Button) this.findViewById(R.id.control_robot_activity_button);
        controlRobotButton.setOnClickListener(this);
        
        
        
    }
    
    @Override
    public void onClick(View v) {
    	Intent myIntent = null;
    	if (v == onRobotButton) {
    		myIntent = new Intent(ActivityChooser.this, BeRobotActivity.class);
    	} else if (v == controlRobotButton) {
    		myIntent = new Intent(ActivityChooser.this, ControlRobotActivity.class);
    	}
    	if (myIntent != null) {
        	ActivityChooser.this.startActivity(myIntent);	
    	}
    }
}