package com.pennstudyspaces;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.pennstudyspaces.api.ApiRequest;
import com.pennstudyspaces.api.ParamsRequest;
import com.pennstudyspaces.api.RoomKind;
import com.pennstudyspaces.api.StudySpacesData;

import static android.content.SharedPreferences.*;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private StudySpacesApplication app;
    private ListView spacesList;

    private HashMap<String, Integer> dateRange;
    private LocationManager locManager;

    private ParamsRequest currentRequest;
    private StudySpacesData ssData;

    // some parameters that will go into the reservation string
    private String reserveString;

    private String roomFilter;
    
    public static final int ACTIVITY_OptionsActivity = 1;
    
    public static final int DIALOG_BAD_CONNECTION = 1;
    
    // Intent constants for RoomDetailsActivity
    public static final String BUILDING   = "building",
                               LONGITUDE  = "longitude",
                               LATITUDE   = "latitude",
                               NAME       = "name",
                               PROJECTOR  = "projector",
                               COMPUTER   = "computer",
                               PRIVACY    = "privacy",
                               WHITEBOARD = "whiteboard",
                               CAPACITY   = "capacity",
                               RESERVE    = "reserve",
                               COMMENT    = "comment",
    						   ROOMNUM 	  = "roomnum",
    						   FRHOUR     = "fromhour",
    						   FRMIN      = "fromtmin",
					           TOHOUR     = "tohour",
                               TOMIN      = "tomin",
                               MONTH      = "month",
                               DAY        = "day",
                               YEAR       = "year";

    private static final int SORT_LOCATION = 1,
                             SORT_ALPHA    = 2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        dateRange = new HashMap<String, Integer>();
        
        app = (StudySpacesApplication) getApplication();
        spacesList = (ListView) findViewById(R.id.spaces_list);
        app.getPrefs().registerOnSharedPreferenceChangeListener(this);
        
        // Display the below TextView instead if the spaces list is empty
        spacesList.setEmptyView(findViewById(R.id.spaces_list_empty));
        
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
                intent.putExtra(CAPACITY  , kind.getCapacity());
                intent.putExtra(RESERVE   , kind.getReserveType());
                intent.putExtra(COMMENT   , kind.getComments());

                intent.putExtra(ROOMNUM   , kind.getRooms().get(0).getId());
                intent.putExtra(FRHOUR    , dateRange.get(FRHOUR));
                intent.putExtra(FRMIN     , dateRange.get(FRMIN));
                intent.putExtra(TOHOUR    , dateRange.get(TOHOUR));
                intent.putExtra(TOMIN     , dateRange.get(TOMIN));
                intent.putExtra(MONTH     , dateRange.get(MONTH));
                intent.putExtra(DAY       , dateRange.get(DAY));
                intent.putExtra(YEAR      , dateRange.get(YEAR));
                startActivity(intent);
        	}
        });
        
        // Populate list of StudySpaces
        // Performs a default search using the current time
        reserveString = deserializeIntent(getIntent());

        currentRequest = intentToRequest(getIntent());
        refresh();
        
        //Set default filter option for rooms
        roomFilter = "";
    }

    private String deserializeIntent(Intent intent) {
        Calendar now = Calendar.getInstance();
        int from_hr = intent.getIntExtra(SearchActivity.FROM_HR,
                now.get(Calendar.HOUR_OF_DAY));
        int from_min = intent.getIntExtra(SearchActivity.FROM_MIN,
                now.get(Calendar.MINUTE));
        int end_hr = intent.getIntExtra(SearchActivity.END_HR, from_hr + 1);
        int end_min = intent.getIntExtra(SearchActivity.END_MIN, from_min);

        int month = intent.getIntExtra(SearchActivity.MONTH,
                now.get(Calendar.MONTH));
        int day = intent.getIntExtra(SearchActivity.DAY,
                now.get(Calendar.DAY_OF_MONTH));
        int year = intent.getIntExtra(SearchActivity.YEAR,
                now.get(Calendar.YEAR));

        dateRange.put(FRHOUR, from_hr);
        dateRange.put(FRMIN, from_min);
        dateRange.put(TOHOUR, end_hr);
        dateRange.put(TOMIN, end_min);
        dateRange.put(MONTH, month);
        dateRange.put(DAY, day);
        dateRange.put(YEAR, year);

        String date = String.format("date=%d-%d-%d", year, month, day);
        String fromTime = String.format("time_from=%02d%02d", from_hr, from_min);
        String toTime = String.format("time_to=%02d%02d", end_hr, end_min);

        roomFilter = intent.getStringExtra(SearchActivity.FILTER);

        return date+"&"+fromTime+"&"+toTime;
    }

    private ParamsRequest intentToRequest (Intent intent) {
        boolean priv   = intent.getBooleanExtra(SearchActivity.PRIVATE, false);
        boolean wboard = intent.getBooleanExtra(SearchActivity.WBOARD, false);
        boolean proj   = intent.getBooleanExtra(SearchActivity.PROJECTOR, false);
        boolean comp   = intent.getBooleanExtra(SearchActivity.COMPUTER, false);

        Calendar now = Calendar.getInstance();
        int quantity = intent.getIntExtra(SearchActivity.QUANTITY, 1);
        int from_hr  = intent.getIntExtra(SearchActivity.FROM_HR,
                now.get(Calendar.HOUR_OF_DAY));
        int from_min = intent.getIntExtra(SearchActivity.FROM_MIN,
                now.get(Calendar.MINUTE));
        int end_hr  = intent.getIntExtra(SearchActivity.END_HR, from_hr + 1);
        int end_min = intent.getIntExtra(SearchActivity.END_MIN, from_min);

        int day   = intent.getIntExtra(SearchActivity.DAY,
                now.get(Calendar.DAY_OF_MONTH));
        int month = intent.getIntExtra(SearchActivity.MONTH,
                now.get(Calendar.MONTH));
        int year  = intent.getIntExtra(SearchActivity.YEAR,
                now.get(Calendar.YEAR));

        roomFilter = intent.getStringExtra(SearchActivity.FILTER);
        
        ParamsRequest req = new ParamsRequest("json");
        req.setNumberOfPeople(quantity);
        req.setStartTime(from_hr, from_min);
        req.setEndTime(end_hr, end_min);
        req.setDate(year, month, day);
        req.setPrivate(priv);
        req.setWhiteboard(wboard);
        req.setProjector(proj);
        req.setComputer(comp);

        return req;
    }
    
    public void search(View view) {
    	Intent i = new Intent(this, SearchActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    	
    	startActivityForResult(i, MainActivity.ACTIVITY_OptionsActivity);
    }
    
    public void refreshButton (View v) {
    	roomFilter = "";
        refresh();
    }
    
    public void refresh() {
        Log.d(TAG, "API request created: " + currentRequest.toString());

        (new SendRequestTask(this)).execute(currentRequest);
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
        }

        return null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	if(resultCode == RESULT_CANCELED || intent.getExtras().isEmpty()) {
    		return;
    	}
    	
    	switch(requestCode) {
    		case ACTIVITY_OptionsActivity:
    			ParamsRequest req = intentToRequest(intent);
    			req.toString();
                refresh();
    			break;
    	}
    }
    
    public void fillDateRange(int fromHour, int fromMin, int toHour, int toMin, int month, int day, int year) {
        dateRange.put("fromHour", new Integer(fromHour));
        dateRange.put("fromMin", new Integer(fromMin));
        dateRange.put("toHour", new Integer(toHour));
        dateRange.put("toMin", new Integer(toMin));
        dateRange.put("month", new Integer(month));
        dateRange.put("day", new Integer(day));
        dateRange.put("year", new Integer(year));
    }
    
    public String generateReserveString() {
        String date = String.format("date=%d-%d-%d", dateRange.get("year"),dateRange.get("month"),dateRange.get("day"));
        String fromTime = String.format("time_from=%02d%02d", dateRange.get("fromHour"), dateRange.get("fromMin"));
        String toTime = String.format("time_to=%02d%02d", dateRange.get("toHour"), dateRange.get("toMin"));
        reserveString = date+"&"+fromTime+"&"+toTime;
        return reserveString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPrefs:
                startActivity(new Intent(this, PrefsActivity.class));
                break;
        }

        return true;
    }

    private void populateList(StudySpacesData data) {
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

        switch (sortOption) {
            case SORT_LOCATION:
                spacesList.setAdapter(
                        DataListAdapter.createLocationSortedAdapter(
                                this, data, latitude, longitude, roomFilter));
                break;
            case SORT_ALPHA:
                spacesList.setAdapter(
                        DataListAdapter.createAlphaSortedAdapater(this, data,
                                latitude, longitude, roomFilter));
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals("sort")) {
            if (ssData != null)
                populateList(ssData);
        }
    }

    // Performs a getJSON request in the background, so we don't block on the UI
    class SendRequestTask 
            extends AsyncTask<ApiRequest, Void, StudySpacesData> {
        Context ctx;
        ProgressDialog dialog;

        public SendRequestTask(Context ctx) {
            super();
            this.ctx = ctx;
        }

       @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ctx, "", "Refreshing...", true, true);
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
            dialog.dismiss();
            dialog = null;

            if (result == null) {
                showDialog(DIALOG_BAD_CONNECTION);
            }
            else {
                ssData = result;
                populateList(result);
            }

        }
    }
}
