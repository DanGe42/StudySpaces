package com.pennstudyspaces.api;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class JsonData {
    private static final String TAG = JsonData.class.getSimpleName();

    private ArrayList<Building> buildings;

    public static JsonData sendRequest (ApiRequest request)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        JsonData data = mapper.readValue(
                new URL(request.createRequest()), JsonData.class);
        return data;
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    private void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }
    
}
