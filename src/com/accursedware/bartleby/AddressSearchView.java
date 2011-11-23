/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;
import java.util.List;

import com.accursedware.bartleby.geocoding.AsyncGeocoder;
import com.accursedware.bartleby.geocoding.AsyncGeocoderListener;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
		
		/*
		 * A {@link BartlebyGeocoderListener} that updates the array adapter when results are found.
		 *
		 */
		final AsyncGeocoderListener listener = new AsyncGeocoderListener() {

			/**
			 * Add the list of addresses to the autocomplete.
			 */
			@Override
			public void onFound(String locationName, final List<BartlebyAddress> addresses) {
				// gotta use the UI thread.
				activity.runOnUiThread(new Runnable() {
					public void run() {
						ArrayAdapter<BartlebyAddress> adapter =
								new ArrayAdapter<BartlebyAddress>(activity, R.layout.autocomplete_item, addresses);
						tv.setAdapter(adapter);
					}
				});
			}

			/**
			 * Display a {@link Toast} letting the user know that no valid addresses exist for what they entered.
			 */
			@Override
			public void onNoAddressesFound(final String locationName) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity, activity.getString(R.string.no_addresses_for_query, locationName), Toast.LENGTH_SHORT);
					}
				});
			}
			
			@Override
			public void onError(IOException e) {
				e.printStackTrace();
			}
		};
		
		/*
		 * This {@link TextWatcher} implementation calls back a {@link BartlebyGeocoder}
		 * to update the {@link AutoCompleteTextView}'s completion list.
		 *
		 */
		tv.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				String address = s.toString();
				if(address.length() > activity.getResources().getInteger(R.integer.completion_threshold)) {
					geocoder.lookup(s.toString(), listener, map);
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
