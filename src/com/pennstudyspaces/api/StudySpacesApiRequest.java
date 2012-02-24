package com.pennstudyspaces.api;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Ge
 * Date: 2/21/12
 * Time: 2:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudySpacesApiRequest {

    /* Parameters that go into the API GET request */
    private static final String NUM_PEOPLE = "capacity",
                                PRIVATE    = "private",
                                WHITEBOARD = "whiteboard",
                                COMPUTER   = "computer",
                                PROJECTOR  = "monitor",
                                START_HOUR = "shr",
                                START_MIN  = "smin",
                                END_HOUR   = "ehr",
                                END_MIN    = "emin",
                                DATE       = "date",
                                FORMAT     = "format";
    
    private static final String API_URL = "http://www.pennstudyspaces.com/api?";

    private int numPeople;
    private boolean priv, wboard, comp, proj;
    private int start_hr, start_min, end_hr, end_min;
    private long date;
    private String format;

    public StudySpacesApiRequest(String format) {
        if (!format.equals("json"))
            throw new IllegalArgumentException("Unsupported format");
        this.format = format;
        
        numPeople = -1;
        priv = wboard = comp = proj = false;
        start_hr = start_min = end_hr = end_min = -1;
        date = -1;
    }

    public void setNumberOfPeople (int numPeople) {
        if (numPeople < 1)
            throw new IllegalArgumentException("Number of people < 1");
        this.numPeople = numPeople;
    }

    public void setPrivate (boolean priv) {
        this.priv = priv;
    }

    public void setWhiteboard (boolean wboard) {
        this.wboard = wboard;
    }

    public void setComputer (boolean comp) {
        this.comp = comp;
    }

    public void setProjector (boolean proj) {
        this.proj = proj;
    }

    public void setStartTime (int hr, int min) {
        if (hr < 0 || hr >= 24 || min < 0 || min >= 60)
            throw new IllegalArgumentException("Hour or minutes not within" +
                    "proper bounds.");
        this.start_hr = hr;
        this.start_min = min;
    }

    public void setEndTime (int hr, int min) {
        if (hr < 0 || hr >= 24 || min < 0 || min >= 60)
            throw new IllegalArgumentException("Hour or minutes not within" +
                    "proper bounds.");
        this.end_hr = hr;
        this.end_min = min;
    }

    public void setDate (int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        this.date = cal.getTimeInMillis();
    }

    public boolean validate() {
        // These fields are required (if they're not, the request won't make
        // any sense at all.
        return !(numPeople < 1 ||
                start_hr < 0 || start_hr >= 24 ||
                start_min < 0 || start_min >= 60 ||
                end_hr < 0 || end_hr >= 24 ||
                end_min < 0 || end_min >= 60 ||
                date < 0);
    }
    
    @Override
    public String toString() {
        final int INITIAL_CAPACITY = 70;
        StringBuilder builder = new StringBuilder(INITIAL_CAPACITY);
        
        builder.append(NUM_PEOPLE + "=" + this.numPeople);

        if (this.priv) {
            builder.append("&" + PRIVATE + "=1");
        }
        
        if (this.wboard) {
            builder.append("&" + WHITEBOARD + "=1");
        }

        if (this.comp) {
            builder.append("&" + COMPUTER + "=1");
        }

        if (this.proj) {
            builder.append("&" + PROJECTOR + "=1");
        }

        builder.append("&" + START_HOUR + "=" + this.start_hr);
        builder.append("&" + START_MIN  + "=" + this.start_min);
        builder.append("&" + END_HOUR   + "=" + this.end_hr);
        builder.append("&" + END_MIN    + "=" + this.end_min);
        
        builder.append("&" + DATE + "=" + this.date);
        
        builder.append("&" + FORMAT + "=" + this.format);
        
        return builder.toString();
    }
    
    public String createRequest() {
        if (validate())
            return API_URL + this.toString();
        else
            throw new IllegalStateException("There is something wrong with" +
                    "this API request: " + this.toString());
    }
}
