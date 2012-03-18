package com.pennstudyspaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pennstudyspaces.api.RoomKind;
import com.pennstudyspaces.api.StudySpacesData;


public class DataListAdapter extends SimpleAdapter {
    public static final String BUILDING    = "building",
                               ROOMKIND    = "roomkind",
                               NUM_ROOMS   = "num_rooms",
                               AMENITIES   = "amenities";
    //FIRST_ROOM  = "first",
    //OTHER_ROOMS = "other",
    
    static final String[] FROM =
            {BUILDING, ROOMKIND, NUM_ROOMS, AMENITIES};
    static final int[] TO = {R.id.item_building_name, R.id.item_room_kind,
                             R.id.item_num_rooms, R.id.item_amenities};

    private RoomKind[] roomKinds;
    
    private DataListAdapter (Context ctx, List<? extends Map<String, ?>> data,
                             RoomKind[] roomKinds) {
        super (ctx, data, R.layout.main_item, FROM, TO);
        
        this.roomKinds = roomKinds;
    }
    

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
    	View v = super.getView(position, convertView, parent);
    	
    	//LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//View rowView = inflater.inflate(R.layout.main_item, parent, false);
		TextView amenities = (TextView) v.findViewById(R.id.item_amenities);
		String amentext = amenities.getText().toString();
		
		ImageView proj = (ImageView) v.findViewById(R.id.item_proj);
		ImageView comp = (ImageView) v.findViewById(R.id.item_comp);
		ImageView board = (ImageView) v.findViewById(R.id.item_board);
		ImageView priv = (ImageView) v.findViewById(R.id.item_private);
		proj.setVisibility(amentext.contains("p") ? View.VISIBLE : View.GONE);
		comp.setVisibility(amentext.contains("c") ? View.VISIBLE : View.GONE);
		board.setVisibility(amentext.contains("w") ? View.VISIBLE : View.GONE);
		priv.setVisibility(amentext.contains("P") ? View.VISIBLE : View.GONE);
		//textView.setText();
		// Change the icon for Windows and iPhone
		
    	 
		return v;
    }
    
    public static DataListAdapter createAdapter (Context ctx,
                                                 StudySpacesData data) {
        List<Map<String, String>> entries = new ArrayList<Map<String, String>>();
        
        for (RoomKind roomKind : data.getRoomKinds()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(BUILDING, roomKind.getParentBuilding().getName());
            map.put(ROOMKIND, roomKind.getName());
            map.put(AMENITIES, processAmenities(roomKind));
            map.put(NUM_ROOMS, String.valueOf(roomKind.getRooms().size()));

            entries.add(map);
        }

        return new DataListAdapter (ctx, entries, data.getRoomKinds());
    }

    @Override
    public Object getItem (int position) {
        return this.roomKinds[position];
    }
    
    private static String processAmenities (RoomKind kind) {
        StringBuilder result = new StringBuilder(5);
        if (kind.getPrivacy() == RoomKind.Privacy.PRIVATE)
            result.append("P");
        if (kind.hasComputer())
            result.append("c");
        if (kind.hasProjector())
            result.append("p");
        if (kind.hasWhiteboard())
            result.append("w");
        if (kind.getReserveType() == RoomKind.Reserve.EXTERNAL)
            result.append("R");
        
        return result.toString();
    }
}
