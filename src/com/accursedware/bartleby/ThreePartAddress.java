/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.location.Address;

/**
 * A three part address contains just a street number,
 * a street name and a ZIP code.  It overrides {@link #equals(Object)}
 * and {@link #hashCode()}.
 * @author talos
 *
 */

final class ThreePartAddress {
	public final String number;
	public final String street;
	public final String zip;
	
	public ThreePartAddress(Address address) throws InvalidThreePartAddressException {
		if(address.getThoroughfare() == null || 
				address.getPostalCode() == null ) {
			throw new InvalidThreePartAddressException(address);
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
				String[] splitAddressLine = addressLine.split(street);
				if(splitAddressLine.length > 0) {
					tentativeNumber = addressLine.split(street)[0].trim();
					break;
				} else {
					throw new InvalidThreePartAddressException(address);
				}
			}
		}
		number = tentativeNumber;
	}
	
	/**
	 * Two {@link ThreePartAddress}es are equal if {@link Object#equals(Object)} is <code>true</code>
	 * for {@link #number}, {@link #street} and {@link #zip}.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		} else if(obj instanceof ThreePartAddress) {
			ThreePartAddress that = (ThreePartAddress) obj;
			if(that.number.equals(number) && that.street.equals(street) && that.zip.equals(zip)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return number.hashCode() + street.hashCode() + zip.hashCode();
	}
}
