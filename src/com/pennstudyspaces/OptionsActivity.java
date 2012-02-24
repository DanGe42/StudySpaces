package com.pennstudyspaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class OptionsActivity extends Activity {

	public static final int ACTIVITY_SortedDisplayActivity = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
	}
	
	public void submitOptions(View view) {
		//Get values from various fields
		String buffer = ((EditText)findViewById(R.id.num_people)).getText().toString();
		
		int numPeople = 1;
		try {
			numPeople = Integer.parseInt(buffer);
		}
		catch(Exception e) {
			//Show a toast for invalid number entries?
		}
		
		String fromTime,toTime,date;
		fromTime = ((EditText)findViewById(R.id.from)).getText().toString();
		toTime = ((EditText)findViewById(R.id.to)).getText().toString();
		date = ((EditText)findViewById(R.id.date)).getText().toString();
		
		boolean priv,whiteboard,computer,projector;
		priv = ((CheckBox)findViewById(R.id.private_check)).isChecked();
		whiteboard = ((CheckBox)findViewById(R.id.whiteboard_check)).isChecked();
		projector = ((CheckBox)findViewById(R.id.projector_check)).isChecked();
		computer = ((CheckBox)findViewById(R.id.computer_check)).isChecked();
		
		
		Intent i = new Intent(this, SortedDisplayActivity.class);
    	
    	startActivityForResult(i, OptionsActivity.ACTIVITY_SortedDisplayActivity);
		
	}
	
	public void backToMain(View view) {
		Intent i = new Intent();
		
		setResult(RESULT_OK,i);
		
		finish();
	}
}
