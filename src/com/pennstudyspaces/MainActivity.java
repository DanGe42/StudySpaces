package com.pennstudyspaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.pennstudyspaces.api.StudySpacesApiRequest;
import com.pennstudyspaces.api.StudySpacesData;

import java.io.IOException;

public class MainActivity extends Activity
{
    private static final String TAG = MainActivity.class.getSimpleName();

	public static final int ACTIVITY_OptionsActivity = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ListView spacesList = (ListView) findViewById(R.id.spaces_list);
        
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
    
    public void testJson (View v) {
        StudySpacesApiRequest req = new StudySpacesApiRequest("json");
        req.setNumberOfPeople(2);
        req.setDate(2012, 2, 28);
        req.setStartTime(15, 30);
        req.setEndTime(16, 30);
        Log.d(TAG, "API request created: " + req.toString());

        try {
            // TODO: This call is blocking. Wrap this with an AsyncTask
            StudySpacesData.sendRequest(req);
        } catch (IOException e) {
            Log.d(TAG, "Something went wrong", e);
        }
    }
}
