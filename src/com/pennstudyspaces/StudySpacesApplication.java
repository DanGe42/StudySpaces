package com.pennstudyspaces;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.pennstudyspaces.api.ParamsRequest;
import com.pennstudyspaces.api.StudySpacesData;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 3/15/12
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudySpacesApplication extends Application {
    private static final String TAG = StudySpacesApplication.class.getSimpleName();


    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

}
