/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import android.location.Address;

/**
 * A three part address contains just a street number,
 * a street name and a ZIP code.
 * @author talos
 *
 */

class ThreePartAddress {
	public final String number;
	public final String street;
	public final String zip;
	
	public ThreePartAddress(Address address) throws InvalidFourPartAddressException {
		if(address.getThoroughfare() == null || 
				address.getPostalCode() == null ) {
			throw new InvalidFourPartAddressException(address);
		}
		
		// get rid of apt numbers
		street = address.getThoroughfare().split("#")[0].trim();
		zip = address.getPostalCode();
		
		// Find the street number by looking through all the address
		// lines for the street name, and then excluding it.
		String tentativeNumber = "";
		int maxLine = address.getMaxAddressLineIndex();
		for(int i = 0 ; i < maxLine ; i ++) {
			String addressLine = address.getAddressLine(i);
			if(addressLine.contains(street)) {
				tentativeNumber = addressLine.split(street)[0].trim();
				break;
			}
		}
		number = tentativeNumber;
	}
}
