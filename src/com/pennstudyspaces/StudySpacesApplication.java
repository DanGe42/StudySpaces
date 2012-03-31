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

    private StudySpacesData ssData;
    private String reserveLinkRoot;

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        this.ssData = null;
        this.reserveLinkRoot = null;

        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public StudySpacesData getData() {
        if (this.ssData == null) {
            this.ssData = createDefaultData();
            return this.ssData;
        }
        else {
            return this.ssData;
        }
    }
    
    private StudySpacesData createDefaultData() {
        TimeWrapper now = getDefaultStartTime();
        int hour = now.getHour();
        int month = now.getMonth();
        int day = now.getDay();
        int year = now.getYear();

        /*
        String date = String.format("date=%d-%d-%d", year,month,day);
        String fromTime = String.format("time_from=%02d%02d", nextStartHour, 0);
        String toTime = String.format("time_to=%02d%02d", nextStartHour + 1, 0);
        */

        ParamsRequest req = new ParamsRequest("json");
        req.setNumberOfPeople(1);
        req.setStartTime((hour+1)%23, 0);
        req.setEndTime((hour+2)%23, 0);
        req.setDate(year, month, day);
        req.setPrivate(false);
        req.setWhiteboard(false);
        req.setProjector(false);
        req.setComputer(false);

        return new StudySpacesData(req);
    }
    
    public void resetData() {
        this.ssData = null;
    }
    
    public void updateData() throws IOException {
        getData().pullData();
    }

    /**
     * This method is currently a HACK!!
     * @param data
     */
    public void setData (StudySpacesData data) {
        this.ssData = data;
    }
    
    private TimeWrapper getDefaultStartTime() {
        Calendar now = Calendar.getInstance();
        int minute = now.get(Calendar.MINUTE);
        int hour   = now.get(Calendar.HOUR_OF_DAY);
        int day    = now.get(Calendar.DAY_OF_YEAR);
        int year   = now.get(Calendar.YEAR);

        int nextHour  = hour;
        int nextDay   = day;
        int nextYear  = year;

        // round down to nearest 15 and add 15
        int nextMinute = (minute / 15) * 15 + 15;
        if (nextMinute >= 60) {
            nextMinute %= 60;
            nextHour += 1;

            if (nextHour >= 24) {
                nextHour %= 24;
                nextDay += 1;

                if (nextDay >= 365) {
                    nextDay %= 365;
                    nextYear += 1;
                }
            }
        }

        now.set(Calendar.MINUTE, nextMinute);
        now.set(Calendar.HOUR_OF_DAY, nextHour);
        now.set(Calendar.DAY_OF_YEAR, nextDay);
        now.set(Calendar.YEAR, nextYear);

        return new TimeWrapper(now.get(Calendar.YEAR),
                               now.get(Calendar.MONTH),
                               now.get(Calendar.DAY_OF_MONTH),
                               now.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * A wrapper object for hour, month, day, and year because Java doesn't
     * have tuples.
     */
    private class TimeWrapper {
        private int year, month, day, hour;

        private TimeWrapper(int year, int month, int day, int hour) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
        }



        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getHour() {
            return hour;
        }
    }
}
