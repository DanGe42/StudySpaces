package com.pennstudyspaces.api;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 2/21/12
 * Time: 1:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudySpacesData {
    private static final String TAG = StudySpacesData.class.getSimpleName();
    
    private Map<String, Building> buildings;

    private StudySpacesData (Map<String, Building> buildings) {
        this.buildings = buildings;
    }
    
    public static StudySpacesData sendRequest (StudySpacesApiRequest request)
            throws ClientProtocolException, IOException {
        getJSON(request);
        return null;
    }
    
    private static JSONObject getJSON (StudySpacesApiRequest request)
            throws IOException {
        Log.i(TAG, "Sending request: " + request.createRequest());
        HttpUriRequest get = new HttpGet(request.createRequest());
        DefaultHttpClient httpClient = new DefaultHttpClient();

        // throws ClientProtocolException or IOException
        HttpResponse response = httpClient.execute(get);

        // throws ParseException or IOException
        String jsonText = EntityUtils.toString(response.getEntity());
        Log.d(TAG, "Response: " + jsonText);
        JsonParser parser = new JsonParser();
        JsonArray ary = parser.parse(jsonText).getAsJsonArray();
        
        Log.d(TAG, "JSON result: " + ary.toString());
        return null;
    }
}
