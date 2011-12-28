/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.accursedware.bartleby.geocoding.AsyncGeocoder;
import com.accursedware.bartleby.geocoding.AsyncReverseGeocoderListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * An {@link Overlay} that handles fall-through taps by adding an item to
 * {@link PropertyOverlay} once an address is found, or displaying a message
 * if the point can't be linked to an address.  It also displays a progress
 * message while geocoding.
 * @author talos
 *
 */
class FallThroughOverlay extends Overlay {

	private final AsyncGeocoder geocoder;
	private ProgressDialog dialog;
	private PropertyOverlay overlay;

	/**
	 * Always cancel the last search when {@link #dialog} is cancelled.
	 */
	private final DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			geocoder.cancelLastSearch();
		}
		
	};
	
	/**
	 * This {@link AsyncReverseGeocoderListener} will listen for the completion
	 * of reverse geocodes, and throw addresses as properties on the map.
	 */
	private final AsyncReverseGeocoderListener listener;
	
	public FallThroughOverlay(final Activity activity,
			AsyncGeocoder geocoder,
			final MapController mapController,
			final AddressSearchView searchView,
			final PropertyOverlay overlay) {
		this.geocoder = geocoder;
		
		this.dialog = ProgressDialog.show(activity, "", activity.getString(R.string.reverse_geocoding), true, true, cancelListener);			
		this.dialog.dismiss();
		this.overlay = overlay;
		
		//this.dialog.ca
		this.listener = new AsyncReverseGeocoderListener() {
			
			/**
			 * Display message if no addresses can be found at clicked point.
			 */
			@Override
			public void onNoAddressesFound(final GeoPoint point) {
				dialog.dismiss();
				Toasts.showNoAddressFound(activity, point);
			}
			
			
			@Override
			public void onFound(final GeoPoint point, final BartlebyAddress address) {
				dialog.dismiss();
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
				dialog.dismiss();
				Toasts.showGeocoderError(activity);
				e.printStackTrace();
			}


			@Override
			public void onCancel() {
				dialog.dismiss();
			}
		};
	}
	
	/**
	 * Hit {@link #geocoder} with the point the user tapped if there is no balloon open,
	 * hide the open balloon otherwise.
	 */
	@Override
	public boolean onTap(GeoPoint gp, MapView mapView) {
		/*if(overlay.getFocus() != null) {
			Property property = overlay.getFocus();
		}*/
		
		// if a ballon is showing, hide it and unfocus
		if(overlay.getFocus() != null) {
			overlay.hideBalloon();
			overlay.setFocus(null);
		} else { // if no ballon is showing, do a lookup
			geocoder.lookup(gp.getLatitudeE6() /1E6, gp.getLongitudeE6() / 1E6, listener);
			dialog.show();
		}
		return true;
	}
}
