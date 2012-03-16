package com.pennstudyspaces;

import android.app.Application;
import com.pennstudyspaces.api.StudySpacesData;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 3/15/12
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudySpacesApplication extends Application {
    private static final String TAG = StudySpacesApplication.class.getSimpleName();

    private StudySpacesData ssData;

    @Override
    public void onCreate() {
        super.onCreate();

        this.ssData = null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public StudySpacesData getData() {
        return null;
    }
}
