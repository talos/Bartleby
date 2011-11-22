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

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * @author talos
 *
 */
public class Bartleby extends MapActivity {
	
	private static final int ABOUT_DIALOG_ID = 0;
	private static final int ERROR_DIALOG_ID = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		// Inflate main layout.
		setContentView(R.layout.main);
		
		BartlebyScraper scraper = new BartlebyScraper(this);
		
		// Set up the mapView.
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		try {
			new BartlebyLocator(this).locate(mapView); // Pan to our current location.
		} catch(NoLocationProvidersException e) {
			// For now, punt this.  It's not a big deal if there's no geolocation to start.
			//showDialog(ERROR_DIALOG_ID);
		}
		
		BartlebyGeocoder geocoder = new BartlebyGeocoder(this, mapView);
		
		BartlebyItemOverlay itemOverlay = new BartlebyItemOverlay(
				this, getResources().getDrawable(R.drawable.marker), mapView, scraper);
		
		// Set up the auto-complete address text view.
		AutoCompleteAddressTextView tv = new AutoCompleteAddressTextView(this, geocoder,
				(AutoCompleteTextView) findViewById(R.id.autocomplete_address));
		
		GoToLocationGeocoderListener listener =
				new GoToLocationGeocoderListener(this, mapView, tv, itemOverlay);
		setupGoButton(geocoder, tv, listener);
		
		// This overlay catches all random clicks and creates new points.
		BartlebyFallThroughOverlay fallThrough = new BartlebyFallThroughOverlay(geocoder, listener);
		
		// Add the overlays.
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(fallThrough);
		overlays.add(itemOverlay);
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
	    Dialog dialog;
	    switch(id) {
	    case ABOUT_DIALOG_ID:
	        // do the work to define the pause Dialog
	    	dialog = new AboutDialog(this);
	        break;
	    /*case ERROR_DIALOG_ID:
	    	dialog = new ErrorDialog();
	    	break;*/
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	
	/**
	 * Link 'Go' button to {@link GoToLocationGeocoderListener}.
	 * @param tv the {@link AutoCompleteAddressTextView} to read address from.
	 * @return
	 */
	private void setupGoButton(final BartlebyGeocoder geocoder, final AutoCompleteAddressTextView tv, final GoToLocationGeocoderListener listener) {
		findViewById(R.id.button_lookup).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				geocoder.lookup(tv.getText(), listener);
			}
		});
	}
}
