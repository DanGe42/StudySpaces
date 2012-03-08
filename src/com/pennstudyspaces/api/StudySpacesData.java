package com.pennstudyspaces.api;

import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 3/7/12
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudySpacesData {
    private static final String TAG = StudySpacesData.class.getSimpleName();
    
    public static StudySpacesData sendRequest (StudySpacesApiRequest request)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        StudySpacesData data = mapper.readValue(
                new URL(request.createRequest()), StudySpacesData.class);
        return data;
    }
    private ArrayList<Building> buildings;

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }
    
}
