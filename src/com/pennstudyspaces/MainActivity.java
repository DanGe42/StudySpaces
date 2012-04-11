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


import com.pennstudyspaces.api.ParamsRequest;
import com.pennstudyspaces.api.RoomKind;
import com.pennstudyspaces.api.StudySpacesData;

import static android.content.SharedPreferences.*;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private StudySpacesApplication app;
    private ListView spacesList;
    private String reserveString;
    private HashMap<String, Integer> dateRange;
    private LocationManager locManager;
    
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
    						   RESLINK 	  = "reservelink",
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

                intent.putExtra(RESLINK   , reserveString);
                intent.putExtra(ROOMNUM   , kind.getRooms().get(0).getId());
                intent.putExtra(FRHOUR    , dateRange.get("fromHour"));
                intent.putExtra(FRMIN     , dateRange.get("fromMin"));
                intent.putExtra(TOHOUR    , dateRange.get("toHour"));
                intent.putExtra(TOMIN     , dateRange.get("toMin"));
                intent.putExtra(MONTH     , dateRange.get("month"));
                intent.putExtra(DAY       , dateRange.get("day"));
                intent.putExtra(YEAR      , dateRange.get("year"));
                startActivity(intent);
        	}
        });
        
        // Populate list of StudySpaces
        // Performs a default search using the current time
        Calendar now = Calendar.getInstance();
        
        int hour = now.get(Calendar.HOUR_OF_DAY)+1;
        int month = now.get(Calendar.MONTH)+1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int year = now.get(Calendar.YEAR);
        fillDateRange(hour, 0, (hour+1)%24, 0, month, day, year);

        reserveString = generateReserveString();
        
        /*String date = String.format("date=%d-%d-%d", year,month,day);
		String fromTime = String.format("time_from=%02d%02d", (hour+1)%24, 0);
		String toTime = String.format("time_to=%02d%02d", (hour+2)%24, 0);
		reserveString = date+"&"+fromTime+"&"+toTime;*/
		
        ParamsRequest req = new ParamsRequest("json");
        req.setNumberOfPeople(1);
        req.setStartTime((hour + 1) % 23, 0);
        req.setEndTime((hour + 2) % 23, 0);
        req.setDate(year, month, day);
        req.setPrivate(false);
        req.setWhiteboard(false);
        req.setProjector(false);
        req.setComputer(false);
        
        app.setData(new StudySpacesData(req));
        refresh();
        
        //Set default filter option for rooms
        roomFilter = "";
    }
    
    public void search(View view) {
    	Intent i = new Intent(this, SearchActivity.class);
    	
    	startActivityForResult(i, MainActivity.ACTIVITY_OptionsActivity);
    }
    
    public void refreshButton (View v) {
    	roomFilter = "";
        refresh();
    }
    
    public void refresh() {
        Log.d(TAG, "API request created: " + app.getData().getApiRequest().toString());

        (new SendRequestTask(this)).execute();
    }
    
    public void roomDetails() {
        startActivity(new Intent(this, RoomDetailsActivity.class));
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
    			int[] intArray = (int[])intent.getExtras().get("INT_ARRAY");
    			boolean[] boolArray = (boolean[])intent.getExtras().get("BOOL_ARRAY");
    			roomFilter = (String)intent.getExtras().get("BUILDING NAME");
    			
    			int numPeople = intArray[0];
    			
    			int fromHour = intArray[1];
    			int fromMin = intArray[2];
    			int toHour = intArray[3];
    			int toMin = intArray[4];
    			
    			int month = intArray[5];
    			int day = intArray[6];
    			int year = intArray[7];
    			
    			fillDateRange(fromHour, fromMin, toHour, toMin, month+1, day, year);
    			reserveString = generateReserveString();
    			
    			boolean priv = boolArray[0];
    			boolean wboard = boolArray[1];
    			boolean projector = boolArray[2];
    			boolean computer = boolArray[3];
    			
    	        ParamsRequest req = new ParamsRequest("json");
    	        req.setNumberOfPeople(numPeople);
    	        
    	        req.setStartTime(fromHour, fromMin);
    	        req.setEndTime(toHour, toMin);
    	        
    	        req.setDate(year, month, day);
    	        
    	        req.setPrivate(priv);
    	        req.setWhiteboard(wboard);
    	        req.setProjector(projector);
    	        req.setComputer(computer);
    	        
                app.setData(new StudySpacesData(req));
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

        switch (sortOption) {
            case SORT_LOCATION:
                Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location == null) {
                    location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if(location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    spacesList.setAdapter(
                            DataListAdapter.createLocationSortedAdapter(
                                    this, data, latitude, longitude, roomFilter));
                }
                else {
                    // Set yourself at Huntsman (if you location isn't working)
                    spacesList.setAdapter(
                            DataListAdapter.createLocationSortedAdapter(
                                    this, data, 39.953278, -75.19846, roomFilter));

                    // default (when no location)
                    //spacesList.setAdapter(DataListAdapter.createDefaultAdapter(ctx, result));
                }
                break;
            case SORT_ALPHA:
                spacesList.setAdapter(
                        DataListAdapter.createAlphaSortedAdapater(this, data));
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals("sort")) {
            populateList(app.getData());
        }
    }

    // Performs a getJSON request in the background, so we don't block on the UI
    class SendRequestTask 
            extends AsyncTask<Void, Void, StudySpacesData> {
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
        protected StudySpacesData doInBackground(Void... req) {
            // we don't need to publish progress updates, unless we want to
            // implement some kind of timeout publishProgress();
            try {
                app.updateData();
                return app.getData();
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
                populateList(result);
            }

        }
    }
}
