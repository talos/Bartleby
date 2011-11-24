/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.accursedware.bartleby.geocoding.AsyncGeocoder;
import com.accursedware.bartleby.geocoding.AsyncReverseGeocoderListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * An {@link Overlay} that handles fall-through taps by adding an item to
 * {@link PropertyOverlay} once an address is found, or displaying a message
 * if the point can't be linked to an address.
 * @author talos
 *
 */
class FallThroughOverlay extends Overlay {

	private final AsyncGeocoder geocoder;
	private final AsyncReverseGeocoderListener listener;
	
	public FallThroughOverlay(final Activity activity,
			AsyncGeocoder geocoder,
			final MapController mapController,
			final AddressSearchView searchView,
			final PropertyOverlay overlay) {
		this.geocoder = geocoder;
		this.listener = new AsyncReverseGeocoderListener() {
			
			/**
			 * Display message if no addresses can be found at clicked point.
			 */
			@Override
			public void onNoAddressesFound(final GeoPoint point) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toasts.showNoAddressFound(activity, point);
					}
				});
			}
			
			
			@Override
			public void onFound(final GeoPoint point, final BartlebyAddress address) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						mapController.animateTo(point);
						searchView.setText(address);
						overlay.addItem(address);
					}
				});
			}
			
			@Override
			public void onError(IOException e) {
				e.printStackTrace();
				Toasts.showGeocoderError(activity);
			}
		};
	}
	
	@Override
	public boolean onTap(GeoPoint gp, MapView mapView) {		
		geocoder.lookup(gp.getLatitudeE6() /1E6, gp.getLongitudeE6() / 1E6, listener);
		
		return true;
	}
}
