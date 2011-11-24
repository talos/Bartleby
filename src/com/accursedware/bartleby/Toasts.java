/**
 * Bartleby Android Dev
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import com.google.android.maps.GeoPoint;

import android.content.Context;
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
	 * @param context
	 * @param locationName The entered {@link String} location name.
	 */
	static void showNoAddressesFound(Context context, String locationName) {
		showToast(context, context.getString(R.string.no_addresses_for_query, locationName));
	}

	/**
	 * Tell the user that there was no address found for the <code>
	 * point</code> they entered.
	 * @param context
	 * @param geoPoint The entered {@link GeoPoint}.
	 */
	static void showNoAddressFound(Context context, GeoPoint point) {
		showToast(context, context.getString(R.string.no_address_for_point));
	}
	
	/**
	 * Tell the user that the geocoder is busted up.
	 * @param activity
	 */
	static void showGeocoderError(Context context) {
		showToast(context, context.getString(R.string.no_geocoder));
	}
	
	private static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
