package com.pennstudyspaces;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchActivity extends Activity {
	private static final String TAG = SearchActivity.class.getSimpleName();
	
	private boolean priv,wboard,computer,projector;
	private int numPeople;
	private int fromTimeHour,fromTimeMin;
	private int toTimeHour, toTimeMin;	
	private int day,month,year;
	
	private TextView dateDisplay;
	private Button dateButton;
	private DatePickerDialog.OnDateSetListener dateListener;
	public final static int DATE_DIALOG_ID = 0;
	
	private Spinner toSpinner;
	private Spinner fromSpinner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        
		numPeople = 1;
		priv = wboard = computer = projector = false;
		fromTimeHour = 0;
		fromTimeMin = 0;
		toTimeHour = 23;
		toTimeMin = 0;
		
		Calendar c = Calendar.getInstance();
		day = c.get(Calendar.DAY_OF_MONTH);
		month = c.get(Calendar.MONTH);
		year = c.get(Calendar.YEAR);
	
		dateButton = (Button) findViewById(R.id.dateButton);
		dateDisplay = (TextView) findViewById(R.id.dateDisplay);
		
		//Update display updates currently displayed date
		updateDisplay();
		
		dateButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		dateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int inputYear, int monthOfYear,
				int dayOfMonth) {
					year = inputYear;
					month = monthOfYear;
					day = dayOfMonth;
					updateDisplay();
			}
		};
		
		//Set up the spinner adapters
		toSpinner = (Spinner)findViewById(R.id.toTime);
		fromSpinner = (Spinner)findViewById(R.id.fromTime);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.time_array, 
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		
		toSpinner.setAdapter(adapter);
		fromSpinner.setAdapter(adapter);
		
		toSpinner.setSelection(1);
	}
	
	public void submitOptions(View view) {
		//Get values from various fields
		String intBuffer = ((EditText)findViewById(R.id.num_people)).getText().toString();
		
		if(!intBuffer.equals("")) {
			numPeople = Integer.parseInt(intBuffer);
		}
		
		//Start & End Time fields
		String fromTime,toTime;
		int fromPos = fromSpinner.getSelectedItemPosition();
		int toPos = toSpinner.getSelectedItemPosition();
		if(toPos <= fromPos) {
			toPos = fromPos + 1;
			if(toPos > 23) {
				toPos = 23;
				fromPos = 22;
			}
		}		
		
		fromTime = fromSpinner.getItemAtPosition(fromPos).toString();
		int fromColonPos = fromTime.indexOf(":");
		fromTimeHour = Integer.parseInt(fromTime.substring(0, fromColonPos));
		
		toTime = toSpinner.getItemAtPosition(toPos).toString();
		int toColonPos = toTime.indexOf(":");
		toTimeHour = Integer.parseInt(toTime.substring(0, toColonPos));
		
		//toTime is always after fromTime, so we only need to check
		//if fromTime is PM
		if(fromPos > 11) {
			fromTimeHour += 12;
			toTimeHour += 12;
		}
		
		//Amenities Fields
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
		setResult(RESULT_CANCELED,new Intent());
		finish();
	}
	
	//Relevant methods for Date Picker
	private void updateDisplay() {
		//Months are zero based, so need to add 1
		String date ="  " + (month + 1) + "/" + day + "/" + year;
		dateDisplay.setText(date);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,dateListener,year,month,day);

		default:
			return null;
		}
	}
}
