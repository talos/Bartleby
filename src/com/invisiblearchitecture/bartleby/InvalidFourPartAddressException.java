/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import android.location.Address;

/**
 * This {@link BartlebyException} is thrown when a {@link ThreePartAddress} cannot
 * be constructed from an {@link Address}. 
 * @author talos
 *
 */
class InvalidFourPartAddressException extends BartlebyException {
	
	/**
	 * 
	 * @param address The {@link Address} that could not be turned into a four-part address.
	 */
	public InvalidFourPartAddressException(Address address) {
		super(address.toString() + " did not have an extractable number, street, city, and zip.");
	}
}
