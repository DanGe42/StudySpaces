package com.pennstudyspaces;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.pennstudyspaces.api.RoomKind.Privacy;
import com.pennstudyspaces.api.RoomKind.Reserve;


public class RoomDetailsActivity extends MapActivity {
	private static final String TAG = RoomDetailsActivity.class.getSimpleName();
	private MapView mapView;
	private MapController mapController;
    
    private String reserveLink;
	
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
            
            mapView = new MapView(this, properties.getProperty("mapsApiKey"));
            mapView.setBuiltInZoomControls(true);
            mapView.setEnabled(true);
            mapView.setClickable(true);

            mapController = mapView.getController();
            
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
        int roomId         = extras.getInt(MainActivity.ROOMNUM);

        boolean privacy =
                ((Privacy) extras.getSerializable(MainActivity.PRIVACY) ==
                        Privacy.PRIVATE);
        boolean reserve =
                ((Reserve) extras.getSerializable(MainActivity.RESERVE) ==
                        Reserve.EXTERNAL);

        reserveLink = "http://pennstudyspaces.com/deeplink?";
        int from_hr = intent.getIntExtra(MainActivity.FRHOUR, 0);
        int from_min = intent.getIntExtra(MainActivity.FRMIN, 0);
        int end_hr = intent.getIntExtra(MainActivity.TOHOUR, 0);
        int end_min = intent.getIntExtra(MainActivity.TOMIN, 0);
        int month = intent.getIntExtra(MainActivity.MONTH, 0);
        int day = intent.getIntExtra(MainActivity.DAY, 0);
        int year = intent.getIntExtra(MainActivity.YEAR, 0);


        String date = String.format("date=%d-%d-%d", year, month, day);
        String fromTime = String.format("time_from=%02d%02d", from_hr, from_min);
        String toTime = String.format("time_to=%02d%02d", end_hr, end_min);
        this.reserveLink = "http://pennstudyspaces.com/deeplink?" + date + "&" +
                fromTime + "&" + toTime + "&room=" + roomId;

        TextView titleText     = (TextView) findViewById(R.id.roomTitle);
        titleText.setText(roomName);

        TextView buildingText  = (TextView) findViewById(R.id.text_building);
        buildingText.setText(building);

        TextView capacityText  = (TextView) findViewById(R.id.text_occupancy);
        capacityText.setText(String.valueOf(capacity));
        
        TextView privacyText = (TextView) findViewById(R.id.text_privacy);
        privacyText.setText(boolToString(privacy));
        
        ImageView projectorText = (ImageView) findViewById(R.id.text_projector);
        projectorText.setVisibility(projector ? View.VISIBLE : View.GONE);

        ImageView computerText  = (ImageView) findViewById(R.id.text_computer);
        computerText.setVisibility(computer ? View.VISIBLE : View.GONE);

        ImageView wbText        = (ImageView) findViewById(R.id.text_whiteboard);
        wbText.setVisibility(whiteboard ? View.VISIBLE : View.GONE);


        Button reserveButton = (Button) findViewById(R.id.reserveButton);
        reserveButton.setEnabled(reserve);
        reserveButton.setVisibility(reserve ? View.VISIBLE : View.GONE);

        setLocationOnMap(latitude, longitude,roomName,building);
    }

    /* Put a specified location on the Google Map */
    private void setLocationOnMap(double latitude, double longitude, 
    		String room, String building) {
        
    	//Room location
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.maps_marker);
        MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(drawable, this);
        
        GeoPoint point = new GeoPoint((int)(latitude * 1e6), (int)(longitude * 1e6));
        OverlayItem overlayItem = new OverlayItem(point, room, building);
        itemizedOverlay.addOverlay(overlayItem);
        mapOverlays.add(itemizedOverlay);
        
        //Set center for view
        mapController.setCenter(point);
        mapController.setZoom(16);
        
        //User Location
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null) {
        	location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else {
		latitude = location.getLatitude();
	        longitude = location.getLongitude();

	        drawable = this.getResources().getDrawable(R.drawable.maps_marker_blue);
	        itemizedOverlay = new MyItemizedOverlay(drawable, this);
	        
	        GeoPoint currentPoint = new GeoPoint((int)(latitude * 1e6), (int)(longitude * 1e6));
	        overlayItem = new OverlayItem(currentPoint, "Me", "My current location");
	        
	        itemizedOverlay.addOverlay(overlayItem);
	        mapOverlays.add(itemizedOverlay);
        }
    }
    
    private String boolToString(boolean b) {
        return b ? "yes" : "no";
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void share(View view) {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        long timeInMillisSinceEpoch = 0;
        int roomId = extras.getInt(MainActivity.ROOMNUM);
        int fromhour = extras.getInt(MainActivity.FRHOUR);
        int frommin = extras.getInt(MainActivity.FRMIN);
        int tohour = extras.getInt(MainActivity.TOHOUR);
        int tomin = extras.getInt(MainActivity.TOMIN);
        int month = extras.getInt(MainActivity.MONTH);
        int day = extras.getInt(MainActivity.DAY);
        int year = extras.getInt(MainActivity.YEAR);
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(year+"-"+month+"-"+day);
            timeInMillisSinceEpoch = date.getTime(); 
            //Log.e("SHARE", year+"-"+month+"-"+day);
            //Log.e("SHARE", "the generated time: "+timeInMillisSinceEpoch);
        } catch (ParseException e) { e.printStackTrace();}
        // request http://www.pennstudyspaces.com/shareevent?roomid=46&shr=8&smin=30&ehr=9&emin=30&date=1334116800000
        String url = "http://www.pennstudyspaces.com/shareevent?" +
                    "roomid=" + roomId +
                    "&shr="   + fromhour +
                    "&smin="  + frommin +
                    "&ehr="   + tohour +
                    "&emin="  + tomin +
                    "&date="  + timeInMillisSinceEpoch;
        Intent browserIntent =
                new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        startActivity(browserIntent);
        // int starthour, startmin
        // int endhour, endmin
        // date
    }
    
    public void back(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
    
    public void reserve(View view) {
        Intent browserIntent =
                new Intent(Intent.ACTION_VIEW, Uri.parse(reserveLink));

        startActivity(browserIntent);
    }
    public void amenityTooltip(View view) {
    	Context context = getApplicationContext();
    	CharSequence text = "";
    	switch (view.getId()){
	    	case R.id.text_computer:
	    		text = "This room has a computer.";
	        	break;
	    	case R.id.text_projector:
	    		text = "This room has a projector.";
	        	break;
	    	case R.id.text_whiteboard:
	    		text = "This room has a whiteboard or a chalkboard.";
	        	break;
    	}
    	
    	Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
    	toast.show();
    }
}