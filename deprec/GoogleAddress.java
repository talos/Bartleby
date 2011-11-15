/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.io.IOException;
import java.util.Map;

import com.google.android.maps.GeoPoint;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public class GoogleAddress {
	private Address address; // Restrict access to the address.
	private final String streetNum;
	private final String streetDir;
	private final String streetName;
	private final String streetSuffix;
	private final String city;
	private final String zip;
	
	public GoogleAddress(Address a) {
		address = a;
		
		String[] splitString = Utils.addressLineSplitter(address.getAddressLine(0));
		streetNum = splitString[0];
		streetDir = splitString[1];
		streetName = splitString[2];
		streetSuffix = splitString[3];
		
		if(address.getLocality() == null) {
			city = address.getAddressLine(1).split(",")[0];
		} else {
			city = address.getLocality();
		}
		zip = address.getPostalCode();
	}
	
	/**
	 * Class to call a geocoder asynchronously to get a new UniversalAddress from an address string,
	 * which would probably be a concatenation of street number, street name, and city.
	 */
	public static abstract class ViaGeocoder extends AsyncTask<Void, Integer, UniversalAddress> {
		private final Geocoder geocoder;
		/**
		 * The number of times UniversalAddress will call the Geocoder.  It only re-calls if the
		 * geocoder fails.  Set at the class level.
		 */
		static int numGeocodeAttempts = 4;
		/**
		 * The delay between calls of UniversalAddress to the Geocoder.  It only re-calls if the 
		 * geocoder fails.  Set at the class level.
		 */
		static int geocodeAttemptDelay = 500;
		static final int GEOCODE = 1;
		static final int REVERSE_GEOCODE = 2;
		private final int task;
		private final String addressString;
		private final GeoPoint geoPoint;
		
		/**
		 * Constructor to get a UniversalAddress from a concatenated address string, most likely
		 * street number + street name + city.
		 */
		public ViaGeocoder(Geocoder g, String streetNum, String streetName, String city) {
			geocoder = g;
			task = GEOCODE;
			addressString = streetNum + " " + streetName + ", " + city;
			geoPoint = null;
		}
		
		/**
		 * Constructor to get a UniversalAddress from a geoPoint.
		 */
		public ViaGeocoder(Geocoder g, GeoPoint gp) {
			geocoder = g;
			task = REVERSE_GEOCODE;
			addressString = null;
			geoPoint = gp;
		}
		
		/**
		 * This handles one of the two geocoding tasks assigned at instantiation.
		 * Will attempt a certain number of times (set at the class level.)  If there
		 * is an interruption during these attempts, it must be handled by handleCancelled().
		 */
		@Override
		protected UniversalAddress doInBackground(Void... nothing) {
			UniversalAddress universalAddress = null;
			for(int i = 0; i < numGeocodeAttempts; i++) {
				try {
					
					switch(task) {
						case GEOCODE:
							universalAddress = new GoogleAddress(geocoder.getFromLocationName(addressString, 1).get(0));
							break;
						case REVERSE_GEOCODE:
							universalAddress = new GoogleAddress(geocoder.getFromLocation(
									((double) geoPoint.getLatitudeE6()) /1E6, ((double) geoPoint.getLongitudeE6()) /1E6, 1).get(0));
							break;
					}
					if(universalAddress != null) {
						return universalAddress;
					}
				} catch (IOException e) { e.printStackTrace();
				} catch (NullPointerException e) { e.printStackTrace();
				} catch (IndexOutOfBoundsException e) { e.printStackTrace(); }
				
				// Cancel if the sleep is interrupted.
				try {
					Thread.sleep(geocodeAttemptDelay);
				} catch (InterruptedException e) {
					cancel(true);
				}
			}
			return universalAddress; // May return NULL.
		}
		
		@Override
		/**
		 * ViaGeocoder can only be run if its subclass handles the onPostExecute.
		 */
		public void onPostExecute(UniversalAddress address) {
			if(isCancelled()) {
				handleCancelled();
			} else if(address == null) {
				handleNull();
			} else {
				try {
					handleAddress(address);
				} catch(NoInformationType e) {
				//TODO: handle no information type.
					handleNoInformationType(address);
				}
			}
		}
		
		/**
		 * Must be implemented by subclasses: what to do after a cancellation.
		 */
		protected abstract void handleCancelled();
		/**
		 * Must be implemented by subclasses: what to do after a totally null result set.
		 */
		protected abstract void handleNull();
		/**
		 * Must be implemented by subclasses: what to do after successfully receiving the address.
		 */
		protected abstract void handleAddress(UniversalAddress address) throws NoInformationType;
		/**
		 * Must be implemented by subclasses: What to do if the address does not match to an existing property type.
		 */
		protected abstract void handleNoInformationType(UniversalAddress address);
		
		protected class NoInformationType extends Throwable {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
		}
	}
	
	@Override
	public final String streetDir() {
		return streetDir;
	}
	
	@Override
	public final String streetName() {
		return streetName;
	}
	
	@Override
	public final String streetSuffix() {
		return streetSuffix;
	}
	
	@Override
	public String city() {
		return city;
	}
	
	@Override
	/**
	 * Used for establishing equality, mainly.
	 */
	public final String toString() {
		return streetNum + " " + streetDir + " " + streetName + " " + streetDir +  ", " + city;
	}
	
	@Override
	public boolean equals(UniversalAddress anotherAddress) {
		// If it talks like a duck...
		if(toString().equals(anotherAddress.toString())) {
			return true;
		// Or if walks like a duck.
		} else if(address.getLongitude() == anotherAddress.getLongitude() &&
				address.getLatitude() == anotherAddress.getLatitude()) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	/**
	 * Test this address against a streetnum/streetname/city
	 * @param addressString
	 * @return
	 */
	public boolean equals(String addressLine1, String a_city) {
		String[] splitAddress = Utils.addressLineSplitter(addressLine1);
		if(streetNum.equals(splitAddress[0]) &&
				streetDir.equals(splitAddress[1]) &&
				streetName.equals(splitAddress[2]) &&
				streetSuffix.equals(splitAddress[3]) &&
				city.equals(a_city)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return
	 */
	public double getLatitude() {
		return address.getLatitude();
	}

	/**
	 * @return
	 */
	public double getLongitude() {
		return address.getLongitude();
	}

	/* (non-Javadoc)
	 * @see com.invisiblearchitecture.geogrape.lib.UniversalAddress#streetNumber()
	 */
	@Override
	public String streetNumber() {
		return streetNum;
	}

	/* (non-Javadoc)
	 * @see com.invisiblearchitecture.geogrape.lib.UniversalAddress#streetNameFull()
	 */
	@Override
	public String streetNameFull() {
		return streetDir + ' ' + streetName + ' ' + streetSuffix;
	}

	/* (non-Javadoc)
	 * @see com.invisiblearchitecture.geogrape.lib.UniversalAddress#zip()
	 */
	@Override
	public String zip() {
		return zip;
	}

	/* (non-Javadoc)
	 * @see com.invisiblearchitecture.geogrape.lib.UniversalAddress#toHashtable()
	 */
	@Override
	public Map<String, String> toMap() {
		return UniversalAddress.Mapping.get(this);
	}
}
