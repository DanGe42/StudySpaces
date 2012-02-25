package com.pennstudyspaces;

import android.app.Activity;
import android.app.ProgressDialog;
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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        spacesList = (ListView) findViewById(R.id.spaces_list);
        
        // Display the below TextView instead if the spaces list is empty
        spacesList.setEmptyView(findViewById(R.id.spaces_list_empty));
    }
    
    //onClick methods for various buttons
    public void seeReservations(View view) {
    	
    }
    
    public void sortOptions(View view) {
    	Intent i = new Intent(this, OptionsActivity.class);
    	
    	startActivityForResult(i, MainActivity.ACTIVITY_OptionsActivity);
    }
    
    public void refresh (View v) {
        StudySpacesApiRequest req = new StudySpacesApiRequest("json", true);
        /*req.setNumberOfPeople(2);
        req.setDate(2012, 2, 28);
        req.setStartTime(15, 30);
        req.setEndTime(16, 30);*/
        Log.d(TAG, "API request created: " + req.toString());

        (new SendRequestTask(this)).execute(req);
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
