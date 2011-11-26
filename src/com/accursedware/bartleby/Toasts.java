/**
 * Bartleby Android Dev
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.widget.Toast;

/**
 * Static methods to display {@link Toast}s.
 * @author talos
 *
 */
class Toasts {
	
	/**
	 * Tell the user that there were no addresses found for the <code>
	 * locationName</code> they entered.
	 * @param activity
	 * @param locationName The entered {@link String} location name.
	 */
	static void showNoAddressesFound(Activity activity, String locationName) {
		showToast(activity, activity.getString(R.string.no_addresses_for_query, locationName));
	}

	/**
	 * Tell the user that there was no address found for the <code>
	 * point</code> they entered.
	 * @param activity
	 * @param geoPoint The entered {@link GeoPoint}.
	 */
	static void showNoAddressFound(Activity activity, GeoPoint point) {
		showToast(activity, activity.getString(R.string.no_address_for_point));
	}
	
	/**
	 * Tell the user that the geocoder is busted up.
	 * @param activity
	 */
	static void showGeocoderError(Activity activity) {
		showToast(activity, activity.getString(R.string.no_geocoder));
	}
	
	/**
	 * The the user that there's no server to get templates from.
	 * @param activity
	 */
	static void showNoServerError(Activity activity) {
		showToast(activity, activity.getString(R.string.no_server));
	}
	
	private static void showToast(final Activity activity, final String message) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
			};
		});
	}
}
