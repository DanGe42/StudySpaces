package com.pennstudyspaces;

import android.content.Context;
import android.widget.SimpleAdapter;
import com.pennstudyspaces.api.Building;
import com.pennstudyspaces.api.Room;
import com.pennstudyspaces.api.RoomKind;
import com.pennstudyspaces.api.StudySpacesData;
import static com.pennstudyspaces.api.StudySpacesData.BuildingRoomPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 2/24/12
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
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

    private DataListAdapter (Context ctx, List<? extends Map<String, ?>> data) {
        super (ctx, data, R.layout.main_item, FROM, TO);
    }

    public static DataListAdapter createAdapter (Context ctx,
                                                 StudySpacesData data) {
        List<Map<String, String>> entries = new ArrayList<Map<String, String>>();
        
        for (Building building : data.getBuildings()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(BUILDING, building.getName());
            
            Map<String, RoomKind> roomKindMap = building.getRoomKinds();
            for (String rkEntry : roomKindMap.keySet()) {
                map.put(ROOMKIND, rkEntry);
                RoomKind rk = roomKindMap.get(rkEntry);
                map.put(AMENITIES, processAmenities(rk));
                map.put(NUM_ROOMS, String.valueOf(rk.getRooms().size()));
            }

            entries.add(map);
        }
        
        return new DataListAdapter (ctx, entries);
    }
    
    private static String processAmenities (RoomKind kind) {
        StringBuilder result = new StringBuilder(4);
        if (kind.getPrivacy() == RoomKind.Privacy.PRIVATE)
            result.append("P");
        if (kind.hasComputer())
            result.append("c");
        if (kind.hasProjector())
            result.append("p");
        if (kind.hasWhiteboard())
            result.append("w");
        
        return result.toString();
    }
}
