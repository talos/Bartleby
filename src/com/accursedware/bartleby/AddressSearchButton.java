/**
 * Bartleby Android Dev
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;
import java.util.List;

import com.accursedware.bartleby.geocoding.AsyncGeocoder;
import com.accursedware.bartleby.geocoding.AsyncGeocoderListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * When this button is clicked, the {@link AsyncGeocoder} looks up the current address in {@link AddressSearchView},
 * and creates the item & pans the map if a single one exists.  Otherwise, an error {@link Toast} is shown.
 * @author talos
 *
 */
class AddressSearchButton {
	
	AddressSearchButton(final Activity activity, final AsyncGeocoder geocoder, View view,
			final AddressSearchView searchView, final MapView mapView, final PropertyOverlay overlay) {
		
		final MapController controller = mapView.getController();
		
		//TODO this is redundant with the listener in {@link AddressSearchView}.
		final AsyncGeocoderListener listener = new AsyncGeocoderListener() {
			
			@Override
			public void onNoAddressesFound(String locationName) {
				Toast.makeText(activity, activity.getString(R.string.no_addresses_for_query, locationName), Toast.LENGTH_SHORT);
			}
			
			/**
			 * Jump
			 */
			@Override
			public void onFound(String locationName, final List<BartlebyAddress> addresses) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						BartlebyAddress address = addresses.get(0);
						controller.animateTo(address.getGeoPoint());
						overlay.addItem(address);
					}
				});
			}
			
			@Override
			public void onError(IOException e) {
				e.printStackTrace();
			}
		};
		
		// hit the geocoder when clicked.
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				geocoder.lookup(searchView.getText(), listener, mapView);
			}
		});
	}
}
