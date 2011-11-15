/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.invisiblearchitecture.bartleby.util.GeoUtils;

/**
 * Listener to move the map to a location.
 * @author talos
 *
 */
class BartlebyLocationListener implements LocationListener {
	
	private static final int CLOSE_ENOUGH = 16;
	private final MapController controller;
	private final BartlebyLocator locator;
	
	BartlebyLocationListener(MapView map, BartlebyLocator locator) {
		this.controller = map.getController();
		this.locator = locator;
	}

	/**
	 * Pan the map when location changes.
	 */
	@Override
	public void onLocationChanged(Location location) {
		controller.animateTo(GeoUtils.geoPointFromLocation(location));
		controller.setZoom(CLOSE_ENOUGH);
		locator.finished();
		/*
    	timer1.cancel();
        locationResult.gotLocation(location);
        lm.removeUpdates(this);
        lm.removeUpdates(locationListenerNetwork);
        */
	}
	
	@Override
	public void onProviderDisabled(String provider) { }
	
	@Override
	public void onProviderEnabled(String provider) { }
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }
	
}
