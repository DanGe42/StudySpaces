package com.pennstudyspaces.api;

import android.util.Log;

import java.util.ArrayList;

public class Building {
    private static final String TAG = Building.class.getSimpleName();
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
            Log.d(TAG, kind.getName() + " -> " + kind.getReserveType());
        }
    }
}
