/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.HashMap;
import java.util.Map;

import net.caustic.Scraper;
import net.caustic.android.ViewScraperListener;
import net.caustic.log.AndroidLogger;
import net.caustic.log.Logger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.StateListDrawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * @author talos
 *
 */
public class Bartleby extends MapActivity {
	private static int NUM_THREADS = 4; // number of threads for scraping
	
	private Logger logger;
	private Scraper scraper = new Scraper(NUM_THREADS);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		logger = new AndroidLogger(getApplicationContext());
				
		setContentView(R.layout.main);
		
		//TableLayout table = (TableLayout) findViewById(R.id.table);
		
		MapView mapView = setupMapView();
		panToCurrentLocation(mapView);
		/*
		Map<String, String> input = new HashMap<String, String>();
		input.put("Number", "157");
		input.put("Street", "Pulaski St");
		input.put("Borough", "3");
		input.put("Apt", "");
		scraper.register(logger);
		scraper.addListener(new ViewScraperListener(this, table));
		
		scraper.scrape("https://raw.github.com/talos/caustic/master/fixtures/json/nyc/nyc-property-owner.json", input);
    */
    }
    
    /**
     * Set up the map view.
     */
    private MapView setupMapView() {
		MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    logger.i("set up map view");
	    return mapView;
    }
    
    /**
     * Pan to the current location to start.
     */
    private void panToCurrentLocation(MapView mapView) {
	    logger.i("panning");

    	new BartlebyLocator(this, mapView).locate();
    }
		/*
		// Add the GeograpeOverlay, which is itemized.
		StateListDrawable marker = (StateListDrawable) this.getResources().getDrawable(R.drawable.);
		
		propertyOverlay = new BartlebyOverlay(marker, mapController);
		
		// An overlay to receive clicks that fell through PropertyOverlay.
		Overlay bottomOverlay = new Overlay() {
			@Override
			public boolean onTap(GeoPoint gp, MapView mapView) {
				try {
			        addPropertyAtPoint(gp);
					return true;
				} catch(Exception e) {
					error_view.setText(e.toString());
					e.printStackTrace();
					return false;
				}
			}
		};
		mapView.getOverlays().add(bottomOverlay);
		mapView.getOverlays().add(propertyOverlay);
		
		BartlebyLocation myLocation = new BartlebyLocation();
		myLocation.getLocation(this, new HandleLocationUpdate());
		
		// What to do when the user asks for information.
		lookup_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String streetNum = streetNumEditText.getText().toString();
				String streetName = streetNameEditText.getText().toString();
				String city = cityEditText.getText().toString();
				// Add the property if the user has changed the text inputs.
				// This will NOT add repetitive properties, as addProperty()
				// will check before adding.
				if(propertyOverlay.getFocus() != null) {
					UniversalAddress oldAddress = propertyOverlay.getFocus().address;
					if(oldAddress.equals(streetNum + " " + streetName, city) == false) {
						addPropertyAtEnteredString(streetNum, streetName, city);
					} else {
						showDialog(propertyInfoDialogID);
					}
				}
			}
		});*/
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
