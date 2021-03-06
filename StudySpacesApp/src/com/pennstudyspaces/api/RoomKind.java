package com.pennstudyspaces.api;

import java.util.ArrayList;

import android.util.Log;
import org.codehaus.jackson.annotate.JsonSetter;

public class RoomKind {
    private boolean hasProjector, hasComputer, hasWhiteboard;
    private String name, comments;
    private Privacy privacy;
    private Reserve reserveType;
    private int capacity;
    private ArrayList<Room> rooms;
    private Building parent;
    private static final String TAG = RoomKind.class.getSimpleName();

    public enum Privacy {
        COMMON,
        PRIVATE
    }
    public enum Reserve {
        NONE,
        EXTERNAL
    }

    public int getCapacity() {
        return capacity;
    }

    public Reserve getReserveType() {
        return reserveType;
    }

    public String getName() {
        return name;
    }

    public Privacy getPrivacy() {
        return privacy;
    }

    public String getComments() {
        return comments;
    }

    public boolean hasComputer() {
        return hasComputer;
    }

    public boolean hasProjector() {
        return hasProjector;
    }

    public boolean hasWhiteboard() {
        return hasWhiteboard;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
    
    public Building getParentBuilding() {
        return this.parent;
    }

    @JsonSetter("max_occupancy")
    private void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    private void setName(String name) {
        this.name = name;
    }

    @JsonSetter("reserve_type")
    private void setReserveType(String reserveType) {
        Log.d(TAG, "reserveType = " + reserveType);
        if ("N".equals(reserveType))
            this.reserveType = Reserve.NONE;
        else if ("E".equals(reserveType))
            this.reserveType = Reserve.EXTERNAL;
        else throw new IllegalArgumentException("Invalid reserve type");
    }

    private void setPrivacy(String privacy) {
        if ("S".equals(privacy))
            this.privacy = Privacy.COMMON;
        else if ("P".equals(privacy))
            this.privacy = Privacy.PRIVATE;
        else throw new IllegalArgumentException("Invalid privacy type");
    }

    private void setComments(String comments) {
        this.comments = comments;
    }

    @JsonSetter("has_big_screen")
    private void setProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    @JsonSetter("has_computer")
    private void setComputer(boolean hasComputer) {
        this.hasComputer = hasComputer;
    }

    @JsonSetter("has_whiteboard")
    private void setWhiteboard(boolean hasWhiteboard) {
        this.hasWhiteboard = hasWhiteboard;
    }

    private void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
        
        for (Room room : rooms) {
            room.setParentRoomKind(this);
        }
    }
    
    void setParentBuilding(Building parent) {
        this.parent = parent;
    }


}
