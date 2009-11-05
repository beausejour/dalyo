package com.penbase.dma.Dalyo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * Object gets GPS informations
 */
public class Gps {
	private static final String sProviderName = "gps";
	private LocationManager mLocationManager;
	private Location mLocation = null;
	private LocationListener mLocationListener;
	private int mStatus = LocationProvider.OUT_OF_SERVICE;

	public Gps(Context context) {
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10000, 10, mLocationListener);
		mLocation = mLocationManager.getLastKnownLocation(sProviderName);
	}

	public static Location createLocation(double latitude, double longitude,
			double altitude, double speed) {
		Location result = new Location(sProviderName);
		result.setLatitude(latitude);
		result.setLongitude(longitude);
		result.setAltitude(altitude);
		result.setSpeed((float) speed);
		return result;
	}

	public int getStatus() {
		return mStatus;
	}

	public Location getLocation() {
		return mLocation;
	}

	public void stop() {
		mLocationManager.removeUpdates(mLocationListener);
		mStatus = LocationProvider.OUT_OF_SERVICE;
	}

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			// Called when the location has changed.
			if (loc != null) {
				mLocation = loc;
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
			mStatus = s;
		}
	}
}
