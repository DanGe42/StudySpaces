package com.pennstudyspaces;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
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
        String roomName    = extras.getString(MainActivity.NAME);
        int capacity       = extras.getInt(MainActivity.CAPACITY);

        boolean privacy =
                ((Privacy) extras.getSerializable(MainActivity.PRIVACY) ==
                        Privacy.PRIVATE);
        boolean reserve =
                ((Reserve) extras.getSerializable(MainActivity.RESERVE) ==
                        Reserve.EXTERNAL);


        TextView titleText     = (TextView) findViewById(R.id.roomTitle);
        titleText.setText(roomName);

        TextView buildingText  = (TextView) findViewById(R.id.text_building);
        buildingText.setText(building);

        TextView capacityText  = (TextView) findViewById(R.id.text_occupancy);
        capacityText.setText(String.valueOf(capacity));

        TextView projectorText = (TextView) findViewById(R.id.text_projector);
        projectorText.setText(boolToString(projector));

        TextView computerText  = (TextView) findViewById(R.id.text_computer);
        computerText.setText(boolToString(computer));

        TextView wbText        = (TextView) findViewById(R.id.text_whiteboard);
        wbText.setText(boolToString(whiteboard));

        TextView privacyText   = (TextView) findViewById(R.id.text_privacy);
        privacyText.setText(boolToString(privacy));


        Button reserveButton = (Button) findViewById(R.id.reserveButton);
        reserveButton.setEnabled(reserve);

        setLocationOnMap(latitude, longitude);
    }

    private void setLocationOnMap(double latitude, double longitude) {

    }
    
    private String boolToString(boolean b) {
        return b ? "yes" : "no";
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void share(View view) {

    }
    
    public void back(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
              .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
    
    public void reserve(View view) {
        String url = "http://pennstudyspaces.com/deeplink?date=2012-02-25&time_from=2330&time_to=30&room=189";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        startActivity(browserIntent);
    }
}