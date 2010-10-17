package com.robot.open;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


// should be a static helper in file DialogHelper instead. Taking message, and onClickListener, (maybe object and methodname)
public class EditStringDialog {
	public final static int DIALOG_STATUS_CANCELLED = 0;
	public final static int DIALOG_STATUS_OKAY = 1;
	
	private String editTextString;
	private int tag;
	private EditStringDialogInterface delegate;
	
	public EditStringDialog(Activity context, 
			String title, 
			String editTextString, 
			int tag, 
			EditStringDialogInterface delegate) {

		this.editTextString = editTextString;
		this.tag = tag;
		this.delegate = delegate;
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   	if (id == AlertDialog.BUTTON_POSITIVE) {
	       				Log.d("LOGGGGGG", "POSITIVE");	
	       			} else {
	       				Log.d("LOGGGGGG", "NEGATIVE");
	       			} 
	           }
	    };
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(title)
	       .setCancelable(false)
	       .setPositiveButton("Okay", listener)
	       .setNegativeButton("Cancel", listener);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.string_setting_dialog, (ViewGroup) context.findViewById(R.id.dialog_root_view));
    	
    	EditText dialogEditText = (EditText)layout.findViewById(R.id.dialog_edittext);
    	dialogEditText.setText(editTextString);
    	
    	
    	builder.setView(layout);
    	
    	AlertDialog dialog = builder.create();
    	dialog.show();
	}

	public String getEditTextString() {
		return editTextString;
	}

	public void setEditTextString(String editTextString) {
		this.editTextString = editTextString;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public EditStringDialogInterface getDelegate() {
		return delegate;
	}

	public void setDelegate(EditStringDialogInterface delegate) {
		this.delegate = delegate;
	}

}


interface EditStringDialogInterface {
	public void dialogFinishedWithStatus(int status, EditStringDialog dialog);	
}

