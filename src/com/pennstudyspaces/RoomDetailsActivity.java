package com.pennstudyspaces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.pennstudyspaces.api.RoomKind;
import static com.pennstudyspaces.api.RoomKind.Privacy;
import static com.pennstudyspaces.api.RoomKind.Reserve;

import java.io.IOException;
import java.util.Properties;


public class RoomDetailsActivity extends MapActivity {
	private static final String TAG = RoomDetailsActivity.class.getSimpleName();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_details);
        
        initMap();
        initData();
    }
    
    private void initMap() {
        Properties properties = new Properties();
        try {
            properties.load(
                    getClass().getResourceAsStream("GoogleMaps.properties"));
            MapView mapView = new MapView(this, properties.getProperty("mapsApiKey"));
            mapView.setBuiltInZoomControls(true);
            mapView.setEnabled(true);
            mapView.setClickable(true);

            FrameLayout ll = (FrameLayout) findViewById(R.id.map_container);
            ll.addView(mapView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.FILL_PARENT,
                    FrameLayout.LayoutParams.FILL_PARENT));

        } catch (IOException e) {
            Log.e(TAG, "Make a GoogleMaps.properties file with this format:\n" +
                    "\tmapsApiKey=ABCDEFGHIJKL1234567890_", e);
            Toast.makeText(this, "Could not load properties file. Check logcat.",
                    Toast.LENGTH_LONG).show();
        }
    }
    
    private void initData() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String building  = extras.getString(MainActivity.BUILDING);
        double longitude = extras.getDouble(MainActivity.LONGITUDE);
        double latitude  = extras.getDouble(MainActivity.LATITUDE);
        
        boolean projector  = extras.getBoolean(MainActivity.PROJECTOR);
        boolean computer   = extras.getBoolean(MainActivity.COMPUTER);
        boolean whiteboard = extras.getBoolean(MainActivity.WHITEBOARD);
        String name        = extras.getString(MainActivity.NAME);
        int capacity       = extras.getInt(MainActivity.CAPACITY);

        Privacy privacy = (Privacy) extras.getSerializable(MainActivity.PRIVACY);
        Reserve reserve = (Reserve) extras.getSerializable(MainActivity.RESERVE);
    }

    private void setLocationOnMap(double latitude, double longitude) {

    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void share(View view) {
    	
    }
    
    public void back(View view) {
    	finish();
    }
    
    public void reserve(View view) {

    }
}