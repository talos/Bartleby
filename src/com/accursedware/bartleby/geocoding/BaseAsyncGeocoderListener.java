/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby.geocoding;

import java.io.IOException;

/**
 * All geocoder listeners can be cancelled or have errors.
 * @author talos
 *
 */
interface BaseAsyncGeocoderListener {

	/**
	 * This is called when the reverse lookup is cancelled.
	 */
	abstract void onCancel();
	
	/**
	 * This is called when an {@link IOException} has occurred.
	 * @param e
	 */
	abstract void onError(IOException e);
}
