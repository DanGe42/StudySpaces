package com.pennstudyspaces;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.pennstudyspaces.api.ApiRequest;
import com.pennstudyspaces.api.ParamsRequest;
import com.pennstudyspaces.api.RoomKind;
import com.pennstudyspaces.api.StudySpacesData;
import static com.pennstudyspaces.StudySpacesApplication.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MainActivityDead.class.getSimpleName();
    private StudySpacesApplication app;
    private RoomKind[] data;
    private LocationManager locManager;

    private ListView spacesList;
    private Spinner peopleSpinner;
    private TextView progressButton;
    private Button dateButton, searchButton, filterButton, fromTime, toTime;
    private CheckBox privCheck, projCheck, compCheck, whiteCheck;
    private static final int DIALOG_BAD_CONNECTION = 0;
    private static final int DATE_DIALOG_ID = 1,
                             TO_TIME_DIALOG_ID = 2,
                             FROM_TIME_DIALOG_ID = 3;

    private TimeManager timeManager;
    private TimePickerDialog.OnTimeSetListener fromListener;
    private TimePickerDialog.OnTimeSetListener toListener;
    private DatePickerDialog.OnDateSetListener dateListener;
    private static final int SORT_LOCATION = 0,
                             SORT_ALPHA = 1;
    private EditText filterText;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        app = (StudySpacesApplication) getApplication();

        spacesList = (ListView) findViewById(R.id.spaces_list);
        app.getPrefs().registerOnSharedPreferenceChangeListener(this);

        // Display the below TextView instead if the spaces list is empty
        spacesList.setEmptyView(findViewById(R.id.spaces_list_empty));

        // Grab the rest of our views
        peopleSpinner = (Spinner) findViewById(R.id.num_people);
        dateButton   = (Button) findViewById(R.id.dateButton);
        searchButton = (Button) findViewById(R.id.submit);
        fromTime     = (Button) findViewById(R.id.fromTime);
        toTime       = (Button) findViewById(R.id.toTime);
        filterButton = (Button) findViewById(R.id.filter);
        privCheck  = (CheckBox) findViewById(R.id.private_check);
        projCheck  = (CheckBox) findViewById(R.id.whiteboard_check);
        compCheck  = (CheckBox) findViewById(R.id.computer_check);
        whiteCheck = (CheckBox) findViewById(R.id.whiteboard_check);
        progressButton = (TextView) findViewById(R.id.search_progress);
        filterText = (EditText) findViewById(R.id.filterText);

        timeManager = new TimeManager();

        init();
    }

    private void init() {
        expandPrefs();
        setDefaults();
        initTimePickers();
        initDateToggles();
        updateDisplay();
        initSpinners();

        // Listener that displays a dialog when a study space is clicked on
        spacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View childView,
                                    int position, long id) {
                // Get the building name of the place that was just clicked on
                Intent intent = new Intent(getApplicationContext(),
                        RoomDetailsActivity.class);

                SimpleAdapter adapter = (SimpleAdapter) parentView.getAdapter();
                RoomKind kind = (RoomKind) adapter.getItem(position);

                // Stuff some information into the Intent
                intent.putExtra(BUILDING  , kind.getParentBuilding().getName());
                intent.putExtra(LONGITUDE , kind.getParentBuilding().getLongitude());
                intent.putExtra(LATITUDE  , kind.getParentBuilding().getLatitude());

                intent.putExtra(PROJECTOR , kind.hasProjector());
                intent.putExtra(COMPUTER  , kind.hasComputer());
                intent.putExtra(NAME      , kind.getName());
                intent.putExtra(PRIVACY   , kind.getPrivacy());
                intent.putExtra(WHITEBOARD, kind.hasWhiteboard());
                intent.putExtra(QUANTITY, kind.getCapacity());
                intent.putExtra(RESERVE   , kind.getReserveType());
                intent.putExtra(COMMENT   , kind.getComments());

                intent.putExtra(ROOMNUM   , kind.getRooms().get(0).getId());
                intent.putExtra(FROM_HR, timeManager.getFromTimeHour());
                intent.putExtra(FROM_MIN, timeManager.getFromTimeMin());
                intent.putExtra(END_HR, timeManager.getToTimeHour());
                intent.putExtra(END_MIN, timeManager.getToTimeMin());
                intent.putExtra(MONTH     , timeManager.getMonth());
                intent.putExtra(DAY       , timeManager.getDay());
                intent.putExtra(YEAR      , timeManager.getYear());
                startActivity(intent);
            }
        });
    }

    private void updateDisplay() {
        //Months are zero based, so need to add 1
        int month = timeManager.getMonth();
        int day = timeManager.getDay();
        int year = timeManager.getYear();
        int fromTimeHour = timeManager.getFromTimeHour();
        int fromTimeMin = timeManager.getFromTimeMin();
        int toTimeHour = timeManager.getToTimeHour();
        int toTimeMin = timeManager.getToTimeMin();

        String date = month + "/" + day + "/" + year;
        dateButton.setText(date);

        String am_pm = (fromTimeHour - 12 >= 0) ? "PM" : "AM";
        String time = formatHour(fromTimeHour) + ":" + pad(fromTimeMin) + " " + am_pm;
        fromTime.setText(time);

        am_pm = (toTimeHour - 12 >= 0) ? "PM" : "AM";
        time = formatHour(toTimeHour) + ":" + pad(toTimeMin) + " " + am_pm;
        toTime.setText(time);
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

    private void expandPrefs() {
        SharedPreferences prefs = app.getPrefs();

        privCheck  = (CheckBox) findViewById(R.id.private_check);
        whiteCheck = (CheckBox) findViewById(R.id.whiteboard_check);
        compCheck  = (CheckBox) findViewById(R.id.computer_check);
        projCheck  = (CheckBox) findViewById(R.id.projector_check);

        boolean priv      = prefs.getBoolean("private",    false);
        boolean wboard    = prefs.getBoolean("whiteboard", false);
        boolean computer  = prefs.getBoolean("computer",   false);
        boolean projector = prefs.getBoolean("projector",  false);

        privCheck.setChecked(priv);
        whiteCheck.setChecked(wboard);
        compCheck.setChecked(computer);
        projCheck.setChecked(projector);
    }

    private void setDefaults() {
        Calendar now = Calendar.getInstance();
        Calendar defaultStart = getDefaultStartTime(now);
        Calendar defaultEnd   = getDefaultEndTime(defaultStart);

        // set up default search times
        int fromTimeHour = defaultStart.get(Calendar.HOUR_OF_DAY);
        int fromTimeMin  = defaultStart.get(Calendar.MINUTE);

        int toTimeHour = defaultEnd.get(Calendar.HOUR_OF_DAY);
        int toTimeMin  = defaultEnd.get(Calendar.MINUTE);

        int day   = defaultStart.get(Calendar.DAY_OF_MONTH);
        int month = defaultStart.get(Calendar.MONTH)+1;
        int year  = defaultStart.get(Calendar.YEAR);

        timeManager.setFromTime(fromTimeHour, fromTimeMin);
        timeManager.setToTime(toTimeHour, toTimeMin);
        timeManager.setDate(year, month, day);
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

    /* Gets the time-picking buttons and attaches handlers to them */
    private void initTimePickers() {
        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(FROM_TIME_DIALOG_ID);
            }
        });
        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TO_TIME_DIALOG_ID);
            }
        });
        fromListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeManager.setFromTime(hourOfDay, minute);
                updateDisplay();
            }
        };
        toListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeManager.setToTime(hourOfDay, minute);
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
                //months in the DatePicker are 0-indexed
                clipDate(inputYear, monthOfYear + 1, dayOfMonth);

                updateDisplay();
            }
        };
    }

    private void clipDate(int year, int month, int day) {
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

        timeManager.setDate(year, month, day);
    }

    private void initSpinners() {
        peopleSpinner = (Spinner) findViewById(R.id.num_people);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.numPeopleArray,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        peopleSpinner.setAdapter(adapter);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder;

        switch (id) {
            case DIALOG_BAD_CONNECTION:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Error")
                        .setMessage("Could not connect to Penn StudySpaces. Please" +
                                " check your connection and touch Refresh.")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                return builder.create();
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,dateListener,
                        timeManager.getYear(), timeManager.getMonth() - 1,
                        timeManager.getDay());
            case TO_TIME_DIALOG_ID:
                return new TimePickerDialog(this, toListener,
                        timeManager.getToTimeHour(),
                        timeManager.getToTimeMin(), false);
            case FROM_TIME_DIALOG_ID:
                return new TimePickerDialog(this, fromListener,
                        timeManager.getFromTimeHour(),
                        timeManager.getFromTimeMin(), false);
        }

        return null;
    }

    public void search(View v) {
        search();
    }

    public void filter(View v) {
        filter();
    }

    private void search() {
        int numPeople = peopleSpinner.getSelectedItemPosition() + 1;

//        String date = String.format("date=%d-%d-%d", timeManager.getYear(),
//                timeManager.getMonth(), timeManager.getDay());

        ParamsRequest request = new ParamsRequest();
        request.setDate(timeManager.getYear(), timeManager.getMonth(),
                timeManager.getDay());
        request.setStartTime(timeManager.getFromTimeHour(),
                timeManager.getFromTimeMin());
        request.setEndTime(timeManager.getToTimeHour(),
                timeManager.getToTimeMin());
        request.setNumberOfPeople(numPeople);

        (new SendRequestTask(this)).execute(request);
    }

    private void filter() {
        refreshList();
    }

    // Private class for managing the state of time in the search/filter section
    private class TimeManager {
        private int fromTimeHour, fromTimeMin,
                toTimeHour, toTimeMin,
                day, month, year;

        public void setFromTime(int hour, int min) {
            fromTimeHour = hour;
            fromTimeMin = min;
        }

        public void setToTime(int hour, int min) {
            toTimeHour = hour;
            toTimeMin = min;
        }

        public void setDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public int getFromTimeHour() {
            return fromTimeHour;
        }

        public int getFromTimeMin() {
            return fromTimeMin;
        }

        public int getToTimeHour() {
            return toTimeHour;
        }

        public int getToTimeMin() {
            return toTimeMin;
        }

        public int getDay() {
            return day;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }
    }

    // Performs a getJSON request in the background, so we don't block on the UI
    class SendRequestTask
            extends AsyncTask<ApiRequest, Void, StudySpacesData> {
        Context ctx;
        // ProgressDialog dialog;

        public SendRequestTask(Context ctx) {
            super();
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressButton.setText("Searching...");
            searchButton.setEnabled(false);
            //dialog = ProgressDialog.show(ctx, "", "Refreshing...", true, true);
        }

        @Override
        protected StudySpacesData doInBackground(ApiRequest... req) {
            // we don't need to publish progress updates, unless we want to
            // implement some kind of timeout publishProgress();
            try {
                StudySpacesData data = new StudySpacesData(req[0]);
                return data;
            }
            catch (IOException e) {
                Log.e(TAG, "Something bad happened", e);
                return null;
            }
        }

        protected void onPostExecute(StudySpacesData result) {
            searchButton.setEnabled(true);
            if (result == null) {
                showDialog(DIALOG_BAD_CONNECTION);
                progressButton.setText("Error");
                return;
            }
            progressButton.setText("Done!");
            data = result.getRoomKinds();
            refreshList();
        }
    }

    private void refreshList() {
        SharedPreferences prefs = app.getPrefs();
        int sortOption = Integer.parseInt(prefs.getString("sort", "1"));

        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null) {
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        // defaults
        double latitude = 39.953278;
        double longitude = -75.19846;

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        RoomKind[] filtered = applyFilter(data,
                                          privCheck.isChecked(),
                                          compCheck.isChecked(),
                                          whiteCheck.isChecked(),
                                          projCheck.isChecked(),
                                          filterText.getText().toString()
                                          );

        switch (sortOption) {
            case SORT_LOCATION:
                spacesList.setAdapter(
                        DataListAdapter.createLocationSortedAdapter(
                                this, filtered, latitude, longitude));
                break;
            case SORT_ALPHA:
                spacesList.setAdapter(
                        DataListAdapter.createAlphaSortedAdapater(this, filtered,
                                latitude, longitude));
                break;
        }
    }

    private RoomKind[] applyFilter (RoomKind[] kinds, boolean priv, boolean comp,
                                    boolean white, boolean proj, String filter) {
        ArrayList<RoomKind> filtered = new ArrayList<RoomKind>(kinds.length);

        for (RoomKind kind : kinds) {
            // Boolean tests
            if (!(!priv || (kind.getPrivacy() == RoomKind.Privacy.PRIVATE)))
                continue;
            if (!(!comp || kind.hasComputer()))
                continue;
            if (!(!white || kind.hasWhiteboard()))
                continue;
            if (!(!proj || kind.hasProjector()))
                continue;

            // String filter
            if (!(kind.getName().contains(filter) ||
                    kind.getParentBuilding().getName().contains(filter)))
                continue;

            filtered.add(kind);
        }

        RoomKind[] results = new RoomKind[1];
        results = filtered.toArray(results);
        return results;
    }
}
