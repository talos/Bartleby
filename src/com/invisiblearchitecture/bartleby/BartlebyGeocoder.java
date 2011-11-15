/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import android.content.Context;
import android.location.Geocoder;

/**
 * Nonblocking {@link Geocoder} using {@link BartlebyGeocoderListener}.
 * @author talos
 *
 */
public class BartlebyGeocoder {
	
	private final Geocoder geocoder;
	private final BartlebyGeocoderListener listener;
	public BartlebyGeocoder(Context context, BartlebyGeocoderListener listener) {
		this.geocoder = new Geocoder(context);
		this.listener = listener;
		
		//geocoder.getFromLocationName(locationName, maxResults)
		
	}
	
	public void lookup(String locationName) {
		//geocoder.
	}
	
	public void lookup(int latitude, int longitude) {
		geocoder.getFromLocation(latitude, longitude, 1);
	}
}
