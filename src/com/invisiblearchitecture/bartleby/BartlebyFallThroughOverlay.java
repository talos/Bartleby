/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * An {@link Overlay} that handles fall-through taps by adding an item to
 * {@link BartlebyItemOverlay} once an address is found.
 * @author talos
 *
 */
class BartlebyFallThroughOverlay extends Overlay {

	private final BartlebyGeocoder geocoder;
	private final GoToLocationGeocoderListener listener;
	
	public BartlebyFallThroughOverlay(
			BartlebyGeocoder geocoder, GoToLocationGeocoderListener listener) {
		this.listener = listener;
		this.geocoder = geocoder;
	}
	
	@Override
	public boolean onTap(GeoPoint gp, MapView mapView) {
		Log.i("Bartleby", "on tap for below");
		
		geocoder.lookup(gp.getLatitudeE6() /1E6, gp.getLongitudeE6() / 1E6, listener);
		
		return true;
	}
}
