package edu.upenn.cis350;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CustomMap extends MapActivity {

	LinearLayout linearLayout;
	MapView mapView;
	MapController mc;
	GeoPoint p;
	GeoPoint q;
	GeoPoint avg;
	List<Overlay> mapOverlays;
	Drawable drawable;
	PinOverlay pins;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = super.getIntent();
		StudySpace o = (StudySpace) i.getSerializableExtra("STUDYSPACE");

		setContentView(R.layout.mapview);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		drawable = this.getResources().getDrawable(R.drawable.pushpin);
		pins = new PinOverlay(drawable);

		mc = mapView.getController();

		double longitude = o.getSpaceLongitude();
		double latitude = o.getSpaceLatitude();
		double avgLong = longitude;
		double avgLat = latitude;

		p = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));

		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria _criteria = new Criteria();
		// _criteria.setAccuracy(Criteria.ACCURACY_LOW);
		PendingIntent _pIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, getIntent(), 0);
		locationManager.requestSingleUpdate(_criteria, _pIntent);

		String _bestProvider = locationManager.getBestProvider(_criteria, true);
		Location location = locationManager.getLastKnownLocation(_bestProvider);

		LocationListener loc_listener = new LocationListener() {
			public void onLocationChanged(Location l) {
			}

			public void onProviderEnabled(String p) {
			}

			public void onProviderDisabled(String p) {
			}

			public void onStatusChanged(String p, int status, Bundle extras) {
			}
		};
		locationManager.requestLocationUpdates(_bestProvider, 0, 0,
				loc_listener);
		location = locationManager.getLastKnownLocation(_bestProvider);

		if (location != null) {
			double gpsLat = location.getLatitude();
			double gpsLong = location.getLongitude();
			
			avgLat += gpsLat;
			avgLat /= 2.0;
			avgLong += gpsLong;
			avgLong /= 2.0;

			q = new GeoPoint((int) (gpsLat * 1E6), (int) (gpsLong * 1E6));

			OverlayItem overlayitem = new OverlayItem(q, "", "");
			pins.addOverlay(overlayitem);
		}

		/*
		 * MapOverlay mapOverlay = new MapOverlay(); List<Overlay>
		 * listOfOverlays = mapView.getOverlays(); listOfOverlays.clear();
		 * listOfOverlays.add(mapOverlay);
		 */

		OverlayItem overlayitem = new OverlayItem(p, "", "");

		pins.addOverlay(overlayitem);

		mapOverlays = mapView.getOverlays();
		mapOverlays.add(pins);
		
		avg = new GeoPoint((int) (avgLat * 1E6), (int) (avgLong * 1E6));
		
		mc.animateTo(avg);
		mc.setZoom(17);
	}

	/*
	 * class MapOverlay extends Overlay {
	 * 
	 * @Override public boolean draw(Canvas canvas, MapView mapView, boolean
	 * shadow, long when) { super.draw(canvas, mapView, shadow);
	 * 
	 * //---translate the GeoPoint to screen pixels--- Point screenPts = new
	 * Point(); mapView.getProjection().toPixels(p, screenPts);
	 * 
	 * //---add the marker--- Bitmap bmp = BitmapFactory.decodeResource(
	 * getResources(), R.drawable.pushpin); //Positions the image
	 * canvas.drawBitmap(bmp, screenPts.x-10, screenPts.y-34, null); return
	 * true; } }
	 */

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	public class PinOverlay extends ItemizedOverlay<OverlayItem>{

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		private Drawable marker;
		
		public PinOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
			marker = defaultMarker;
		}
		
		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}
		
		@Override
		protected OverlayItem createItem(int i) {
		  return mOverlays.get(i);
		}


		@Override
		public int size() {
			return mOverlays.size();
		}

		@Override
		protected boolean onTap(int index) {
		    //called when an item is tapped
			
		    return true;
		}

		@Override
		public boolean onTap (final GeoPoint p, final MapView mapV) {
			
			boolean tapped = super.onTap(p, mapView);
		    if (tapped){            
		        //do what you want to do when you hit an item    
		    	//GeoPoint p = mOverlays.get(index).getPoint();

			    //boolean tapped = super.onTap(p, mapV);
			   /* if(!tapped){            
			        //you can use this to check for other taps on the custom elements you are drawing
			    	LayoutInflater inflater = (LayoutInflater)mapV.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    	View popUp = inflater.inflate(R.layout.map_popup, mapV, false);
			    	MapView.LayoutParams mapParams = new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
		                    ViewGroup.LayoutParams.WRAP_CONTENT, p, 0, 0, MapView.LayoutParams.BOTTOM_CENTER);
			    	mapV.addView(popUp, mapParams);
			    	mapV.invalidate();
			    }*/
			    
			    Geocoder geoCoder = new Geocoder(
		                getBaseContext(), Locale.getDefault());
		            try {
		                List<Address> addresses = geoCoder.getFromLocation(
		                    p.getLatitudeE6()  / 1E6, 
		                    p.getLongitudeE6() / 1E6, 1);

		                String add = "";
		                if (addresses.size() > 0) 
		                {
		                    for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
		                         i++)
		                       add += addresses.get(0).getAddressLine(i) + "\n";
		                }

		                Toast.makeText(getBaseContext(), add, Toast.LENGTH_SHORT).show();
		            }
		            catch (IOException e) {                
		                e.printStackTrace();
		            }   
		    }   
			return true;
		}
		
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow){
		    /*if(!shadow) 
		    // if you have a custom image you may not want the shadow to be drawn
		        super.draw(canvas,mapV,shadow);
		    if(selected != null) { 
		    // selected just means that something was clicked
		    // it isn't defined in this example
		    Projection projection = mapV.getProjection();
		    Point drawPoint = projection.toPixels(selected.getPoint(), null);
		        //get coordinates so you can do your drawing code afterward
		    }
		    */
			 super.draw(canvas, mapView, shadow);                   
			 
	         //---translate the GeoPoint to screen pixels---
	         Point screenPts = new Point();
	         for(int i = 0; i<mOverlays.size(); i++){
	        	 mapView.getProjection().toPixels(mOverlays.get(i).getPoint(), screenPts);

	         //---add the marker---
	        	 Bitmap bmp = ((BitmapDrawable)marker).getBitmap();
	         //Positions the image
	         canvas.drawBitmap(bmp, screenPts.x-10, screenPts.y-34, null);  
	         }
		}
	}


}
