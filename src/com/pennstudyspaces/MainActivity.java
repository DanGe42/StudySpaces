package com.pennstudyspaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends Activity {
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
}
