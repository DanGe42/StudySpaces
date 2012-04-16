package edu.upenn.cis350;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TabDetails extends Fragment {

	private StudySpace o;
	private Preferences p;
	private View fav;
	private View unfav;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tabdetails, container, false);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Intent i = getActivity().getIntent();
		o = (StudySpace) i.getSerializableExtra("STUDYSPACE");
		p = (Preferences) i.getSerializableExtra("PREFERENCES");
		
		TextView tt = (TextView) getView().findViewById(R.id.spacename);
		tt.setText(o.getBuildingName());
		
		TextView rt = (TextView) getView().findViewById(R.id.roomtype);
		rt.setText(o.getSpaceName());
		
		TextView rn = (TextView) getView().findViewById(R.id.roomnumbers);
		/*Room[] rooms = o.getRooms();
		String room_string="";
		for (int j =0; j<rooms.length;j++){
				room_string += rooms[j].getRoomName()+" ";
		}*/
		rn.setText(o.getRoomNames());
		
		TextView mo = (TextView) getView().findViewById(R.id.maxoccupancy);
		mo.setText("Maximum occupancy: "+o.getMaximumOccupancy());
		
		TextView pi = (TextView) getView().findViewById(R.id.privacy);
		if(o.getPrivacy().equals("S"))
			pi.setText("This study space is a common Space");
		else pi.setText("Private space");
		
		TextView res = (TextView) getView().findViewById(R.id.reservetype);
		View calLayout = getView().findViewById(R.id.addCal);
		View resLayout = getView().findViewById(R.id.reserve);
		if(o.getReserveType().equals("N")){
			res.setText("This study space is non-reservable.");
			calLayout.setVisibility(View.VISIBLE);
			resLayout.setVisibility(View.GONE);
		}else{ 
			res.setText("This study space can be reserved.");
			calLayout.setVisibility(View.GONE);
			resLayout.setVisibility(View.VISIBLE);
		}
		TextView wb = (TextView) getView().findViewById(R.id.whiteboard);
		if(o.hasWhiteboard())
			wb.setText("This study space has a whiteboard.");
		else wb.setText("This study space does not have a whiteboard.");
		
		TextView com = (TextView) getView().findViewById(R.id.computer);
		if(o.hasComputer())
			com.setText("This study space has a computer.");
		else com.setText("This study space does not have computers.");
		
		TextView proj = (TextView) getView().findViewById(R.id.projector);
		if(o.has_big_screen())
			proj.setText("This study space has a big screen.");
		else proj.setText("This study space does not have a big screen.");
		
		ImageView image = (ImageView) getView().findViewById(R.id.imageID);
       if(image!=null){
    	   Resources resource = getResources();
    	   if(SpaceInfo.getPicture(o).length()!=0){
    		   int resID = resource.getIdentifier(SpaceInfo.getPicture(o), "drawable", getActivity().getPackageName() );
    		   image.setImageResource(resID);
    	   }
       }
       
       TextView descr = (TextView) getView().findViewById(R.id.details);
       if(descr!=null){
    	   descr.setText(SpaceInfo.getDescription(o));
       }
       fav = getView().findViewById(R.id.favorite);
   		unfav = getView().findViewById(R.id.unfavorite);
       //favorites
		if(p.isFavorite(o.getBuildingName(), o.getSpaceName())){
			unfav.setVisibility(View.VISIBLE);
			fav.setVisibility(View.GONE);
		}else{ 
			unfav.setVisibility(View.GONE);
			fav.setVisibility(View.VISIBLE);
		}
	}
	
	public void onFavClick(View v){
			unfav.setVisibility(View.VISIBLE);
			fav.setVisibility(View.GONE);
	}
	public void onRemoveFavClick(View v){
			fav.setVisibility(View.VISIBLE);
			unfav.setVisibility(View.GONE);
	}
	
	public Intent getReserveIntent(View v){
		Intent k = null;
		if(o.getBuildingType().equals(StudySpace.WHARTON)){
			k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://spike.wharton.upenn.edu/Calendar/gsr.cfm?"));}
		else if(o.getBuildingType().equals(StudySpace.ENGINEERING)){
			k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weblogin.pennkey.upenn.edu/login/?factors=UPENN.EDU&cosign-seas-www_userpages-1&https://www.seas.upenn.edu/about-seas/room-reservation/form.php"));
		}else if(o.getBuildingType().equals(StudySpace.LIBRARIES)){
			k = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weblogin.library.upenn.edu/cgi-bin/login?authz=grabit&app=http://bookit.library.upenn.edu/cgi-bin/rooms/rooms"));
		}
		return k;
	}
	
	public Intent getCalIntent(View v){
		Calendar cal = Calendar.getInstance();              
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", cal.getTimeInMillis());
		//intent.putExtra("allDay", true);
		//intent.putExtra("rrule", "FREQ=YEARLY");
		intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
		intent.putExtra("title", "PennStudySpaces Reservation confirmed. Details - "+o.getBuildingName()+" - "+o.getRooms()[0].getRoomName()+"\nTime: ");
		return intent;
	}
	public Intent getTextIntent(View v){
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		try {
			
			// Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			 //For now
		     sendIntent.putExtra("sms_body", "PennStudySpaces Reservation confirmed. Details - "+o.getBuildingName()+" - "+o.getRooms()[0].getRoomName()+"\nTime: ");
		     sendIntent.setType("vnd.android-dir/mms-sms");
		} catch (Exception e) {
			Toast.makeText(v.getContext(),
					"SMS faild, please try again later!",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		return sendIntent;
	}
	
	public void onReserveClick(View v){
		Intent k = getReserveIntent(v);
		
		Intent calIntent = getCalIntent(v);
		
		Intent sendIntent = getTextIntent(v);
			
			     CheckBox text = (CheckBox) getView().findViewById(R.id.resTextCheckBox);
					if(text!=null && text.isChecked())
								startActivity(sendIntent);
				CheckBox calBox = (CheckBox) getView().findViewById(R.id.calCheckBox);
					if(calBox !=null && calBox.isChecked())
										startActivity(calIntent);
			     if(k!=null)
						startActivity(k);
			     
	}
	public void onCalClick(View v){
		
		Intent calIntent = getCalIntent(v);
		
		Intent sendIntent = getTextIntent(v);
		
		CheckBox text = (CheckBox) getView().findViewById(R.id.calTextCheckBox);
		if(text!=null && text.isChecked())
					startActivity(sendIntent);
			     startActivity(calIntent);
			     
	}
}
