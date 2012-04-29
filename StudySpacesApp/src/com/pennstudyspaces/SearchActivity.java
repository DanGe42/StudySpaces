package com.pennstudyspaces;

import java.util.Calendar;

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
import android.widget.TimePicker;

public class SearchActivity extends Activity {
	private static final String TAG = SearchActivity.class.getSimpleName();

    private StudySpacesApplication app;

    public static final String PRIVATE   = "private",
                               WBOARD    = "wboard",
                               COMPUTER  = "computer",
                               PROJECTOR = "projector",
                               QUANTITY = "num_people",
                               FROM_HR  = "fhr",
                               FROM_MIN = "fmin",
                               END_HR  = "ehr",
                               END_MIN = "emin",
                               DAY   = "day",
                               MONTH = "month",
                               YEAR  = "year",
                               FILTER = "filter";
	
	private boolean priv, wboard, computer, projector;
	private int numPeople;
	private int fromTimeHour, fromTimeMin;
	private int toTimeHour, toTimeMin;
	
	private int day,month,year;

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
    private String buildingName;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        app = (StudySpacesApplication) getApplication();

		numPeople = 1;
        buildingName = "";

		//Set up various input widgets
        expandPrefs();

        setDefaults();

        initTimePickers();
        initDateToggles();
		initSpinners();
	}

    @Override
    protected void onResume() {
        super.onResume();
        setDefaults();
        updateDisplay();
    }

    private void setDefaults() {
        Calendar now = Calendar.getInstance();
        Calendar defaultStart = getDefaultStartTime(now);
        Calendar defaultEnd   = getDefaultEndTime(defaultStart);

        // set up default search times
        fromTimeHour = defaultStart.get(Calendar.HOUR_OF_DAY);
        fromTimeMin  = defaultStart.get(Calendar.MINUTE);

        toTimeHour = defaultEnd.get(Calendar.HOUR_OF_DAY);
        toTimeMin  = defaultEnd.get(Calendar.MINUTE);

        day   = defaultStart.get(Calendar.DAY_OF_MONTH);
        month = defaultStart.get(Calendar.MONTH)+1;
        year  = defaultStart.get(Calendar.YEAR);

    }

    /* Gets the time-picking buttons and attaches handlers to them */
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

    /* Get the UI widgets dealing with dates and attach handlers */
    private void initDateToggles() {
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
        int currentMonth = c.get(Calendar.MONTH) + 1;
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

    /* Replicate the default search on the website. Basically, we round up to
     * the nearest 15th minute interval and return a Calendar with that time. */
    private Calendar getDefaultStartTime(Calendar now) {
        int minute = now.get(Calendar.MINUTE);
        int hour   = now.get(Calendar.HOUR_OF_DAY);
        int day    = now.get(Calendar.DAY_OF_YEAR);
        int year   = now.get(Calendar.YEAR);

        int nextHour  = hour;
        int nextDay   = day;
        int nextYear  = year;

        // round down to nearest 15 and add 15
        int nextMinute = (minute / 15) * 15 + 15;
        if (nextMinute >= 60) {
            nextMinute %= 60;
            nextHour += 1;

            if (nextHour >= 24) {
                nextHour %= 24;
                nextDay += 1;

                if (nextDay >= 365) {
                    nextDay %= 365;
                    nextYear += 1;
                }
            }
        }

        Calendar defStart = Calendar.getInstance();

        defStart.set(Calendar.MINUTE, nextMinute);
        defStart.set(Calendar.HOUR_OF_DAY, nextHour);
        defStart.set(Calendar.DAY_OF_YEAR, nextDay);
        defStart.set(Calendar.YEAR, nextYear);

        return defStart;
    }

    /* Take the default start and add one hour to it */
    private Calendar getDefaultEndTime(Calendar start) {
        Calendar defEnd = Calendar.getInstance();

        // copy some data over first
        defEnd.set(Calendar.MINUTE,      start.get(Calendar.MINUTE));
        defEnd.set(Calendar.DAY_OF_YEAR, start.get(Calendar.DAY_OF_YEAR));
        defEnd.set(Calendar.YEAR,        start.get(Calendar.YEAR));

        // and, NOW do the increment
        defEnd.set(Calendar.HOUR_OF_DAY, (start.get(Calendar.HOUR_OF_DAY) + 1) % 24);

        return defEnd;
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

	/** Called when the Submit button is pressed */
	public void submit(View view) {
		//Get values from various fields
		numPeople = numSpinner.getSelectedItemPosition() + 1;

//		buildingName = ((EditText)findViewById(R.id.building_name_input))
//						.getText().toString();

		//Start & End Time fields
		if(toTimeHour < fromTimeHour) {
			toTimeHour = fromTimeHour + 1;
		}

		//Amenities Fields
		priv      = privCheck.isChecked();
		wboard    = wboardCheck.isChecked();
		projector = projCheck.isChecked();
		computer  = compCheck.isChecked();

        Intent i = serializeToIntent();

        startActivity(i);
        setResult(RESULT_OK, i);
	}

    private Intent serializeToIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PRIVATE  , priv)
              .putExtra(WBOARD   , wboard)
              .putExtra(PROJECTOR, projector)
              .putExtra(COMPUTER , computer)
              .putExtra(QUANTITY, numPeople)
              .putExtra(FROM_HR , fromTimeHour)
              .putExtra(FROM_MIN, fromTimeMin)
              .putExtra(END_HR  , toTimeHour)
              .putExtra(END_MIN , toTimeMin)
              .putExtra(MONTH, month)
              .putExtra(DAY  , day)
              .putExtra(YEAR , year)
              .putExtra(FILTER, buildingName);

        return intent;
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

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,dateListener,year,month-1,day);
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
