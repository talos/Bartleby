/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.io.IOException;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.invisiblearchitecture.bartleby.util.AddressUtils;

import android.app.Activity;
import android.location.Address;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author talos
 *
 */
class BartlebyGoButton {
	
	private final Activity activity;
	private final Button button;
	private final BartlebyGeocoder geocoder;
	private final AutoCompleteAddressTextView tv;
	private final BartlebyGeocoderListener geocoderListener = new GoButtonGeocoderListener();
	private final MapController mapController;
	
	public BartlebyGoButton(Activity activity,
			Button button, BartlebyGeocoder geocoder,
			MapView mapView,
			AutoCompleteAddressTextView tv) {
		this.activity = activity;
		this.button = button;
		this.geocoder = geocoder;
		this.mapController = mapView.getController();
		this.tv = tv;
		this.button.setOnClickListener(new GoButtonClickListener());
	}

	/**
	 * This listener will do a lookup for the text of {@link AutoCompleteAddressTextView}
	 * when clicked.
	 * @author talos
	 *
	 */
	private class GoButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			geocoder.lookup(tv.getText(), geocoderListener);
		}
	}
	
	/**
	 * This listener will pan the map to the first address,
	 * and replace the contents of the text view with the address.
	 * @author talos
	 *
	 */
	private class GoButtonGeocoderListener implements BartlebyGeocoderListener {
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
									Log.i("bartleby", new FourPartAddress(address).toString());
								} catch(InvalidAddressException e) {
									Log.i("bartleby", "invalid address");
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
}
