/**
 * Bartleby Android Dev
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby.geocoding;

import java.io.IOException;

import com.accursedware.bartleby.BartlebyAddress;
import com.google.android.maps.GeoPoint;

/**
	* Listener called by {@link AsyncGeocoder}. This receives
	* notices whenever a reverse geocode (point -> address) finishes, or
	* an exception occurs while looking up a location.
	* @author talos
	**/
public interface AsyncReverseGeocoderListener {

	/**
	 * This is called when a single address has been found looking for a point.
	 * @param locationName
	 * @param addresses
	 */
	public abstract void onFound(GeoPoint point, BartlebyAddress address);
	
	/**
	 * This is called when no addresses are found looking for <code>
	 * point</code>.
	 * @param point The {@link GeoPoint} for which no locations
	 * were found.
	 */
	public abstract void onNoAddressesFound(GeoPoint point);

	/**
	 * This is called when an {@link IOException} has occurred.
	 * @param e
	 */
	public abstract void onError(IOException e);
}
