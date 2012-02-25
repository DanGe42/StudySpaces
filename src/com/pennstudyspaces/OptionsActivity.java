package com.pennstudyspaces;

import com.pennstudyspaces.MainActivity.SendRequestTask;
import com.pennstudyspaces.api.StudySpacesApiRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class OptionsActivity extends Activity {
	private static final String TAG = OptionsActivity.class.getSimpleName();
	
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
		
		//Start & End Time fields
		String fromTime,toTime,date;
		fromTime = ((EditText)findViewById(R.id.from)).getText().toString();
		int fromTimeHour = Integer.parseInt(fromTime.substring(0, fromTime.indexOf(":")));
		int fromTimeMin = Integer.parseInt(fromTime.substring(fromTime.indexOf(":")+1,fromTime.length()));
		
		toTime = ((EditText)findViewById(R.id.to)).getText().toString();
		int toTimeHour = Integer.parseInt(toTime.substring(0, toTime.indexOf(":")));
		int toTimeMin = Integer.parseInt(toTime.substring(toTime.indexOf(":")+1,toTime.length()));
		
		//Date Field
		date = ((EditText)findViewById(R.id.date)).getText().toString();
		int slashIndex = date.indexOf("/");
		int secondSlash = date.indexOf("/", slashIndex+1);
		
		int month = Integer.parseInt(date.substring(0,slashIndex));
		int day = Integer.parseInt(date.substring(slashIndex+1,secondSlash));
		int year = Integer.parseInt(date.substring(secondSlash+1,date.length()));
		
		//Amenities Fields
		boolean priv,wboard,computer,projector;
		priv = ((CheckBox)findViewById(R.id.private_check)).isChecked();
		wboard = ((CheckBox)findViewById(R.id.whiteboard_check)).isChecked();
		projector = ((CheckBox)findViewById(R.id.projector_check)).isChecked();
		computer = ((CheckBox)findViewById(R.id.computer_check)).isChecked();
		
        int[] intArray = {numPeople,fromTimeHour,fromTimeMin,toTimeHour,toTimeMin,month,day,year};
        boolean[] boolArray = {priv,wboard,projector,computer};
		
		
        Intent i = new Intent();
        i.putExtra("INT_ARRAY", intArray);
        i.putExtra("BOOL_ARRAY", boolArray);
        setResult(RESULT_OK,i);
        
		finish();
	}
	
	public void backToMain(View view) {
		/*
		Intent i = new Intent(getApplicationContext(),MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i); 
		*/
		
		setResult(RESULT_CANCELED,new Intent());
		
		finish();
	}
}
