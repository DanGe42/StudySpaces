package com.pennstudyspaces.api;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 2/23/12
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
class Building {
    private double latitude, longitude;
    private String name;
    private Map<String, RoomKind> roomkinds;
    
    public Building (String name, double latitude, double longitude,
                     Map<String, RoomKind> roomkinds) {
        this.name      = name;
        this.latitude  = latitude;
        this.longitude = longitude;
        this.roomkinds = roomkinds;
    }
    
    public String getName () {
        return name;
    }
    
    public double getLatitude () {
        return latitude;
    }
    
    public double getLongitude () {
        return longitude;
    }
    
    public Map<String, RoomKind> getRoomKinds () {
        return roomkinds;
    }
}
