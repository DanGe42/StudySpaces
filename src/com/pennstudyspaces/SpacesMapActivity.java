package com.pennstudyspaces;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 3/9/12
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpacesMapActivity extends MapActivity {
	
	MapView mapView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spaces_map);
        
        mapView = (MapView) findViewById(R.id.mapview);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void testButton(View view) {
    	GeoPoint point = new GeoPoint(19240000,-99120000);
    	OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
    		
    	
    }
}