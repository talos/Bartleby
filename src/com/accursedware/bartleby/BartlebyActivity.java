/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.accursedware.bartleby.geocoding.AsyncGeocoder;
import com.accursedware.bartleby.util.NetworkUtils;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * @author talos
 *
 */
public class BartlebyActivity extends MapActivity {
	
	private static final int ABOUT_DIALOG_ID = 0;
	private static final int NO_LOCATOR_DIALOG_ID = 1;
	private static final int NO_INTERNET_DIALOG_ID = 2;
	
	private AsyncGeocoder geocoder;
	private ServerPinger pinger;
	private Locator locator;
	
	/**
	 * Set up app basics, inflate views etc.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Inflate main layout.
		setContentView(R.layout.main);
		
		PropertyScraper scraper = new PropertyScraper(this);
		
		// Set up the mapView.
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		locator = new Locator(this, mapView.getController());
		
		PropertyOverlay propertyOverlay = new PropertyOverlay(
				this, getResources().getDrawable(R.drawable.marker), mapView, scraper);
		
		geocoder = new AsyncGeocoder(this);
		
		// Set up the server pinger.
		final Activity activity = this;
		pinger = new ServerPinger(getString(R.string.root_url), new ServerPingerListener() {
			@Override
			public boolean onAlive() {
				return false; // stop making requests if the server is alive.
			}

			@Override
			public boolean onDead() {
				Toasts.showNoServerError(activity);
				
				return true; // keep making requests
			}
			
		});
		
		// Set up the auto-complete address text view.
		final AddressSearchView searchView = new AddressSearchView(this, geocoder,
				(AutoCompleteTextView) findViewById(R.id.autocomplete_address),
				mapView, propertyOverlay);
		
		// This overlay catches all random clicks and creates new points.
		FallThroughOverlay fallThrough = new FallThroughOverlay(this, geocoder,
				mapView.getController(),
				searchView, propertyOverlay);
		
		// Add the overlays.
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(fallThrough);
		overlays.add(propertyOverlay);
	}
	
	/**
	 * When the application resumes, look up where we are now.  Let the user
	 * know if we can't find out.  Also, make sure to resume the geocoder.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		pinger.ping();
		geocoder.resume(this);
		if(!NetworkUtils.isNetworkAvailable(this)) {
			showDialog(NO_INTERNET_DIALOG_ID);
		}
		
		try {
			locator.locate(); // Pan to our current location.
		} catch(NoLocationProvidersException e) {
			showDialog(NO_LOCATOR_DIALOG_ID);
		}
	}
	
	/**
	 * When the application is paused, pause the geocoder.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		pinger.stop();
		geocoder.pause();
	}

	/**
	 * Create the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    
	    // When about is clicked, open up the about dialog box.
	    MenuItem about = menu.findItem(R.id.about);
	    about.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(ABOUT_DIALOG_ID);
				return true;
			}
		});
	    return true;
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    final Dialog dialog;
	    switch(id) {
	    case ABOUT_DIALOG_ID:
	        // do the work to define the pause Dialog
	    	dialog = new AboutDialog(this);
	        break;
	    case NO_LOCATOR_DIALOG_ID:
	    	dialog = AlertDialogs.NoLocation(this);
	    	break;
	    case NO_INTERNET_DIALOG_ID:
	    	dialog = AlertDialogs.NoInternet(this);
	    	break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
}
