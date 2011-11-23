/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby.geocoding;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.accursedware.bartleby.BartlebyAddress;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

/**
 * Nonblocking {@link Geocoder} using {@link AsyncGeocoderListener}.
 * Uses a single worker thread, which is terminated if any additional
 * lookups are done before it finishes.
 * @author talos
 *
 */
public final class AsyncGeocoder {
	
	/**
	 * The radius in microdegrees (degrees * 1E6) within which a lookup
	 * by name will search.  The center
	 * is established by the center of the passed map.  1 degree latitude
	 * is approximately 69 miles; 1 degree longitude varies from 0 to
	 * approximately 69 miles.
	 */
	//private static double SEARCH_RADIUS = .1; // 1/10 degree each way approx. 14 miles
	
	/**
	 * Max number of address results to retrieve.
	 */
	private static final int MAX_RESULTS = 4;
	//private final Context context;
	private final Geocoder geocoder;
	//private final MapView mapView;
	private final ExecutorService worker = Executors.newSingleThreadExecutor();
	private Future<?> lastSearch;
	
	public AsyncGeocoder(Context context) {
		//this.context = context;
		this.geocoder = new Geocoder(context);
		//this.mapView = mapView;
	}
	
	/**
	 * Look up an address by name.  Calls back <code>listener</code>, including
	 * if an exception occurred.
	 * when done, or if there was an exception.
	 * @param locationName
	 * @param listener the {@link AsyncGeocoderListener} to call back
	 * @param mapView the {@link MapView} to limit lookup using the boundaries of
	 */
	public void lookup(final String locationName,
			final AsyncGeocoderListener listener,
			final MapView mapView) {
		cancelLastSearch();
		lastSearch = worker.submit(new Runnable() {
			public void run() {
				try {
					//GeoPoint center = mapView.getMapCenter();
					List<Address> addresses;
					
					GeoPoint center = mapView.getMapCenter();
					
					double centerLat = center.getLatitudeE6() / 1E6;
					double centerLon = center.getLongitudeE6() / 1E6;

					double latSpan = mapView.getLatitudeSpan() / 1E6;
					double lonSpan = mapView.getLongitudeSpan() / 1E6;
					
					double llLat = centerLat - latSpan;
					double llLon = centerLon - lonSpan;
					double urLat = centerLat + latSpan;
					double urLon = centerLon + lonSpan;
					
					try {
						addresses = geocoder.getFromLocationName(
								locationName, MAX_RESULTS,
								llLat < -90 ? -90 : llLat,
								llLon < -90 ? -90 : llLon,
								urLat > 90 ? 90 : urLat,
								urLon > 90 ? 90 : urLon);
					} catch(IllegalArgumentException e) { // this shouldn't happen, but just in case...
						Log.e("bartleby", e.toString());
						e.printStackTrace();
						addresses = geocoder.getFromLocationName(
								locationName, MAX_RESULTS);
					}
					
					List<BartlebyAddress> bAddresses = BartlebyAddress.fromAddressArray(addresses);
					
					if(bAddresses.size() > 0) {
						listener.onFound(locationName, bAddresses);
					} else {
						listener.onNoAddressesFound(locationName);
					}
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
	public void lookup(final double latitude, final double longitude, final AsyncReverseGeocoderListener listener) {
		cancelLastSearch();
		lastSearch = worker.submit(new Runnable() {
			public void run() {
				try {
					List<BartlebyAddress> addresses =
							BartlebyAddress.fromAddressArray(geocoder.getFromLocation(latitude, longitude, 1));
					if(addresses.size() == 0) {
						listener.onNoAddressesFound(new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6)));
					} else {
						BartlebyAddress address = addresses.get(0);
						listener.onFound(address.getGeoPoint(), address);
					}
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
				//Log.i(context.getString(R.string.app_name), "Cancelling last address search");
			}
			lastSearch.cancel(true);
		}
	}
}
