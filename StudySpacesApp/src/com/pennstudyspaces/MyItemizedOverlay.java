package com.pennstudyspaces;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public MyItemizedOverlay(Drawable icon) {
		super(boundCenterBottom(icon));
	}

	public MyItemizedOverlay(Drawable icon, Context context) {
		super(boundCenterBottom(icon));
		mContext = context;
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
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		
		dialog.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.cancel();
    		}
		});
		
		dialog.show();
		return true;
	}
}
