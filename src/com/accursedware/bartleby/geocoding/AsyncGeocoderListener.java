/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby.geocoding;

import java.io.IOException;
import java.util.List;

import com.accursedware.bartleby.BartlebyAddress;

/**
 * Listener called by {@link AsyncGeocoder}. This receives
 * notices whenever a geocoding (name -> addresses) lookup finishes, or
 * an exception occurs while geocoding.
 * @author talos
 *
 */
public interface AsyncGeocoderListener {

	/**
	 * This is called when a series of addresses have been found.
	 * @param locationName
	 * @param addresses
	 */
	public abstract void onFound(String locationName, List<BartlebyAddress> addresses);
	
	/**
	 * This is called when no addresses are found looking for <code>
	 * locationName</code>.
	 * @param locationName The {@link String} name for which no addresses
	 * were found.
	 */
	public abstract void onNoAddressesFound(String locationName);

	/**
	 * This is called when an {@link IOException} has occurred.
	 * @param e
	 */
	public abstract void onError(IOException e);
}
