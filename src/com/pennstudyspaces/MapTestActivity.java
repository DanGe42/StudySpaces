package com.pennstudyspaces;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.maps.MapActivity;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 3/9/12
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapTestActivity extends MapActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}