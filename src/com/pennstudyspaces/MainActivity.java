package com.pennstudyspaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.pennstudyspaces.api.StudySpacesApiRequest;
import com.pennstudyspaces.api.StudySpacesData;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

	public static final int ACTIVITY_OptionsActivity = 1;
    
    private ListView spacesList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        spacesList = (ListView) findViewById(R.id.spaces_list);
        
        // Display the below TextView instead if the spaces list is empty
        spacesList.setEmptyView(findViewById(R.id.spaces_list_empty));
        
        //Populate list of StudySpaces
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

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	if(resultCode == RESULT_CANCELED) {
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
    
    // Performs a getJSON request in the background, so we don't block on the UI
    class SendRequestTask 
            extends AsyncTask<StudySpacesApiRequest, Void, StudySpacesData> {
        
        Context ctx;
        public SendRequestTask(Context ctx) {
            this.ctx = ctx;
        }

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
            spacesList.setAdapter(DataListAdapter.createAdapter(ctx, result));
        }
    }
}
