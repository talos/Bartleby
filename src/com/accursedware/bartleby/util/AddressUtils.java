/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby.util;

import android.location.Address;

/**
 * @author talos
 *
 */
public class AddressUtils {

	/**
	 * Convert a multiple line address into a single-line string,
	 * separated by commas.
	 * @param address
	 * @return A compressed {@link String}, which is blank if the
	 * address had no significatn data.
	 */
	public static String compressAddress(Address address) {		
		// build up the address

		int maxLine = address.getMaxAddressLineIndex();

		StringBuilder sb = new StringBuilder();
		
		for(int i = 0 ; i < maxLine ; i ++) {
			sb.append(address.getAddressLine(i)).append(", ");
		}
		
		if(sb.length() > 2) {
			return sb.substring(0, sb.length() - 2);
		} else {
			return "";
		}
	}
}
