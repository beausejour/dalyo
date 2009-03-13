package com.penbase.dma.Dalyo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class Gps{
	private static final String providerName = "gps";
	private LocationManager locationManager;
    private Location location = null;
    private LocationListener locationListener;
    private int status = LocationProvider.OUT_OF_SERVICE;
	
	public Gps(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        location = locationManager.getLastKnownLocation(providerName);
	}
	
	public int getStatus() {
		return status;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void stop() {
		locationManager.removeUpdates(locationListener);
	}
	
    private class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location loc) {
        	// Called when the location has changed.
        	if (loc != null) {
        		location = loc;
        	}
        }

        public void onProviderDisabled(String provider) {
        	// Called when the provider is disabled by the user.
        }

        public void onProviderEnabled(String provider) {
        	// Called when the provider is enabled by the user.
        }

        public void onStatusChanged(String provider, int s, Bundle extras) {
        	// Called when the provider status changes.
        	status = s;
        }
    }
}
