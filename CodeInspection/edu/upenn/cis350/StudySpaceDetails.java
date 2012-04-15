package edu.upenn.cis350;

import java.io.Serializable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

public class StudySpaceDetails extends FragmentActivity {

	private TabDetails tabdetails;
	private TabMap tabmap;
	private StudySpace o;
	private Preferences p;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ssdetails);
		Intent i = getIntent();
		o = (StudySpace) i.getSerializableExtra("STUDYSPACE");
		p = (Preferences) i.getSerializableExtra("PREFERENCES");
		tabmap = new TabMap();
		tabdetails = new TabDetails();

		// Saves the first state of the code
		ImageView image = (ImageView) findViewById(R.id.button_details);
		image.setImageResource(R.color.lightblue);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragment_container, tabdetails);
		transaction.commit();
	}

	public void onShareClick(View v) {

	}

	public void onDetailsClick(View v) {
		ImageView image = (ImageView) v.findViewById(R.id.button_details);
		image.setImageResource(R.color.lightblue);
		image = (ImageView) findViewById(R.id.button_map);
		image.setImageResource(R.color.darkgrey);

		// Create new fragment and transaction
		// Fragment newFragment = new TabDetails();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragment_container, tabdetails);
		// transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	public void onMapClick(View v) {
		/*
		 * ImageView image = (ImageView) findViewById(R.id.button_details);
		 * image.setImageResource(R.color.darkgrey); image = (ImageView)
		 * v.findViewById(R.id.button_map);
		 * image.setImageResource(R.color.lightblue);
		 * 
		 * FragmentTransaction transaction = getSupportFragmentManager()
		 * .beginTransaction(); transaction.replace(R.id.fragment_container,
		 * tabmap); transaction.commit();
		 */
		Intent i = new Intent(this, CustomMap.class);
		i.putExtra("STUDYSPACE", o);
		startActivity(i);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

	    if (keyCode == KeyEvent.KEYCODE_BACK) {

	    	Intent i = new Intent();
	    	
        	i.putExtra("PREFERENCES", (Serializable)p);
        	setResult(RESULT_OK, i);
        	//ends this activity
        	finish();
        }
	    return super.onKeyDown(keyCode, event);
	}
	    
	public void onFavClick(View v){
		p.addFavorites(o.getBuildingName(), o.getSpaceName());
		tabdetails.onFavClick(v);
	}
	public void onRemoveFavClick(View v){
		p.removeFavorites(o.getBuildingName(), o.getSpaceName());
		tabdetails.onRemoveFavClick(v);
	}
	public void onCalClick(View v) {
		tabdetails.onCalClick(v);

	}

	public void onReserveClick(View v){
		tabdetails.onReserveClick(v);
	}

}
