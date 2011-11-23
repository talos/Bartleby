/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.location.Address;

/**
 * This {@link BartlebyException} is thrown when a {@link BartlebyAddress} cannot
 * be constructed from an {@link Address}. 
 * @author talos
 *
 */
class BartlebyAddressException extends BartlebyException {
	
	/**
	 * 
	 * @param address The {@link Address} that could not be turned into a four-part address.
	 */
	public BartlebyAddressException(Address address) {
		super(address.toString() + " did not have an extractable number, street and zip.");
	}
}
