package com.pennstudyspaces.api;

import org.codehaus.jackson.annotate.JsonSetter;

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
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Building {
    private String name;
    private double latitude, longitude;
    private ArrayList<RoomKind> roomkinds;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public ArrayList<RoomKind> getRoomKinds() {
        return roomkinds;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private void setRoomkinds(ArrayList<RoomKind> roomkinds) {
        this.roomkinds = roomkinds;
        
        for (RoomKind kind : roomkinds) {
            kind.setParentBuilding(this);
        }
    }
}
