/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.List;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.accursedware.bartleby.geocoding.AsyncGeocoder;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * @author talos
 *
 */
public class Bartleby extends MapActivity {
	
	private static final int ABOUT_DIALOG_ID = 0;
	private static final int NO_LOCATOR_DIALOG_ID = 1;
	
	private Locator locator;
	
	/**
	 * Set up app basics, inflate views etc.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Inflate main layout.
		setContentView(R.layout.main);
		
		BartlebyScraper scraper = new BartlebyScraper(this);
		
		// Set up the mapView.
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		locator = new Locator(this, mapView.getController());
		
		final AsyncGeocoder geocoder = new AsyncGeocoder(this);
		
		PropertyOverlay propertyOverlay = new PropertyOverlay(
				this, getResources().getDrawable(R.drawable.marker), mapView, scraper);
		
		// Set up the auto-complete address text view.
		final AddressSearchView searchView = new AddressSearchView(this, geocoder,
				(AutoCompleteTextView) findViewById(R.id.autocomplete_address),
				mapView, propertyOverlay);
		
		// Set up lookup button to search for currently entered address.
		new AddressSearchButton(this, geocoder, findViewById(R.id.button_lookup),
				searchView, mapView, propertyOverlay);
		
		// This overlay catches all random clicks and creates new points.
		FallThroughOverlay fallThrough = new FallThroughOverlay(this, geocoder, mapView.getController(),
				searchView, propertyOverlay);
		
		// Add the overlays.
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(fallThrough);
		overlays.add(propertyOverlay);
	}
	
	/**
	 * When the application resumes, look up where we are now.  Let the user
	 * know if we can't find out.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		try {
			locator.locate(); // Pan to our current location.
		} catch(NoLocationProvidersException e) {
			showDialog(NO_LOCATOR_DIALOG_ID);
		}
		
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
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
}
