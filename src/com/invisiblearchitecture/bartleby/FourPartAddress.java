/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import android.location.Address;

/**
 * A four part address contains just a street number,
 * a street name, a city, and a ZIP code.
 * @author talos
 *
 */

class FourPartAddress {
	public final String number;
	public final String street;
	public final String city;
	public final String zip;
	
	public FourPartAddress(Address address) throws InvalidAddressException {
		if(address.getThoroughfare() == null || 
				address.getLocality() == null ||
				address.getPostalCode() == null ) {
			throw new InvalidAddressException();
		}
		
		// get rid of apt numbers
		street = address.getThoroughfare().split("#")[0].trim();
		city = address.getLocality();
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
	
	public String toString() {
		return number + " " + street + ", " + city + ", " + zip;
	}
}
