/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.HashMap;
import java.util.Map;

/**
 * @author realest
 *
 */
public interface UniversalAddress {
	// Default mappings to fields in Information.
	public static final String STREET_NUMBER = "Street Number";
	public static final String STREET_NAME = "Street Name";
	public static final String STREET_DIR = "Street Direction";
	public static final String STREET_SUFFIX = "Street Suffix";
	public static final String CITY = "City";
	public static final String ZIP = "Zip";
	
	public String streetNumber();
	public String streetDir();
	public String streetName();
	public String streetSuffix();
	public String streetNameFull();	
	public String zip();
	public String city();
	
	public Map<String, String> toMap();
	
	public static class Mapping {
		public static Map<String, String> get(UniversalAddress address) {
			Map<String, String> map = new HashMap<String, String>(7,1);
			map.put(STREET_NUMBER, address.streetNumber());
			map.put(STREET_NAME, address.streetName());
			map.put(STREET_DIR, address.streetDir());
			map.put(STREET_SUFFIX, address.streetSuffix());
			map.put(CITY, address.city());
			map.put(ZIP, address.zip());
			return map;
		}
	}
	
	/**
	 * Used for establishing equality, mainly.
	 */
	public String toString();
	
	public boolean equals(UniversalAddress anotherAddress);
	public boolean equals(String addressLine1, String city);
	
	/**
	 * BadAddressException is thrown when somehow, somewhere, we realize that the address we've reverse-geocoded
	 * is no good.  At that point, we need to come up with other options.  The Exception carries with it the UniversalAddress
	 * that caused the problem.
	 * @author john
	 *
	 */
	public static final class BadAddressException extends Throwable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1357986540234099126L;
		
		public final UniversalAddress badAddress;
		public BadAddressException(UniversalAddress ba) {
			badAddress = ba;
		}
	}

	/**
	 * @return
	 */
	public double getLatitude();

	/**
	 * @return
	 */
	public double getLongitude();
}
