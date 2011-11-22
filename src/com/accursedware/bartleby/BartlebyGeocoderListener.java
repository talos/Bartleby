/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;
import java.util.List;

import android.location.Address;

/**
 * Listener called by {@link BartlebyGeocoder}. This receives
 * notices whenever a location lookup callback finishes, or
 * an exception occurs while looking up a location.
 * @author talos
 *
 */
interface BartlebyGeocoderListener {

	/**
	 * This is called when a series of addresses have been found.
	 * @param addresses
	 */
	public abstract void onFound(List<Address> addresses);
	
	/**
	 * This is called when an {@link IOException} has occurred.
	 * @param e
	 */
	public abstract void onError(IOException e);
}
