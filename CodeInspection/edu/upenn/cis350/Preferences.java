package edu.upenn.cis350;

import java.io.Serializable;
import java.util.HashMap;

public class Preferences implements Serializable{

	private HashMap<String, Boolean> favorites;
	
	public Preferences(){
		favorites = new HashMap<String, Boolean>();
	}
	
	//Need to test if the array actually doubles in size
	public void addFavorites(String buildingName, String spaceName){
			favorites.put(buildingName+spaceName, true);
	}
	
	public void removeFavorites(String buildingName, String spaceName){
		if(favorites.containsKey(buildingName+spaceName)) favorites.remove(buildingName+spaceName);
	}
	
	public boolean isFavorite(String buildingName, String spaceName){
		if(favorites.containsKey(buildingName+spaceName)) return true;
		return false;
	}
}
