package com.penbase.dma.Dalyo;

import java.util.List;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class Gps{
	private Context context;
	private String providerName;
	private LocationManager locationManager;
	private boolean hasProvider = false;
	
	public Gps(Context c) {
		this.context = c;
		providerName = "gps";
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//List<LocationProvider> providers = locationManager.getProviders();
		List<String> providers = locationManager.getAllProviders();
		int size = providers.size();
        for (int i=0; i<size; i++) {
        	/*if (providers.get(i).getName().equals(providerName)) {
        		hasProvider = true;
        	}*/
        }
	}
	
	public int GetStatus() {
		if (hasProvider) {
			//return locationManager.getProviderStatus(providerName);
			//return locationManager.getProvider(providerName);
			return 1;
		}
		else {
			return 0;
		}
	}
	
	public Location getLocation() {
		//return locationManager.getCurrentLocation(providerName);
		return locationManager.getLastKnownLocation(providerName);
	}
	
	public double getLogitude(Location location) {
		return location.getLongitude();
	}
	
	public double getLatitude(Location location) {
		return location.getLatitude();
	}
	
	public void stop() {
		hasProvider = false;
	}
}
