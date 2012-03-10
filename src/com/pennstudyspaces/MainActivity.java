package com.pennstudyspaces;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pennstudyspaces.api.StudySpacesApiRequest;
import com.pennstudyspaces.api.StudySpacesData;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

	public static final int ACTIVITY_OptionsActivity = 1;
    
    private ListView spacesList;
    
    private static final int ITEM_SELECT_DIALOG = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        spacesList = (ListView) findViewById(R.id.spaces_list);
        
        // Display the below TextView instead if the spaces list is empty
        spacesList.setEmptyView(findViewById(R.id.spaces_list_empty));
        
        // Listener that displays a dialog when a study space is clicked on
        spacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
        	@Override
        	public void onItemClick(AdapterView<?> parentView, View childView,
        			int position, long id) {
        		// Get the building name of the place that was just clicked on
        		Bundle bldginfo = new Bundle();
        		String buildingname = ((TextView) childView.findViewById(R.id.item_building_name)).getText().toString();
        		String amenities = ((TextView) childView.findViewById(R.id.item_amenities)).getText().toString();
        		bldginfo.putString("building", buildingname);
        		bldginfo.putString("amenities", amenities);
        		removeDialog(ITEM_SELECT_DIALOG);
        		showDialog(ITEM_SELECT_DIALOG, bldginfo);
        	}  
        }); 
        
        // Populate list of StudySpaces
        StudySpacesApiRequest req = new StudySpacesApiRequest("json", true);
        Log.d(TAG, "API request created: " + req.toString());
        (new SendRequestTask(this)).execute(req);
    }
    
    public void search(View view) {
    	Intent i = new Intent(this, OptionsActivity.class);
    	
    	startActivityForResult(i, MainActivity.ACTIVITY_OptionsActivity);
    }
    
    public void refresh (View v) {
        StudySpacesApiRequest req = new StudySpacesApiRequest("json", true);
        
        Log.d(TAG, "API request created: " + req.toString());

        (new SendRequestTask(this)).execute(req);
    }
    
    public void mapTest (View v) {
        startActivity(new Intent(this, MapTestActivity.class));
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
    			
    	        StudySpacesApiRequest req = new StudySpacesApiRequest("json", false);
    	        req.setNumberOfPeople(numPeople);
    	        req.setStartTime(fromTimeHour, fromTimeMin);
    	        req.setEndTime(toTimeHour, toTimeMin);
    	        req.setDate(year, month, day);
    	        req.setPrivate(priv);
    	        req.setWhiteboard(wboard);
    	        req.setProjector(projector);
    	        req.setComputer(computer);
    	        
    	        Log.d(TAG, "API request created: " + req.toString());
    	        (new SendRequestTask(this)).execute(req);
    	        
    			break;
    	}
    }
    
    protected Dialog onCreateDialog(int id, Bundle b) {
    	if (id == ITEM_SELECT_DIALOG) {
    		String building = b.getString("building");
    		final String amenities = b.getString("amenities");
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	if(building != null) builder.setMessage(building);
	    	builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int id) {
	    			// TODO implement sharing
	    			dialog.cancel();
	    		}
	    	})
	    	.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int id) {
	    			dialog.cancel();
	    		}
	    	});
	    	if(amenities.contains("R")) {
		    	builder.setNeutralButton("Reserve", new DialogInterface.OnClickListener() {
		    		public void onClick(DialogInterface dialog, int id) {
		    			// TODO we need to be able to pass in more info into these views
		    			// namely the room number and a more elegant way to see if it's reservable
		    			// this is some dummy hardcoded reservation for a GSR
		    			String url = "http://pennstudyspaces.com/deeplink?date=2012-02-25&time_from=2330&time_to=30&room=189";
		    			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		    			dialog.cancel();
		    			startActivity(browserIntent);
		    		}
		    	});
	    	}
    		return builder.create();
    	}
		return null;
    }
    
    // Performs a getJSON request in the background, so we don't block on the UI
    class SendRequestTask 
            extends AsyncTask<StudySpacesApiRequest, Void, StudySpacesData> {
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
        protected StudySpacesData doInBackground(StudySpacesApiRequest... req) {
            // we don't need to publish progress updates, unless we want to implement some kind of timeout
            // publishProgress();
            try {
                StudySpacesData data = StudySpacesData.sendRequest(req[0]);
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
            spacesList.setAdapter(DataListAdapter.createAdapter(ctx, result));
        }
    }
}
