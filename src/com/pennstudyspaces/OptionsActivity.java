package com.pennstudyspaces;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
		if(!buffer.equals("")) {
			numPeople = Integer.parseInt(buffer);
		}
		
		//Start & End Time fields
		String fromTime,toTime,date;
		fromTime = ((EditText)findViewById(R.id.from)).getText().toString();
		
		int fromTimeHour,fromTimeMin;
		if(fromTime.equals("")) {
			fromTimeHour = 0;
			fromTimeMin = 0;
		}
		else {
			fromTimeHour = Integer.parseInt(fromTime.substring(0, fromTime.indexOf(":")));
			if(fromTimeHour < 0) {
				fromTimeHour = 0;
			}
			else if(fromTimeHour > 23) {
				fromTimeHour = 23;
			}
			
			fromTimeMin = Integer.parseInt(fromTime.substring(fromTime.indexOf(":")+1,fromTime.length()));
			if(fromTimeMin < 0) {
				fromTimeMin = 0;
			}
			else if(fromTimeMin > 59) {
				fromTimeMin = 59;		
			}
		}
		
		toTime = ((EditText)findViewById(R.id.to)).getText().toString();
		int toTimeHour, toTimeMin;
		if(toTime.equals("")) {
			toTimeHour = 23;
			toTimeMin = 59;
		}
		else {
			toTimeHour = Integer.parseInt(toTime.substring(0, toTime.indexOf(":")));
			if(toTimeHour < 0) {
				toTimeHour = 0;
			}
			else if(toTimeHour > 23) {
				toTimeHour = 23;
			}
			
			toTimeMin = Integer.parseInt(toTime.substring(toTime.indexOf(":")+1,toTime.length()));
			if(toTimeMin < 0) {
				toTimeMin = 0;
			}
			else if(toTimeMin > 59) {
				toTimeMin = 59;
			}
		}
		
		//Date Field
		date = ((EditText)findViewById(R.id.date)).getText().toString();
		int month,day,year;
		if(date.equals("")) {
			Calendar temp = new GregorianCalendar();
			month = temp.get(Calendar.MONTH);
			day = temp.get(Calendar.MONTH);
			year = temp.get(Calendar.YEAR);
		}
		else {
			int slashIndex = date.indexOf("/");
			int secondSlash = date.indexOf("/", slashIndex+1);
			
			month = Integer.parseInt(date.substring(0,slashIndex));
			if(month > 12) {
				month = 12;
			}
			else if(month < 1) {
				month = 1;
			}
			
			day = Integer.parseInt(date.substring(slashIndex+1,secondSlash));
			if(day < 1) {
				day = 1;
			}
			else if(day > 31) {
				day = 31;
			}
			
			year = Integer.parseInt(date.substring(secondSlash+1,date.length()));
			if(year < 2011) {
				year = 2011;
			}
		}

		
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
