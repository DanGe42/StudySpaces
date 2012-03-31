package com.pennstudyspaces;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 3/31/12
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrefsActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}