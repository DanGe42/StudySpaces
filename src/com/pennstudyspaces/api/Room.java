package com.pennstudyspaces.api;

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
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Room {
    private String name;
    private int id;
    private ArrayList<Availability> availabilities;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Availability> getAvailabilities() {
        return availabilities;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAvailabilities(Map<String, Object> availabilities)
            throws ParseException {
        this.availabilities = new ArrayList<Availability>();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (String d : availabilities.keySet()) {
            Date date = format.parse(d);
            this.availabilities.add(new Availability(
                    date, (ArrayList) availabilities.get(d)));
        }
    }
}
