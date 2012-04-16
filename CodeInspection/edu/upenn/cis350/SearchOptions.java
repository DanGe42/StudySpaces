package edu.upenn.cis350;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class SearchOptions implements Serializable, Parcelable {
	
	private int numberOfPeople = 0;
	
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	private int year;
	private int month;
	private int day;
	
	private boolean isPrivate = false;
	private boolean hasWhiteboard = false;
	private boolean hasComputer = false;
	private boolean hasProjector = false;
	
	private boolean engiBox;
	private boolean wharBox;
	private boolean libBox;
	private boolean othBox;
	
	public void setNumberOfPeople(int _numberOfPeople) {
		numberOfPeople = _numberOfPeople;
	}
	public void setStartHour(int _startHour) {
		startHour = _startHour;
	}
	public void setStartMinute(int _startMinute) {
		startMinute = _startMinute;
	}
	public void setEndHour(int _endHour) {
		endHour = _endHour;
	}
	public void setEndMinute(int _endMinute) {
		endMinute = _endMinute;
	}
	public void setYear(int _year) {
		year = _year;
	}
	public void setMonth(int _month) {
		month = _month;
	}
	public void setDay(int _day) {
		day = _day;
	}
	public void setPrivate(boolean _isPrivate) {
		isPrivate = _isPrivate;
	}
	public void setWhiteboard(boolean _hasWhiteboard) {
		hasWhiteboard = _hasWhiteboard;
	}
	public void setComputer(boolean _hasComputer) {
		hasComputer = _hasComputer;
	}
	public void setProjector(boolean _hasProjector) {
		hasProjector = _hasProjector;
	}
	public void setEngi(boolean _bool){
		engiBox = _bool;
	}
	public void setWhar(boolean _bool){
		wharBox = _bool;
	}
	public void setLib(boolean _bool){
		libBox = _bool;
	}
	public void setOth(boolean _bool){
		othBox = _bool;
	}
	
	public int getNumberOfPeople() {
		return numberOfPeople;
	}
	public int getStartHour() {
		return startHour;
	}
	public int getStartMinute() {
		return startMinute;
	}
	public int getEndHour() {
		return endHour;
	}
	public int getEndMinute() {
		return endMinute;
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
	public boolean getPrivate() {
		return isPrivate;
	}
	public boolean getWhiteboard() {
		return hasWhiteboard;
	}
	public boolean getComputer() {
		return hasComputer;
	}
	public boolean getProjector() {
		return hasProjector;
	}
	public boolean getEngi(){
		return engiBox;
	}
	public boolean getWhar(){
		return wharBox;
	}
	public boolean getLib(){
		return libBox;
	}
	public boolean getOth(){
		return othBox;
	}
	
	
	
	
	public Date getStartDate() {
		
    	// Create Date object from the raw time and date data:
		
    	Date startDate = new Date();
    	//Log.e("year",Integer.toString(year));
    	startDate.setYear(year - 1900);    	
    	startDate.setMonth(month);
    	startDate.setDate(day);
    	startDate.setHours(startHour);
    	startDate.setMinutes(startMinute);
    	
    	return startDate;
	}
	public Date getEndDate() {
		
		// Create Date object from the raw time and date data:
		
    	Date endDate = new Date();
    	
    	endDate.setYear(year - 1900);
    	endDate.setMonth(month);
    	endDate.setDate(day);
    	endDate.setHours(endHour);
    	endDate.setMinutes(endMinute);
    	
    	return endDate;
	}
	
	
	
	
	
	// Parcelable stuff:
	
	public int describeContents() {
        return 0;
    }
	
	/** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(numberOfPeople);
        out.writeInt(startHour);
        out.writeInt(startMinute);
		out.writeInt(endHour);
		out.writeInt(endMinute);
		out.writeInt(year);
		out.writeInt(month);
		out.writeInt(day);
		boolean[] booleanArray = {isPrivate, hasWhiteboard, hasComputer, hasProjector, engiBox, wharBox, libBox, othBox};
		out.writeBooleanArray(booleanArray);
    }

    public static final Parcelable.Creator<SearchOptions> CREATOR
            = new Parcelable.Creator<SearchOptions>() {
        public SearchOptions createFromParcel(Parcel in) {
            return new SearchOptions(in);
        }

        public SearchOptions[] newArray(int size) {
            return new SearchOptions[size];
        }
    };

    /** recreate object from parcel */
    private SearchOptions(Parcel in) {
        numberOfPeople = in.readInt();
        startHour = in.readInt();
        startMinute = in.readInt();
        endHour = in.readInt();
        endMinute = in.readInt();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        boolean[] booleanArray = {false, false, false, false, false, false, false, false};
        in.readBooleanArray(booleanArray);
        isPrivate = booleanArray[0];
        hasWhiteboard = booleanArray[1];
        hasComputer = booleanArray[2];
        hasProjector = booleanArray[3];
        engiBox = booleanArray[4];
        wharBox = booleanArray[5];
        libBox = booleanArray[6];
        othBox = booleanArray[7];
    }
	public SearchOptions() {
		// TODO Auto-generated constructor stub
	}
    

    
    
	
}
