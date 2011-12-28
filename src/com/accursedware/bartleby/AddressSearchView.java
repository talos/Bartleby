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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
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
	 * This is set to <code>true</code> when something other than the user
	 * changes the text.
	 */
	private boolean forcedTextChange = false;
	
	//private final ProgressBar progress;
	
	//private boolean hasFocus = false;
	
	/**
	 * The last real {@link BartlebyAddress} picked from the adapter.
	 */
	private BartlebyAddress lastClickedItem;
	
	public AddressSearchView(final Activity activity,
			final AsyncGeocoder geocoder,
			final AutoCompleteTextView tv,
			final ProgressBar progress,
			final MapView map,
			final PropertyOverlay overlay) {
		this.tv = tv;
		//progress.setVisibility(View.VISIBLE);
		//this.progress = progress;
		
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
				tv.post(new Runnable() {
					public void run() {
						progress.setVisibility(View.INVISIBLE);
						ArrayAdapter<BartlebyAddress> adapter =
								new ArrayAdapter<BartlebyAddress>(tv.getContext(), R.layout.autocomplete, addresses);
						
						tv.setAdapter(adapter);
						tv.showDropDown();
					}
				});
				//activity.runOnUiThread();
			}

			/**
			 * Display a {@link Toast} letting the user know that no valid addresses exist for
			 * what they entered, and kill the adapter.
			 */
			@Override
			public void onNoAddressesFound(final String locationName) {
				tv.post(new Runnable() {
					public void run() {
						progress.setVisibility(View.INVISIBLE);
						
						//ArrayAdapter<BartlebyAddress> empty = new ArrayAdapter<BartlebyAddress>();
					}
				});
				Toasts.showNoAddressesFound(activity, locationName);
			}
			
			@Override
			public void onError(IOException e) {
				tv.post(new Runnable() {
					public void run() {
						progress.setVisibility(View.INVISIBLE);
					}
				});
				
				Toasts.showGeocoderError(activity);
				e.printStackTrace();
			}

			@Override
			public void onCancel() {
				tv.post(new Runnable() {
					public void run() {
						progress.setVisibility(View.INVISIBLE);
					}
				});
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
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(forcedTextChange) {
					forcedTextChange = false;
				} else if(s.length() > threshold) {
					geocoder.cancelLastSearch();
					progress.setVisibility(View.VISIBLE);
					geocoder.lookup(tv.getText().toString(), listener, map);
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
		
		/*
		 * Select all text if clicked when focus changed, do nothing otherwise.
		 */
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//tv.showDropDown();
				//tv.setText("");
				//tv.clearComposingText();
				//tv.selectAll();
				//if(hasFocus == false) {
					tv.selectAll();
				//	hasFocus = true;
				//}
			}
		});
		
		/*
		 * Revert hasFocus to <code>false</code> no matter what -- it is set to
		 * <code>true</code> from a Click event.
		 */
		/*tv.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hf) {
				Log.i("bartleby", "lost focus");
				hasFocus = false;
			}
		});*/
		
		//tv.set
	}
	
	/**
	 * Set the content of the the search view to a {@link BartlebyAddress}.
	 * Does not call the text change handler.
	 * @param address
	 */
	void setText(BartlebyAddress address) {
		forcedTextChange = true;
		tv.setText(address.toString());
	}
	
	/**
	 * 
	 * @return The currently entered text.
	 */
	String getText() {
		return tv.getText().toString();
	}
	
	/**
	 * Give focus to this element.
	 */
	void focus() {
		tv.selectAll();
	}
}
