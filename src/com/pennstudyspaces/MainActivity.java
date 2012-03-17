package com.pennstudyspaces;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pennstudyspaces.api.ApiRequest;
import com.pennstudyspaces.api.RoomKind;
import com.pennstudyspaces.api.StudySpacesData;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private StudySpacesApplication app;
    private ListView spacesList;
    
    public static final int ACTIVITY_OptionsActivity = 1;
    
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
                               RESERVE    = "reserve";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        app = (StudySpacesApplication) getApplication();
        spacesList = (ListView) findViewById(R.id.spaces_list);
        
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
                
                startActivity(intent);
        	}
        });
        
        // Populate list of StudySpaces
        refresh();
    }
    
    public void search(View view) {
    	Intent i = new Intent(this, SearchActivity.class);
    	
    	startActivityForResult(i, MainActivity.ACTIVITY_OptionsActivity);
    }
    
    public void refreshButton (View v) {
        refresh();
    }
    
    public void refresh() {
        ApiRequest req = new ApiRequest("json", true);

        Log.d(TAG, "API request created: " + req.toString());

        (new SendRequestTask(this)).execute();
    }
    
    //Test button for opening a mapView
    public void roomDetails() {
        startActivity(new Intent(this, RoomDetailsActivity.class));
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
    			
    			int numPeople = intArray[0];
    			int fromTimeHour = intArray[1];
    			int fromTimeMin = intArray[2];
    			int toTimeHour = intArray[3];
    			int toTimeMin = intArray[4];
    			int month = intArray[5];
    			int day = intArray[6];
    			int year = intArray[7];
    			
    			boolean priv = boolArray[0];
    			boolean wboard = boolArray[1];
    			boolean projector = boolArray[2];
    			boolean computer = boolArray[3];
    			
    	        ApiRequest req = new ApiRequest("json", false);
    	        req.setNumberOfPeople(numPeople);
    	        req.setStartTime(fromTimeHour, fromTimeMin);
    	        req.setEndTime(toTimeHour, toTimeMin);
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
            spacesList.setAdapter(DataListAdapter.createAdapter(ctx, result));
        }
    }
}
