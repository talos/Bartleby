/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

/**
 * Nonblocking {@link Geocoder} using {@link BartlebyGeocoderListener}.
 * Uses a single worker thread, which is terminated if any additional
 * lookups are done before it finishes.
 * @author talos
 *
 */
final class BartlebyGeocoder {
	/**
	 * The radius in microdegrees (degrees * 1E6) within which a lookup
	 * by name will search.  The center
	 * is established by the center of the passed map.  1 degree latitude
	 * is approximately 69 miles; 1 degree longitude varies from 0 to
	 * approximately 69 miles.
	 */
	private static double SEARCH_RADIUS = .1; // 1/10 degree each way approx. 14 miles
	
	/**
	 * Max number of address results to retrieve.
	 */
	private static final int MAX_RESULTS = 4;
	private final Context context;
	private final Geocoder geocoder;
	private final MapView mapView;
	private final ExecutorService worker = Executors.newSingleThreadExecutor();
	private Future<?> lastSearch;
	
	public BartlebyGeocoder(Context context, MapView mapView) {
		this.context = context;
		this.geocoder = new Geocoder(context);
		this.mapView = mapView;
	}
	
	/**
	 * Look up an address by name.  Calls back <code>listener</code>, including
	 * if an exception occurred.
	 * when done, or if there was an exception.
	 * @param locationName
	 * @param listener the {@link BartlebyGeocoderListener} to call back
	 * 
	 */
	public void lookup(final String locationName,
			final BartlebyGeocoderListener listener) {
		cancelLastSearch();
		lastSearch = worker.submit(new Runnable() {
			public void run() {
				try {
					GeoPoint center = mapView.getMapCenter();
					double centerLat = center.getLatitudeE6() / 1E6;
					double centerLon = center.getLongitudeE6() / 1E6;
					
					List<Address> addresses = geocoder.getFromLocationName(
							locationName, MAX_RESULTS,
							centerLat - SEARCH_RADIUS,
							centerLon - SEARCH_RADIUS,
							centerLat + SEARCH_RADIUS,
							centerLon + SEARCH_RADIUS);
					
					if(addresses != null) {
						if(addresses.size() > 0) {
							listener.onFound(addresses);
							return;
						}
					}
					Log.i(context.getString(R.string.app_name),
								"No addresses found for " + locationName);
				} catch(IOException e) {
					listener.onError(e);
				}
			}
		});
	}
	
	/**
	 * Look up an address by lat/lon.  Calls back <code>listener</code>, including
	 * if an exception occurred. 
	 * @param latitude a <code>double</code> latitude.
	 * @param longitude a <code>double</code> longitude.
	 * @param listener
	 */
	public void lookup(final double latitude, final double longitude, final BartlebyGeocoderListener listener) {
		cancelLastSearch();
		lastSearch = worker.submit(new Runnable() {
			public void run() {
				try {
					listener.onFound(geocoder.getFromLocation(latitude, longitude, MAX_RESULTS));
				} catch(IOException e) {
					listener.onError(e);
				}
			}
		});
	}
	
	/**
	 * Attempt to cancel the last search.
	 */
	private void cancelLastSearch() {
		// should this be threaded? could lock up UI.
		if(lastSearch != null) {
			if(!lastSearch.isDone()) {
				Log.i(context.getString(R.string.app_name), "Cancelling last address search");
			}
			lastSearch.cancel(true);
		}
	}
}
