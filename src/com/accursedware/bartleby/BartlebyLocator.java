/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.Timer;
import java.util.TimerTask;

import com.accursedware.bartleby.util.GeoUtils;
import com.google.android.maps.MapView;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Build;

/**
 * From http://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-in-an/3145655#3145655
 * Thanks!
 */
class BartlebyLocator {
	//private final Timer timer;
	private final LocationManager lm;
	private boolean gpsEnabled = false;
	private boolean networkEnabled = false;
	private final boolean emulation;

	public BartlebyLocator(Context context) {
		this.lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		try {
			gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch(SecurityException e) {
			// we can safely ingore this.
		}
		try {
			networkEnabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch(SecurityException e){
			// we can safely ingore this.
		}
		
		if(Build.BRAND.equals("generic")) {
			emulation = true;
		} else {
			emulation = false;
		}
	}
	
	/**
	 * Start to try to find where we are.  
	 * @param map The {@link MapView} to pan.
	 */
	public void locate(MapView map) {
		BartlebyLocationListener listener = new BartlebyLocationListener(map, this);

		if(emulation == true) {
			listener.onLocationChanged(GeoUtils.NYC);
		} else {
			//don't start listeners if no provider is enabled
			if(!gpsEnabled && !networkEnabled) {
				//TODO: some warning about lack of location access?
				throw new RuntimeException("Cannot look up location, no providers available");
			}
			if(gpsEnabled) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
			}
			if(networkEnabled) {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
			}
			// timer1=new Timer();
			//timer1.schedule(new GetLastLocation(), 20000);
		}
	}
	
	/**
	 * Stop checking the {@link LocationManager} for updates.
	 */
	protected void finished(BartlebyLocationListener listener) {
		lm.removeUpdates(listener);
	}
	/*
	public void getLocation() throws NoLocationAccessException {
		// fake out for NYC if emulation.
		if(emulation == true) {
			listener.onLocationChanged(GeoUtils.NYC);
		}
		//don't start listeners if no provider is enabled
		if(!gpsEnabled && !networkEnabled) {
			//TODO: some warning about lack of location access?
			
		}
		if(gpsEnabled) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		}
		if(networkEnabled) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		}
		timer1=new Timer();
		timer1.schedule(new GetLastLocation(), 20000);
	}
	
	protected void stopLooking() {
		
	}

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			 lm.removeUpdates(locationListenerGps);
			 lm.removeUpdates(locationListenerNetwork);

			 Location net_loc=null, gps_loc=null;
			 if(gpsEnabled)
				 gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			 if(networkEnabled)
				 net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			 //if there are both values use the latest one
			 if(gps_loc!=null && net_loc!=null){
				 if(gps_loc.getTime()>net_loc.getTime())
					 locationResult.onLocationChanged(gps_loc);
				 else
					 locationResult.onLocationChanged(net_loc);
				 return;
			 }

			 if(gps_loc!=null){
				 locationResult.onLocationChanged(gps_loc);
				 return;
			 }
			 if(net_loc!=null){
				 locationResult.onLocationChanged(net_loc);
				 return;
			 }
			 locationResult.onLocationChanged(null);
		}
	}*/
}