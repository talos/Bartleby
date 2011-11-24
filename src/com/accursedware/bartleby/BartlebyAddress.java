/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.location.Address;
import android.util.Log;

/**
 * An address with a street number,
 * a street name, ZIP code, and lat/lon.  It overrides {@link #equals(Object)}
 * and {@link #hashCode()}.
 * @author talos
 *
 */

public final class BartlebyAddress {
	public final String number;
	public final String street;
	public final String zip;
	
	private final GeoPoint geoPoint;
	private final String asString;
	
	/**
	 * Optional, only used in {@link #toString()}
	 */
	//private final String city;

	public BartlebyAddress(Address address) throws BartlebyAddressException {
		
		// To create a BartlebyAddress, we must have a thoroughfare, postal code,
		// and valid point on map.
		if(address.getThoroughfare() == null || 
				address.getPostalCode() == null ||
				!address.hasLatitude() || 
				!address.hasLongitude()) {
			throw new BartlebyAddressException(address);
		}
		
		// Calculate the string representation
		StringBuilder sb = new StringBuilder();
		int numLines = address.getMaxAddressLineIndex();
		for(int i = 0 ; i < numLines ; i++) {
			sb.append(address.getAddressLine(i)).append(", ");
		}
		asString = sb.substring(0, sb.length() - 2); // cut out trailing comma
		
		this.geoPoint = new GeoPoint((int) (address.getLatitude() * 1E6),
									(int) (address.getLongitude() * 1E6));
		
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
					throw new BartlebyAddressException(address);
				}
			}
		}
		number = tentativeNumber;
	}
	
	/**
	 * Two {@link BartlebyAddress}es are equal if {@link Object#equals(Object)} is <code>true</code>
	 * for {@link #number}, {@link #street} and {@link #zip}.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		} else if(obj instanceof BartlebyAddress) {
			BartlebyAddress that = (BartlebyAddress) obj;
			if(that.number.equals(number)
					&& that.street.equals(street)
					&& that.zip.equals(zip)
					&& that.geoPoint.getLatitudeE6() == geoPoint.getLatitudeE6()
					&& that.geoPoint.getLongitudeE6() == geoPoint.getLongitudeE6()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return number.hashCode() + street.hashCode() + zip.hashCode();
	}
	
	@Override
	public String toString() {
		return asString;
	}
	
	/**
	 * 
	 * @return A {@link GeoPoint} locating this {@link BartlebyAddress}.
	 */
	public final GeoPoint getGeoPoint() {
		return geoPoint;
	}
	
	/**
	 * 
	 * @param addresses A {@link List} of {@link Address}es.  Can be <code>null</code>,
	 * in which case an empty list is returned.
	 * @return A {@link List} of {@link BartlebyAddress} from the supplied <code>addresses</code>.
	 * Could be shorter than the supplied list, as only {@link Address}es that could be parsed
	 * are included.
	 */
	public static List<BartlebyAddress> fromAddressArray(List<Address> addresses) {
		if(addresses == null) {
			return Collections.emptyList();
		} else {
			List<BartlebyAddress> bAddresses = new ArrayList<BartlebyAddress>();
			for(Address address : addresses) {
				// if valid bartlebyaddress can be generated, add it.
				try {
					bAddresses.add(new BartlebyAddress(address));
				} catch(BartlebyAddressException e) {
					// couldn't convert, don't add to return list.
				}
			}
			return bAddresses;
		}
	}
}
