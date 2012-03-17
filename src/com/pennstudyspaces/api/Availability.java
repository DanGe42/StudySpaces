package com.pennstudyspaces.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Availability {
    private Date date;
    private Range general;
    private ArrayList<Range> openTimes;

    public Availability (Date date, ArrayList schedule) {
        this.date = date;

        this.general = new Range((ArrayList) schedule.get(0));

        ArrayList open = (ArrayList) schedule.get(1);
        if (open != null) {
            this.openTimes = new ArrayList<Range>();
            Iterator iter = open.iterator();
            while (iter.hasNext()) {
                this.openTimes.add(new Range((ArrayList) iter.next()));
            }
        }
        else
            this.openTimes = null;

    }

    public Date getDate() {
        return date;
    }

    public Range getGeneral() {
        return general;
    }

    public ArrayList<Range> getOpenTimes() {
        return openTimes;
    }

    public static class Range {
        private int startHr, startMin, endHr, endMin;

        public Range(int start, int end) {
            this.startHr  = getHour(start);
            this.startMin = getMinutes(start);
            this.endHr    = getHour(end);
            this.endMin   = getMinutes(end);
        }

        /* May throw ClassCastException. Careful there... */
        public Range(ArrayList ar) {
            this ((Integer) ar.get(0), (Integer) ar.get(1));
        }

        public int getStartHour() {
            return startHr;
        }

        public int getStartMin() {
            return startMin;
        }

        public int getEndHour() {
            return endHr;
        }

        public int getEndMin() {
            return endMin;
        }

        private int getHour (int time) {
            return time / 100;
        }

        private int getMinutes (int time) {
            return time % 100;
        }
    }

}
