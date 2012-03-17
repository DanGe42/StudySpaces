package com.pennstudyspaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;

import com.pennstudyspaces.api.Building;
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
    
    // overriding this lets us pass information into the view
    // for now we're passing in the room's ability to be reserved
    /*@Override
    public View getView (int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setTag(1);
        return view;
    }*/
    
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
