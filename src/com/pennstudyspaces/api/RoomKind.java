package com.pennstudyspaces.api;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 2/23/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoomKind {
    public enum Privacy {
        COMMON,
        PRIVATE
    }
    public enum Reservation {
        EXTERNAL,
        NONE
    }
    private boolean computer, whiteboard, projector;
    private Privacy privacy;
    private Reservation reserve;
    
    private String name;
    private int occupancy;
    private String comments;
    
    private Map<Integer, Room> rooms;

    RoomKind (Privacy privacy, Reservation reserve,
                     boolean computer, boolean whiteboard, boolean projector,
                     String name, int occupancy, String comments,
                     Map<Integer, Room> rooms) {
        this.computer = computer;
        this.whiteboard = whiteboard;
        this.projector = projector;
        this.privacy = privacy;
        this.reserve = reserve;
        this.name = name;
        this.occupancy = occupancy;
        this.comments = comments;
        this.rooms = rooms;
    }

    static RoomKind createRoomKind (String privacy, String reserve,
                     boolean computer, boolean whiteboard, boolean projector,
                     String name, int occupancy, String comments,
                     Map<Integer, Room> rooms) {
        Reservation r;
        if (reserve.equals("N"))
            r = Reservation.NONE;
        else if (reserve.equals("E"))
            r = Reservation.EXTERNAL;
        else 
            throw new IllegalArgumentException("Bad reservation type: " + reserve);

        Privacy p;
        if (privacy.equals("S"))
            p = Privacy.COMMON;
        else if (privacy.equals("P"))
            p = Privacy.PRIVATE;
        else
            throw new IllegalArgumentException("Bad privacy type: " + privacy);
        
        return new RoomKind(p, r, computer, whiteboard, projector, name,
                occupancy, comments, rooms);
    }

    public boolean isComputer() {
        return computer;
    }

    public boolean isWhiteboard() {
        return whiteboard;
    }

    public boolean isProjector() {
        return projector;
    }

    public Privacy getPrivacy() {
        return privacy;
    }

    public Reservation getReserve() {
        return reserve;
    }

    public String getName() {
        return name;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public String getComments() {
        return comments;
    }

    public Map<Integer, Room> getRooms() {
        return rooms;
    }
}
