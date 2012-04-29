package com.pennstudyspaces.api;

import java.io.IOException;
import java.util.ArrayList;


public class StudySpacesData {
    private ApiRequest request;
    private Building[] buildingData;
    private RoomKind[] roomKindsData;
    
    public StudySpacesData (ApiRequest request) throws IOException {
        if (request == null)
            throw new IllegalArgumentException("Request cannot be null");
        this.request = request;
        this.buildingData = null;
        this.roomKindsData = null;
        pullData();
    }
    
    private void pullData() throws IOException {
        JsonData jsonData = JsonData.sendRequest(request);
        
        this.buildingData = jsonData.getBuildings().toArray(new Building[1]);

        ArrayList<RoomKind> roomKinds =
                new ArrayList<RoomKind>(getBuildings().length * 5);
        for (Building building : this.buildingData) {
            roomKinds.addAll(building.getRoomKinds());
        }
        
        this.roomKindsData = roomKinds.toArray(new RoomKind[1]);
    }
    
    public ApiRequest getApiRequest() {
        return request;
    }
    
    private Building[] getBuildings() {
        return this.buildingData;
    }
    
    public RoomKind[] getRoomKinds() {
        return this.roomKindsData;
    }
}
