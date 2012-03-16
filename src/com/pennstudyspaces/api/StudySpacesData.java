package com.pennstudyspaces.api;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 3/16/12
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudySpacesData {
    private ApiRequest request;
    private Building[] data;
    
    public StudySpacesData (ApiRequest request) {
        if (request == null)
            throw new IllegalArgumentException("Request cannot be null");
        this.request = request;
        this.data = null;
    }
    
    public void pullData() throws IOException {
        JsonData jsonData = JsonData.sendRequest(request);
        
        this.data = jsonData.getBuildings().toArray(new Building[1]);
    }
    
    public ApiRequest getApiRequest() {
        return request;
    }
    
    public Building[] getBuildings() {
        return this.data;
    }
}
