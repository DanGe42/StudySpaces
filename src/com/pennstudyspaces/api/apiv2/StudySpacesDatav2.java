package com.pennstudyspaces.api.apiv2;

import com.pennstudyspaces.api.StudySpacesApiRequest;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 3/7/12
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudySpacesDatav2 {
    public static StudySpacesDatav2 sendRequest (StudySpacesApiRequest request)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        StudySpacesDatav2 data = mapper.readValue(
                new URL(request.createRequest()), StudySpacesDatav2.class);
        return data;
    }
    private ArrayList<Building> buildings;

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }
    
    public static class Building {
        private String name;
        private double latitude, longitude;
        private ArrayList<RoomKind> roomkinds;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public ArrayList<RoomKind> getRoomkinds() {
            return roomkinds;
        }

        public void setRoomkinds(ArrayList<RoomKind> roomkinds) {
            this.roomkinds = roomkinds;
        }

        public static class RoomKind {
            private boolean hasProjector, hasComputer, hasWhiteboard;
            private String reserveType, name, privacy, comments;
            private int capacity;
            private ArrayList<Room> rooms;

            public int getMaxOccupancy() {
                return capacity;
            }

            public void setMaxOccupancy(int capacity) {
                this.capacity = capacity;
            }

            public String getReserveType() {
                return reserveType;
            }

            public void setReserveType(String reserveType) {
                this.reserveType = reserveType;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPrivacy() {
                return privacy;
            }

            public void setPrivacy(String privacy) {
                this.privacy = privacy;
            }

            public String getComments() {
                return comments;
            }

            public void setComments(String comments) {
                this.comments = comments;
            }

            public boolean isHasProjector() {
                return hasProjector;
            }

            public void setHasProjector(boolean hasProjector) {
                this.hasProjector = hasProjector;
            }

            public boolean isHas_Computer() {
                return hasComputer;
            }

            public void setHas_Computer(boolean hasComputer) {
                this.hasComputer = hasComputer;
            }

            public boolean isHas_Whiteboard() {
                return hasWhiteboard;
            }

            public void setHas_Whiteboard(boolean hasWhiteboard) {
                this.hasWhiteboard = hasWhiteboard;
            }

            public static class Room {
                private String name;
                private int id;
                private ArrayList<Object> availabilities;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public ArrayList<Object> getAvailabilities() {
                    return availabilities;
                }

                public void setAvailabilities(ArrayList<Object> availabilities) {
                    this.availabilities = availabilities;
                }
            }
        }
    }
}
