package edu.upenn.cis350;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.*;

import java.io.Serializable;

public class Room implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ID;
	private String roomName;
	private String availabilities;
	//private JSONObject avails;
	/*private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat df1 = new SimpleDateFormat("HH");
	private DateFormat df2 = new SimpleDateFormat("mm");*/
	
	public Room(int id, String name, JSONObject avail) {
		ID = id;
		roomName = name;
		availabilities = avail.toString();
	}
	
	public int getID() {
		return ID;
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public boolean searchAvailability(Date d1, Date d2) throws Exception {
		int year1 = (d1.getYear() + 1900);
		int year2 = (d2.getYear() + 1900);
		int month1 = (d1.getMonth() + 1);
		int month2 = (d2.getMonth() + 1);
		int day1 = d1.getDate();
		int day2 = d2.getDate();
		String m1 = "";
		String m2 = "";
		String da1 = "";
		String da2 = "";
		if(year1 == year2) {
			if(month1 < 10) {
				m1 = "0" + month1;
			}
			else {
				m1 = "" + month1;
			}
			if(month2 < 10) {
				m2 = "0" + month2;
			}
			else {
				m2 = "" + month2;
			}
			if(day1 < 10) {
				da1 = "0" + day1;
			}
			else {
				da1 = "" + day1;
			}
			if(day2 < 10) {
				da2 = "0" + day2;
			}
			else {
				da2 = "" + day2;
			}
			String date = year1 + "-" + m1 + "-" + da1;
			int h1 = d1.getHours();
			int mo1 = d1.getMinutes();
			int h2 = d2.getHours();
			int mo2 = d2.getMinutes();
			int start = (h1*100) + mo1;
			int end = (h2*100) + mo2;
			if(end <= start) {
				return false;
			}
			else {
				return getAvailability(start, end, date);
			}
		}
		else {
			return false;
		}
	}
	
	public boolean availableNow() throws Exception {
		Date d = new Date();
		int year = d.getYear();
		int month = d.getMonth();
		int day = d.getDate();
		String mo = "";
		String da = "";
		if(month < 10) {
			mo = "0" + month;
		}
		else {
			mo = "" + month;
		}
		if(day < 10) {
			da = "0" + day;
		}
		else {
			da = "" + day;
		}
		String date = year + "-" + mo + "-" + da;
		int h = d.getHours();
		int m = d.getMinutes();
		int start = (h*100) + m;
		int end = start+100;
		return getAvailability(start, end, date);
	}
	
	public boolean getAvailability(int start_time, int end_time, String date) throws Exception {
		JSONArray times = new JSONObject(availabilities).getJSONArray(date);
		JSONArray times_on_date = times.getJSONArray(0);
		int start_time_on_date = times_on_date.getInt(0);
		int end_time_on_date = times_on_date.getInt(1);
		
		if(times.get(1) == JSONObject.NULL) {
			if((start_time >= start_time_on_date) && (end_time <= end_time_on_date)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			JSONArray available_times_on_date = (JSONArray) times.get(1);
			int flag = 0;
			
			for(int i = 0; i < available_times_on_date.length(); i++) {
				JSONArray temp = available_times_on_date.getJSONArray(i);
				int temp_start_time = temp.getInt(0);
				int temp_end_time = temp.getInt(1);
				
				if((start_time >= temp_start_time) && (end_time <= temp_end_time)) {
					flag = 1;
				}
			}
			
			if(flag == 1) {
				return true;
			}
			else {
				return false;
			}
		}
	}
}
