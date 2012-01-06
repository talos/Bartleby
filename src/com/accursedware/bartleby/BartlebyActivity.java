/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.List;

import net.caustic.android.activity.CausticAndroidActivity;
import net.caustic.android.activity.CausticAndroidButtons;
import net.caustic.android.activity.DataAdapter;
import net.caustic.android.activity.DataUpdateReceiver;
import net.caustic.android.service.CausticServiceIntent.CausticForceIntent;
import net.caustic.android.service.CausticIntentFilter;

import android.app.Dialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.accursedware.bartleby.geocoding.AsyncGeocoder;
import com.accursedware.bartleby.util.NetworkUtils;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * @author talos
 *
 */
public class BartlebyActivity extends MapActivity implements CausticAndroidButtons {
	
	private static final int ABOUT_DIALOG_ID = 0;
	private static final int NO_LOCATOR_DIALOG_ID = 1;
	private static final int NO_INTERNET_DIALOG_ID = 2;
	
	private AsyncGeocoder geocoder;
	//private ServerPinger pinger;
	private Locator locator;
	private AddressSearchView search;
	
	private DataAdapter adapter;
	private DataUpdateReceiver receiver;
	private IntentFilter filter;
	
	/**
	 * Set up app basics, inflate views etc.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new DataAdapter();
		receiver = new DataUpdateReceiver(adapter);
		filter = new CausticIntentFilter();

		// Inflate main layout.
		setContentView(R.layout.main);

		// Set up the mapView.
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		locator = new Locator(this, mapView.getController());
		
		PropertyOverlay propertyOverlay = new PropertyOverlay(
				this, getResources().getDrawable(R.drawable.marker), mapView, adapter, receiver);
		
		geocoder = new AsyncGeocoder(this);

		// Set up the auto-complete address text view.
		search = new AddressSearchView(this, geocoder,
				(AutoCompleteTextView) findViewById(R.id.autocomplete_address),
				(ProgressBar) findViewById(R.id.autocomplete_progress), 
				mapView, propertyOverlay);
		
		// This overlay catches all random clicks and creates new points.
		FallThroughOverlay fallThrough = new FallThroughOverlay(this, geocoder,
				mapView.getController(),
				search, propertyOverlay);
		
		// Add the overlays.
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(fallThrough);
		overlays.add(propertyOverlay);

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		
		// Set up the server pinger.
		//final Activity activity = this;
		/*pinger = new ServerPinger(getString(R.string.root_url), new ServerPingerListener() {
			@Override
			public boolean onAlive() {
				return false; // stop making requests if the server is alive.
			}

			@Override
			public boolean onDead() {
				Toasts.showNoServerError(activity);
				
				return true; // keep making requests
			}
			
		});*/
		
		
	}

	/**
	 * When the application resumes, look up where we are now.  Let the user
	 * know if we can't find out.  Also, make sure to resume the geocoder.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);

		//pinger.ping();
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
		//pinger.stop();
		geocoder.pause();
		unregisterReceiver(receiver);
	}
	
	/**
	 * Absorb search events
	 * @return
	 */
	@Override
	public boolean onSearchRequested() {
		search.focus();
		return true;
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
	
	/**
	 * Switch to specialized activity when viewing child.
	 */
	@Override
	public void viewChild(View view) {
		String childId = (String) view.getTag(R.id.child_id);
		CausticAndroidActivity.launch(this, childId);
	}

	@Override
	public void loadWaitingRequest(View view) {
		String waitId = (String) view.getTag(R.id.wait_id);
		startService(CausticForceIntent.newForce(waitId));
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
