/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import net.caustic.log.AndroidLogger;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

/**
 * @author talos
 *
 */
public class Bartleby extends MapActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
						
		setContentView(R.layout.main);
		
		BartlebyScraper scraper = new BartlebyScraper(this);
		
		MapView mapView = setupMapView();
		BartlebyItemOverlay itemOverlay = new BartlebyItemOverlay(
				this, getResources().getDrawable(R.drawable.marker), mapView, scraper);
		
		BartlebyGeocoder geocoder = new BartlebyGeocoder(this, mapView);
		
		AutoCompleteAddressTextView tv = setupAddressTextView(geocoder);
		
		GoToLocationGeocoderListener listener =
				new GoToLocationGeocoderListener(this, mapView, tv, itemOverlay);
		setupGoButton(geocoder, tv, listener);
		BartlebyFallThroughOverlay fallThrough = new BartlebyFallThroughOverlay(geocoder, listener);
		
		mapView.getOverlays().add(fallThrough);
		mapView.getOverlays().add(itemOverlay);
	}
	
	/**
	 * Set up the map view, panning to current location.
	 */
	private MapView setupMapView() {
		MapView mapView = (MapView) findViewById(R.id.mapview);
		Log.i("bartleby", mapView.toString());
		mapView.setBuiltInZoomControls(true);
		new BartlebyLocator(this).locate(mapView);
		return mapView;
	}
	
	/**
	 * Set up the address text view to autocomplete.
	 * @param mapView the {@link MapView} that will be used for boundaries of autocomplete.
	 * @return
	 */
	private AutoCompleteAddressTextView setupAddressTextView(BartlebyGeocoder geocoder) {
		return new AutoCompleteAddressTextView(this, geocoder,
				(AutoCompleteTextView) findViewById(R.id.autocomplete_address));
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
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
