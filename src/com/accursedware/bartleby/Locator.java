/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Call {@link #locate()} on this class to try and track down the user's current location,
 * and pan the map to that point.
 * @author talos
 *
 */
class Locator {
	private final int zoomToLevel;
	private final LocationManager lm;
	private final MapController controller;
	
	/**
	 * @param controller The {@link MapController} to pan and zoom with.
	 */
	Locator(Context context, MapController controller) {
		this.lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.controller = controller;
		this.zoomToLevel = context.getResources().getInteger(R.integer.zoom_to_level);
	}
	
	/**
	 * Try to find where we are.  Asynchronous pans map using {@link #controller} when
	 * there is a location update.
	 * @throws NoLocationProvidersException if there are no location providers available.
	 */
	void locate() throws NoLocationProvidersException {
		boolean gpsEnabled = false;
		boolean networkEnabled = false;
		
		LocationListener listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) { }
			
			@Override
			public void onProviderEnabled(String provider) { }
			
			@Override
			public void onProviderDisabled(String provider) { }
			
			/**
			 * Pan and zoom the map, and immediately stop listening.
			 */
			@Override
			public void onLocationChanged(Location location) { 
				controller.animateTo(new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() *1E6)));
				controller.setZoom(zoomToLevel);
				lm.removeUpdates(this);
			}
		};

		// Prefer the use of GPS.
		try {
			if(gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
			}
		} catch(SecurityException e) {
			// we can safely ignore this.
		}
		
		// Fall back to use of network.
		try {
			if(networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
			}
		} catch(SecurityException e){
			// we can safely ignore this.
		}
		
		if(!gpsEnabled && !networkEnabled) {
			throw new NoLocationProvidersException();
		}
	}
}



/*
if(Build.BRAND.equals("generic")) {
	emulation = true;
} else {
	emulation = false;
}*/
/*
if(emulation == true) {
	//listener.onLocationChanged(GeoUtils.NYC);
	throw new NoLocationProvidersException();
} else {
	//don't start listeners if no provider is enabled
	if(!gpsEnabled && !networkEnabled) {
		throw new NoLocationProvidersException();
	}
	if(gpsEnabled) {
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
	}
	if(networkEnabled) {
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
	}
}*/