package com.pennstudyspaces.api;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 2/21/12
 * Time: 1:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudySpacesData {
    private static final String TAG = StudySpacesData.class.getSimpleName();
    
    private Map<String, Building> buildings;
    
    private StudySpacesData (Map<String, Building> buildings) {
        this.buildings = buildings;
    }
    
    public List<BuildingRoomPair> getRoomTypes() {
        List<BuildingRoomPair> roomTypeList = new ArrayList<BuildingRoomPair>();
        for (Map.Entry<String, Building> buildingEntry : buildings.entrySet()) {
            Building b = buildingEntry.getValue();
            for (Map.Entry<String, RoomKind> roomKindEntry : b.getRoomKinds().entrySet()) {
                roomTypeList.add(new BuildingRoomPair(b, roomKindEntry.getValue()));
            }
        }

        return roomTypeList;
    }
    
    public static class BuildingRoomPair {
        private Building building;
        private RoomKind roomKind;
        
        private BuildingRoomPair (Building b, RoomKind r) {
            building = b;
            roomKind = r;
        }

        public Building getBuilding() {
            return building;
        }

        public RoomKind getRoomKind() {
            return roomKind;
        }
    }
    
    public static StudySpacesData sendRequest (StudySpacesApiRequest request)
            throws ClientProtocolException, IOException {
        return new StudySpacesData(getJSON(request));
    }

    public static StudySpacesData sendRequest (String request)
            throws ClientProtocolException, IOException {
        return new StudySpacesData(getJSON(request));
    }

    private static Map<String, Building> getJSON (StudySpacesApiRequest request)
            throws IOException{
        return getJSON(request.createRequest());
    }
    
    /* This call is blocking */
    private static Map<String, Building> getJSON (String request)
            throws IOException {
        Log.i(TAG, "Sending request: " + request);
        HttpUriRequest get = new HttpGet(request);
        DefaultHttpClient httpClient = new DefaultHttpClient();

        // throws ClientProtocolException or IOException
        HttpResponse response = httpClient.execute(get);

        // throws ParseException or IOException
        String jsonText = EntityUtils.toString(response.getEntity());
        Log.d(TAG, "Response: " + jsonText);
        Reader r = new StringReader(jsonText);
        
        Map<String, Building> parsedJson = null;
        
        try {
            parsedJson = readJSON(r);
            Log.d(TAG, "Success!");
        } catch (IOException e) {
            Log.d(TAG, "Oh boy...", e);
        }
        
        return parsedJson;
    }

    private static Map<String, Building> readJSON (Reader in)
            throws IOException {
        final String BUILDINGS = "buildings",
                     LAT       = "latitude",
                     LON       = "longitude",
                     NAME      = "name",
                     ROOMKINDS = "roomkinds";
        
        Map<String, Building> buildings = new HashMap<String, Building>();

        JsonReader reader = new JsonReader(in);
        reader.beginObject();

        // The JSON should be an object with one attribute:
        // buildings -> array of buildings
        if (!reader.nextName().equals(BUILDINGS)) {
            return null;
        }
        
        reader.beginArray();
        // Read each entry in the buildings array
        while (reader.hasNext()) {
            reader.beginObject();
            Map<String, Object> buildingProps = new HashMap<String, Object>();
            
            // Read the building object
            while (reader.hasNext()) {
                String buildingProp = reader.nextName();
                
                if (buildingProp.equals(LAT)) {
                    buildingProps.put(buildingProp, reader.nextDouble());
                }
                else if (buildingProp.equals(LON)) {
                    buildingProps.put(buildingProp, reader.nextDouble());
                }
                else if (buildingProp.equals(NAME)) {
                    buildingProps.put(buildingProp, reader.nextString());
                }
                else if (buildingProp.equals(ROOMKINDS)) {
                    Map<String, RoomKind> roomkinds = readRoomKindsArray(reader);
                    buildingProps.put(buildingProp, roomkinds);
                }
                else {
                    Log.w(TAG, "Warning: Unrecognized property: " + buildingProp);
                }
            }
            
            buildings.put((String) buildingProps.get(NAME),
                    new Building((String) buildingProps.get(NAME),
                            (Double) buildingProps.get(LAT),
                            (Double) buildingProps.get(LON),
                            (Map<String, RoomKind>) buildingProps.get(ROOMKINDS)));
            reader.endObject();
        }

        reader.endArray();
        reader.endObject();

        reader.close();
        return buildings;
    }
    
    private static Map<String, RoomKind> readRoomKindsArray (JsonReader reader) 
            throws IOException {
        final String PROJ       = "has_big_screen",
                     RESERVE    = "reserve_type",
                     COMP       = "has_computer",
                     NAME       = "name",
                     PRIVACY    = "privacy",
                     WHITEBOARD = "has_whiteboard",
                     OCCU       = "max_occupancy",
                     COMM       = "comments",
                     ROOMS      = "rooms";
        
        reader.beginArray();
        Map<String, RoomKind> roomkinds = new HashMap<String, RoomKind>();
        
        while (reader.hasNext()) {
            reader.beginObject();
            Map<String, Object> rkProps = new HashMap<String, Object>();
            
            while (reader.hasNext()) {
                String rkProp = reader.nextName();
                
                if (rkProp.equals(PROJ)) {
                    rkProps.put(rkProp, reader.nextBoolean());
                }
                else if (rkProp.equals(RESERVE)) {
                    rkProps.put(rkProp, reader.nextString());
                }
                else if (rkProp.equals(COMP)) {
                    rkProps.put(rkProp, reader.nextBoolean());
                }
                else if (rkProp.equals(NAME)) {
                    rkProps.put(rkProp, reader.nextString());
                }
                else if (rkProp.equals(PRIVACY)) {
                    rkProps.put(rkProp, reader.nextString());
                }
                else if (rkProp.equals(WHITEBOARD)) {
                    rkProps.put(rkProp, reader.nextBoolean());
                }
                else if (rkProp.equals(OCCU)) {
                    rkProps.put(rkProp, reader.nextInt());
                }
                else if (rkProp.equals(COMM)) {
                    rkProps.put(rkProp, reader.nextString());
                }
                else if (rkProp.equals(ROOMS)) {
                    Map<Integer, Room> rooms = readRoomsArray(reader);
                    rkProps.put(rkProp, rooms);
                }
                else
                    Log.w(TAG, "Unrecognized property: " + rkProp);
            }
            
            roomkinds.put((String) rkProps.get(NAME),
                    RoomKind.createRoomKind((String) rkProps.get(PRIVACY),
                            (String) rkProps.get(RESERVE),
                            (Boolean) rkProps.get(COMP),
                            (Boolean) rkProps.get(WHITEBOARD),
                            (Boolean) rkProps.get(PROJ),
                            (String) rkProps.get(NAME),
                            (Integer) rkProps.get(OCCU),
                            (String) rkProps.get(COMM),
                            (Map<Integer, Room>) rkProps.get(ROOMS)));
            reader.endObject();
        }
        
        reader.endArray();
        return roomkinds;
    }
    
    private static Map<Integer, Room> readRoomsArray (JsonReader reader) 
            throws IOException{
        final String NAME = "name",
                     ID   = "id",
                     AV   = "availabilities";
        reader.beginArray();
        Map<Integer, Room> rooms = new HashMap<Integer, Room>();
        
        while (reader.hasNext()) {
            reader.beginObject();
            Map<String, Object> roomProps = new HashMap<String, Object>();
            
            while (reader.hasNext()) {
                String roomProp = reader.nextName();
                
                if (roomProp.equals(NAME)) {
                    roomProps.put(roomProp, reader.nextString());
                }
                else if (roomProp.equals(ID)) {
                    roomProps.put(roomProp, reader.nextInt());
                }
                else if (roomProp.equals(AV)) {
                    Map<String, Object[]> av = getAvailabilities(reader);
                    roomProps.put(roomProp, av);
                }
            }
            
            rooms.put((Integer) roomProps.get(ID),
                    new Room((String) roomProps.get(NAME),
                            (Integer) roomProps.get(ID),
                            (Map<String, Object[]>) roomProps.get(AV)));
            reader.endObject();
        }
        
        reader.endArray();

        return rooms;
    }
    
    // TODO
    // HACK
    // HACK
    // HACK
    // XXX
    // FIXME
    private static Map<String, Object[]> getAvailabilities (JsonReader reader)
            throws IOException {
        reader.beginObject();
        // HACK HACK HACK
        Map<String, Object[]> availabilities = new HashMap<String, Object[]>();

        while (reader.hasNext()) {
            String date = reader.nextName();
            // TODO: verify that the date is in the right format
            
            // Read the first array (no idea what it does)
            reader.beginArray();
            reader.beginArray();
            int[] range = new int[] {reader.nextInt(), reader.nextInt()};
            reader.endArray();

            // What are these arrays for?
            ArrayList<int[]> range2;
            if (reader.peek() == JsonToken.NULL) {
                range2 = null;
                reader.nextNull();
            }
            else {
                range2 = new ArrayList<int[]>();
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginArray();
                    range2.add(new int[] {reader.nextInt(), reader.nextInt()});
                    reader.endArray();
                }
                reader.endArray();
            }
            reader.endArray();
            
            availabilities.put(date, new Object[] {range, range2});
        }
        reader.endObject();
        
        return availabilities;
    }
}
