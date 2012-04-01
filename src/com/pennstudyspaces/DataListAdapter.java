package com.pennstudyspaces;

import java.util.*;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pennstudyspaces.api.Building;
import com.pennstudyspaces.api.RoomKind;
import com.pennstudyspaces.api.StudySpacesData;


public class DataListAdapter extends SimpleAdapter {
    public static final String BUILDING    = "building",
                               ROOMKIND    = "roomkind",
                               NUM_ROOMS   = "num_rooms",
                               DISTANCE    = "dist",
                               AMENITIES   = "amenities";
    //FIRST_ROOM  = "first",
    //OTHER_ROOMS = "other",
    
    static final String[] FROM =
            {BUILDING, ROOMKIND, NUM_ROOMS, AMENITIES, DISTANCE};
    static final int[] TO = {R.id.item_building_name, R.id.item_room_kind,
                             R.id.item_num_rooms, R.id.item_amenities, R.id.item_dist};

    private RoomKind[] roomKinds;

    private static Comparator<RoomKind> alphaSort = new AlphaComparator();
    
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
		
		setAmenityIcon(R.id.item_proj, "p", amentext, v);
		setAmenityIcon(R.id.item_comp, "c", amentext, v);
		setAmenityIcon(R.id.item_board, "w", amentext, v);
		setAmenityIcon(R.id.item_private, "P", amentext, v);
		setAmenityIcon(R.id.item_reservable, "R", amentext, v);
		//ImageView proj   = (ImageView) v.findViewById(R.id.item_proj);
		//ImageView comp   = (ImageView) v.findViewById(R.id.item_comp);
		//ImageView board  = (ImageView) v.findViewById(R.id.item_board);
		//ImageView priv   = (ImageView) v.findViewById(R.id.item_private);
		//ImageView reserv = (ImageView) v.findViewById(R.id.item_reservable);
		//proj.setVisibility(  amentext.contains("p") ? View.VISIBLE : View.GONE);
		//comp.setVisibility(  amentext.contains("c") ? View.VISIBLE : View.GONE);
		//board.setVisibility( amentext.contains("w") ? View.VISIBLE : View.GONE);
		//priv.setVisibility(  amentext.contains("P") ? View.VISIBLE : View.GONE);
		//reserv.setVisibility(amentext.contains("R") ? View.VISIBLE : View.GONE);		
    	 
		return v;
    }
    
    private void setAmenityIcon(int id, CharSequence find, String amentext, View v) {
        ImageView icon   = (ImageView) v.findViewById(id);
        icon.setVisibility(amentext.contains(find) ? View.VISIBLE : View.GONE);
    }
    
    public static DataListAdapter createDefaultAdapter(Context ctx,
                                                       StudySpacesData data) {
        RoomKind[] kinds = data.getRoomKinds();

        return new DataListAdapter (ctx, generateMapList(kinds), kinds);
    }

    public static DataListAdapter createAlphaSortedAdapater(Context ctx,
                                                            StudySpacesData data) {
        RoomKind[] kinds = data.getRoomKinds();
        Arrays.sort(kinds, alphaSort);

        return new DataListAdapter (ctx, generateMapList(kinds), kinds);
    }

    //Removes rooms whose building names do not contain the given substring
    private static RoomKind[] filterRooms(RoomKind[] kinds,String str) {
    	ArrayList<RoomKind> tempList = new ArrayList<RoomKind>();
    	
    	for(RoomKind room : kinds) {
    		if(room.getParentBuilding().getName().contains(str)) {
    			tempList.add(room);
    		}
    	}
    	
    	RoomKind[] result = new RoomKind[tempList.size()];
    	return tempList.toArray(result);
    }
    
    public static DataListAdapter createLocationSortedAdapter(Context ctx,
                                                              StudySpacesData data,
                                                              double latitude,
                                                              double longitude,
                                                              String roomFilter) {

        RoomKind[] kinds = data.getRoomKinds();
        kinds = filterRooms(kinds,roomFilter);
        
        Arrays.sort(kinds, new LocationComparator(latitude, longitude));
        List<Map<String, String>> entries = new ArrayList<Map<String, String>>();

        /*Log.e("createLocationSortedAdapter","the first roomkind is " +
                sortedkinds[0].getParentBuilding().getName());*/
        for (RoomKind roomKind : kinds) {

            Building parent = roomKind.getParentBuilding();
            double klat = parent.getLatitude();
            double klon = parent.getLongitude();

            String humanDistance = String.format("(%.2fmi)",distFrom(latitude, longitude, klat, klon));

            Map<String, String> map = new HashMap<String, String>();
            map.put(BUILDING, roomKind.getParentBuilding().getName());
            map.put(DISTANCE, humanDistance);
            map.put(ROOMKIND, roomKind.getName());
            map.put(AMENITIES, processAmenities(roomKind));

            String roomstr = roomKind.getRooms().get(0).getName();
            int numrooms = roomKind.getRooms().size();
            if((numrooms-1) > 0) {
                map.put(NUM_ROOMS, roomstr+" (and "+(numrooms-1)+" others)");
            }
            else {
                map.put(NUM_ROOMS, roomstr);
            }

            entries.add(map);
        }

        return new DataListAdapter (ctx, entries, kinds);
    }

    // taken from http://stackoverflow.com/questions/120283/working-with-latitude-longitude-values-in-java
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat2-lat1),
               dLon = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2),
               sindLon = Math.sin(dLon / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLon, 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
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

    private static List<Map<String,String>> generateMapList (RoomKind[] kinds) {
        List<Map<String,String>> entries = new ArrayList<Map<String, String>>();

        for (RoomKind roomKind : kinds) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(BUILDING, roomKind.getParentBuilding().getName());
            map.put(DISTANCE, "");
            map.put(ROOMKIND, roomKind.getName());
            map.put(AMENITIES, processAmenities(roomKind));

            String roomstr = roomKind.getRooms().get(0).getName();
            int numrooms = roomKind.getRooms().size();
            if((numrooms-1) > 0) {
                map.put(NUM_ROOMS, roomstr+" (and "+(numrooms-1)+" others)");
            }
            else {
                map.put(NUM_ROOMS, roomstr);
            }

            entries.add(map);
        }

        return entries;
    }

    private static class AlphaComparator implements Comparator<RoomKind> {
        @Override
        public int compare (RoomKind r1, RoomKind r2) {
            return r1.getParentBuilding().getName()
            		.compareTo(r2.getParentBuilding().getName());
        }
    }

    private static class LocationComparator implements Comparator<RoomKind> {
        private double latitude, longitude;

        private LocationComparator(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public int compare(RoomKind r1, RoomKind r2) {
            Building r1parent = r1.getParentBuilding(),
                     r2parent = r2.getParentBuilding();

            // If the building names are the same, then the locations will be
            // equivalent. Knowing this, we sort by alpha in each building
            if (r1parent.getName().equals(r2parent.getName()))
                return r1parent.getName().compareTo(r2parent.getName());

            Double dist1 = distFrom(latitude, longitude,
                    r1.getParentBuilding().getLatitude(),
                    r1.getParentBuilding().getLongitude());
            Double dist2 = distFrom(latitude, longitude,
                    r2.getParentBuilding().getLatitude(),
                    r2.getParentBuilding().getLongitude());

            return dist1.compareTo(dist2);
        }
    }
}
