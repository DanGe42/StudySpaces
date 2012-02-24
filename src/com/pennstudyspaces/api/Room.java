package com.pennstudyspaces.api;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 2/23/12
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
class Room {
    private String name;
    private int id;
    
    // TODO: This is currently here because I don't understand the JSON here
    private Map<String, Object[]> availabilities;

    Room(String name, int id, Map<String, Object[]> availabilities) {
        this.name = name;
        this.id = id;
        this.availabilities = availabilities;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Map<String, Object[]> getAvailabilities() {
        return availabilities;
    }
}
