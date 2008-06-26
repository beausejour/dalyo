package com.penbase.dma.Dalyo;

import java.util.List;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;

public class Gps{
	private Context context;
	private String providerName;
	private LocationManager locationManager;
	private boolean hasProvider = false;
	
	public Gps(Context c){
		this.context = c;
		providerName = "gps";
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<LocationProvider> providers = locationManager.getProviders();
        for (int i=0; i<providers.size(); i++){
        	if (providers.get(i).getName().equals(providerName)){
        		hasProvider = true;
        	}
        }
	}
	
	public int GetStatus(){
		if (hasProvider){
			return locationManager.getProviderStatus(providerName);
		}
		else{
			return 0;
		}
	}
	
	public Location getLocation(){
		return locationManager.getCurrentLocation(providerName);
	}
	
	public double getLogitude(Location location){
		return location.getLongitude();
	}
	
	public double getLatitude(Location location){
		return location.getLatitude();
	}
	
	public void stop(){
		hasProvider = false;
	}
}
