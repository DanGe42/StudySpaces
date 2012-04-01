package com.pennstudyspaces;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

public class SearchActivity extends Activity {
	private static final String TAG = SearchActivity.class.getSimpleName();

    private StudySpacesApplication app;
	
	private boolean priv, wboard, computer, projector;
	private int numPeople;
	private int fromTimeHour, fromTimeMin;
	private int toTimeHour, toTimeMin;	
	
	private int day,month,year;
	private String buildingName;
	
	private TextView dateDisplay;
	private Button dateButton;
	private DatePickerDialog.OnDateSetListener dateListener;
    private CheckBox privCheck, wboardCheck, compCheck, projCheck;

	public final static int DATE_DIALOG_ID = 0;
	
	private Spinner toSpinner;
	private Spinner fromSpinner;
	private Spinner numSpinner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        app = (StudySpacesApplication) getApplication();

		numPeople = 1;
		buildingName = "";
		priv = wboard = computer = projector = false;
		fromTimeHour = 0;
		fromTimeMin  = 0;
		toTimeHour = 23;
		toTimeMin = 0;
		
		//Set up various input widgets
        expandPrefs();
        initDateToggles();
		initSpinners();
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
                month = monthOfYear;
                day = dayOfMonth;
                updateDisplay();
            }
        };

        //Update display updates currently displayed date
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        updateDisplay();
    }

    private void initSpinners() {
        toSpinner   = (Spinner) findViewById(R.id.toTime);
        fromSpinner = (Spinner) findViewById(R.id.fromTime);
        numSpinner = (Spinner) findViewById(R.id.num_people);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.time_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        toSpinner.setAdapter(adapter);
        fromSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.numPeopleArray,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        numSpinner.setAdapter(adapter);
        
        //Set default time to 9a - 5p, seeing as no one really wants to
        //reserve a room at midnight.
        fromSpinner.setSelection(9);
        toSpinner.setSelection(17);
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
		//String fromTime,toTime;
		int fromPos = fromSpinner.getSelectedItemPosition();
		int toPos = toSpinner.getSelectedItemPosition();
		if(toPos <= fromPos) {
			toPos = fromPos + 1;
			if(toPos > 23) {
				toPos = 23;
				fromPos = 22;
			}
		}
		
		//Since the spinner indices correspond exactly to the hours, we don't
		//need to do any parsing
		fromTimeHour = fromPos;
		toTimeHour = toPos;
		
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
