/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.location.Address;
import android.util.Log;
import android.widget.TextView;

import com.accursedware.bartleby.util.AddressUtils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/**
 * This listener will pan the map to the first address,
 * replace the contents of the text view with the address,
 * and put a marker on the map.
 * @author talos
 *
 */
class GoToLocationGeocoderListener implements BartlebyGeocoderListener {
	private final Activity activity;
	private final MapController mapController;
	private final AutoCompleteAddressTextView tv;
	private final BartlebyItemOverlay overlay;
	
	/**
	 * @param bartlebyGoButton
	 */
	GoToLocationGeocoderListener(Activity activity, MapView mapView,
			AutoCompleteAddressTextView tv, BartlebyItemOverlay overlay) {
		this.activity = activity;
		this.mapController = mapView.getController();
		this.tv = tv;
		this.overlay = overlay;
	}

	@Override
	public void onFound(List<Address> addresses) {
		if(addresses.size() > 0) {
			// look for an address with actual lon/lat, break out when one is
			// hit
			for(final Address address : addresses) {
				if(address.hasLatitude() && address.hasLongitude()) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							GeoPoint gp = new GeoPoint(
									(int) (address.getLatitude() * 1E6),
									(int) (address.getLongitude() * 1E6));
							mapController.animateTo(gp);
							tv.setText(AddressUtils.compressAddress(address));
							try {
								overlay.addItem(gp, new ThreePartAddress(address));
							} catch(InvalidThreePartAddressException e) {
								Log.i("bartleby", "invalid address: " + address.toString());
							}
						}
					});
					return;
				}
			}
		}
		Log.i(activity.getString(R.string.app_name), "No valid addresses found");
	}

	@Override
	public void onError(IOException e) {
		e.printStackTrace();
	}	
}