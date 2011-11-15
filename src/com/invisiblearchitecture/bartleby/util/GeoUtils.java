/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby.util;

import com.google.android.maps.GeoPoint;

import android.location.Location;

/**
 * Util functions for dealing with {@link GeoPoint} and {@link Location}
 * @author talos
 *
 */
public class GeoUtils {
	
	private static final String PROVIDER = "synthetic";
	/**
	 * {@link GeoPoint} approximating NYC.  Real lat/long is:
	 */
	//public static final GeoPoint NYC = new GeoPoint(40627307, -73968200);
	
	/**
	 * {@link Location} approximating NYC.<p>
	 * 40.627307916989615 / -73.96820068359375
	 */
	public static final Location NYC = locationFromLatLon(40.627307916989615, -73.96820068359375);
	
	/**
	 * Obtain a {@link GeoPoint} from a {@link Location}.
	 * @param location
	 * @return
	 */
	public static GeoPoint geoPointFromLocation(Location location) {
		return new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() *1E6));
	}
	
	/**
	 * Obtain a {@link Location} from a latitude and longitude.
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static Location locationFromLatLon(double lat, double lon) {
		Location location = new Location(PROVIDER);
		location.setLongitude(lon);
		location.setLatitude(lat);
		
		return location;
	}
}
