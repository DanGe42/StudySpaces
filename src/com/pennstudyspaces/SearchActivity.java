package com.pennstudyspaces;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class SearchActivity extends Activity {
	private static final String TAG = SearchActivity.class.getSimpleName();

    private StudySpacesApplication app;
	
	private boolean priv, wboard, computer, projector;
	private int numPeople;
	private int fromTimeHour, fromTimeMin;
	private int toTimeHour, toTimeMin;	
	
	private int day,month,year;
	private String buildingName;
	
	private Button fromButton;
	private TimePickerDialog.OnTimeSetListener fromListener;
	private Button toButton;
	private TimePickerDialog.OnTimeSetListener toListener;
	
	private Button dateButton;
	private DatePickerDialog.OnDateSetListener dateListener;
    private CheckBox privCheck, wboardCheck, compCheck, projCheck;

	public final static int DATE_DIALOG_ID = 0;
	public final static int TO_TIME_DIALOG_ID = 1;
	public final static int FROM_TIME_DIALOG_ID = 2;
	
	private Spinner numSpinner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        app = (StudySpacesApplication) getApplication();

		numPeople = 1;
		buildingName = "";
		priv = wboard = computer = projector = false;
		fromTimeHour = 9;
		fromTimeMin  = 0;
		toTimeHour = 17;
		toTimeMin = 0;
		
		//Set up various input widgets
        expandPrefs();
        initTimePickers();
        initDateToggles();
		initSpinners();
	}
	
	private void initTimePickers() {
		fromButton = (Button) findViewById(R.id.fromTime);
		toButton = (Button) findViewById(R.id.toTime);
		
		fromButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(FROM_TIME_DIALOG_ID);	
			}
		});
		toButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(TO_TIME_DIALOG_ID);	
			}
		});
		fromListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				fromTimeHour = hourOfDay;
				fromTimeMin = minute;
				updateDisplay();
			}
		};
		toListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				toTimeHour = hourOfDay;
				toTimeMin = minute;
				updateDisplay();
			}
		};
	}

    private void initDateToggles() {
        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        dateButton = (Button) findViewById(R.id.dateButton);
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
                //months in the DatePicker are 0-indexed
                month = monthOfYear + 1;
                day = dayOfMonth;
                clipDate();
                
                updateDisplay();
            }
        };
        
        updateDisplay();
    }
    
    private void clipDate() {
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentMonth = c.get(Calendar.MONTH);
        int currentYear = c.get(Calendar.YEAR);
        
        if(year < currentYear) {
        	year = currentYear;
        }
        if(month < currentMonth) {
        	month = currentMonth;
        	day = currentDay;
        }
        
        if(year == currentYear && month == currentMonth && day < currentDay) {
        	day = currentDay;
        }
    }

    private void initSpinners() {
        numSpinner = (Spinner) findViewById(R.id.num_people);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.numPeopleArray,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        numSpinner.setAdapter(adapter);
    }

    private void expandPrefs() {
        SharedPreferences prefs = app.getPrefs();

        privCheck   = (CheckBox) findViewById(R.id.private_check);
        wboardCheck = (CheckBox) findViewById(R.id.whiteboard_check);
        compCheck   = (CheckBox) findViewById(R.id.computer_check);
        projCheck   = (CheckBox) findViewById(R.id.projector_check);

        priv      = prefs.getBoolean("private",    false);
        wboard    = prefs.getBoolean("whiteboard", false);
        computer  = prefs.getBoolean("computer",   false);
        projector = prefs.getBoolean("projector",  false);

        privCheck.setChecked(priv);
        wboardCheck.setChecked(wboard);
        compCheck.setChecked(computer);
        projCheck.setChecked(projector);
    }
	
	public void submitOptions(View view) {
		//Get values from various fields
		numPeople = numSpinner.getSelectedItemPosition() + 1;
		
		buildingName = ((EditText)findViewById(R.id.building_name_input))
						.getText().toString();
		
		//Start & End Time fields
		if(toTimeHour < fromTimeHour) {
			toTimeHour = fromTimeHour + 1;
		}
		
		//Amenities Fields
		priv      = privCheck.isChecked();
		wboard    = wboardCheck.isChecked();
		projector = projCheck.isChecked();
		computer  = compCheck.isChecked();
		
        int[] intArray = {numPeople,fromTimeHour,fromTimeMin,
        				  toTimeHour,toTimeMin,month,day,year};
        boolean[] boolArray = {priv,wboard,projector,computer};
		
        Intent i = new Intent();
        i.putExtra("INT_ARRAY", intArray);
        i.putExtra("BOOL_ARRAY", boolArray);
        i.putExtra("BUILDING NAME", buildingName);
        setResult(RESULT_OK,i);
        
		finish();
	}
	
	public void backToMain(View view) {		
		setResult(RESULT_CANCELED,new Intent());
		finish();
	}
	
	private String pad(int c) {
	    if (c >= 10)
	        return String.valueOf(c);
	    else
	        return "0" + String.valueOf(c);
	}
	
	private int formatHour(int hour) {
		return (hour - 12 > 0) ? hour - 12 : hour;
	}
	
	//Relevant methods for Date Picker
	private void updateDisplay() {
		//Months are zero based, so need to add 1
		String date = month + "/" + day + "/" + year;
		dateButton.setText(date);
		
		String am_pm = (fromTimeHour - 12 >= 0) ? "PM" : "AM";
		String time = formatHour(fromTimeHour) + ":" + pad(fromTimeMin) + " " + am_pm;
		fromButton.setText(time);
		
		am_pm = (toTimeHour - 12 >= 0) ? "PM" : "AM";
		time = formatHour(toTimeHour) + ":" + pad(toTimeMin) + " " + am_pm;
		toButton.setText(time);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,dateListener,year,month,day);
		case TO_TIME_DIALOG_ID:
			return new TimePickerDialog(this,
					toListener,toTimeHour,toTimeMin,false);
		case FROM_TIME_DIALOG_ID:
			return new TimePickerDialog(this,
					fromListener,fromTimeHour,fromTimeMin,false);			
		default:
			return null;
		}
	}
}
