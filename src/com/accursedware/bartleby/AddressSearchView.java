/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.accursedware.bartleby.geocoding.AsyncGeocoder;
import com.accursedware.bartleby.geocoding.AsyncGeocoderListener;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

/**
 * A {@link TextView} that autocompletes with a full address, and can click the map when the user
 * selects an autocomplete option.
 * @author talos
 *
 */
final class AddressSearchView {
	private final AutoCompleteTextView tv;
	
	/**
	 * The last real {@link BartlebyAddress} picked from the adapter.
	 */
	private BartlebyAddress lastClickedItem;
	
	public AddressSearchView(final Activity activity,
			final AsyncGeocoder geocoder,
			final AutoCompleteTextView tv,
			final MapView map,
			final PropertyOverlay overlay) {
		this.tv = tv;
		//tv.setSelectAllOnFocus(true); // this should be set through the XML, too
		/*
		 * A {@link BartlebyGeocoderListener} that updates the array adapter when results are found.
		 *
		 */
		final AsyncGeocoderListener listener = new AsyncGeocoderListener() {

			/**
			 * Add the list of addresses to the autocomplete, and show the drop down immediately.
			 */
			@Override
			public void onFound(String locationName, final List<BartlebyAddress> addresses) {
				// gotta use the UI thread.
				activity.runOnUiThread(new Runnable() {
					public void run() {
						ArrayAdapter<BartlebyAddress> adapter =
								new ArrayAdapter<BartlebyAddress>(activity, R.layout.autocomplete, addresses);
						
						tv.setAdapter(adapter);
						tv.showDropDown();
					}
				});
			}

			/**
			 * Display a {@link Toast} letting the user know that no valid addresses exist for
			 * what they entered, and kill the adapter.
			 */
			@Override
			public void onNoAddressesFound(final String locationName) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toasts.showNoAddressesFound(activity, locationName);
						
						//ArrayAdapter<BartlebyAddress> empty = new ArrayAdapter<BartlebyAddress>();
					}
				});
			}
			
			@Override
			public void onError(IOException e) {
				Toasts.showGeocoderError(activity);
				e.printStackTrace();
			}
		};
		
		/*
		 * This {@link TextWatcher} implementation calls back a {@link BartlebyGeocoder}
		 * to update the {@link AutoCompleteTextView}'s completion list, after the millisecond wait
		 * time defined in {@link R.integer.autocomplete_delay}.  Uses a {@link Timer} to schedule
		 * calls to {@link AsyncGeocoder}.
		 *
		 */
		tv.addTextChangedListener(new TextWatcher() {
			
			private final int threshold = activity.getResources().getInteger(R.integer.completion_threshold);
			private final int delay = activity.getResources().getInteger(R.integer.autocomplete_delay);
			
			private final Timer timer = new Timer();
			
			private TimerTask lastTimer;
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				// cancel the last timer no matter what if text changed.
				if(lastTimer != null) {
					lastTimer.cancel();
				}
				
				// we check against threshold twice -- first to schedule the task.
				if(s.length() > threshold) {
					lastTimer = new TimerTask() {
						
						@Override
						public void run() {
							String locationName = tv.getText().toString();
							
							// we check against threshold twice -- second to see if we
							// should still bother calling the geocoder after the timeout
							if(locationName.length() >= threshold) {
								// use the text as it exists when this is called, instead
								// of the {@link Editable} <code>s</code>
								geocoder.lookup(tv.getText().toString(), listener, map);
							}
						}
					};
					// only call the geocoder if a certain amount of time has elapsed.
					timer.schedule(lastTimer, delay);
				}				
			}
		});
		
		/*
		 * Add a listener that ensures that when one of the auto-fill items is clicked, we jump right away.
		 */
		tv.setOnItemClickListener(new OnItemClickListener() {
			
			/**
			 * Jump to clicked address.
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				lastClickedItem = (BartlebyAddress) parent.getItemAtPosition(position);
				map.getController().animateTo(lastClickedItem.getGeoPoint());
				overlay.addItem(lastClickedItem);
			}
		});
		
		/*
		 * Clear text when clicked.
		 */
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//tv.showDropDown();
				tv.setText("");
				//tv.clearComposingText();
				//tv.selectAll();
			}
		});
	}
	
	/**
	 * Set the content of the the search view to a {@link BartlebyAddress}.
	 * @param address
	 */
	public void setText(BartlebyAddress address) {
		this.tv.setText(address.toString());
	}
	
	/**
	 * 
	 * @return The currently entered text.
	 */
	public String getText() {
		return this.tv.getText().toString();
	}
}
