package com.pennstudyspaces.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Room {
    private String name;
    private int id;
    private ArrayList<Availability> availabilities;
    private RoomKind parent;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Availability> getAvailabilities() {
        return availabilities;
    }

    public RoomKind getParentRoomKind() {
        return this.parent;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setId(int id) {
        this.id = id;
    }

    private void setAvailabilities(Map<String, Object> availabilities)
            throws ParseException {
        this.availabilities = new ArrayList<Availability>();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (String d : availabilities.keySet()) {
            Date date = format.parse(d);
            this.availabilities.add(new Availability(
                    date, (ArrayList) availabilities.get(d)));
        }
    }
    
    void setParentRoomKind(RoomKind parent) {
        this.parent = parent;
    }
}
