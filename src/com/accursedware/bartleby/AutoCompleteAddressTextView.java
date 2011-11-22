/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.io.IOException;
import java.util.List;

import com.accursedware.bartleby.util.AddressUtils;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.location.Address;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * A {@link TextView} that autocompletes with a full address.
 * @author talos
 *
 */
final class AutoCompleteAddressTextView {
	
	private final AutoCompleteTextView tv;
	
	/**
	 * Used to access UI thread.
	 */
	private final Activity activity;
	
	/**
	 * Used to establish bounds for autocomplete lookups.
	 */
	private final BartlebyGeocoder geocoder;
	private final AddressGeocoderListener listener = new AddressGeocoderListener();
	
	public AutoCompleteAddressTextView(Activity activity,
			BartlebyGeocoder geocoder,
			AutoCompleteTextView tv) {
		this.activity = activity;
		this.tv = tv;
		this.geocoder = geocoder;
		//this.geocoder = new BartlebyGeocoder(tv.getContext());
		tv.addTextChangedListener(new AddressTextWatcher());
	}
	
	/**
	 * Get the text of this {@link AutoCompleteAddressTextView} as a {@link String}
	 * @return a {@link String}
	 */
	public String getText() {
		return tv.getText().toString();
	}
	
	/**
	 * Replace the text of this {@link AutoCompleteAddressTextView} with a {@link String}
	 * @param string a {@link String} to replace the existing text with.
	 */
	public void setText(String string) {
		tv.setText(string);
	}
	
	/**
	 * This {@link TextWatcher} implementation calls back a {@link BartlebyGeocoder}
	 * to update the {@link AutoCompleteTextView}'s completion list.
	 * @author talos
	 *
	 */
	private class AddressTextWatcher implements TextWatcher {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {
			String address = s.toString();
			if(address.length() > activity.getResources().getInteger(R.integer.completion_threshold)) {
				geocoder.lookup(s.toString(), listener);
			}
		}
	}
	
	private class AddressGeocoderListener implements BartlebyGeocoderListener {

		/**
		 * Add the list of addresses to the autocomplete.
		 */
		@Override
		public void onFound(final List<Address> addresses) {
			// gotta use the UI thread.
			activity.runOnUiThread(new Runnable() {
				public void run() {
					ArrayAdapter<String> adapter =
							new ArrayAdapter<String>(tv.getContext(), R.layout.autocomplete_item);
					// add each address separately, because compressAddress has to be called.
					for(Address address : addresses) {
						adapter.add(AddressUtils.compressAddress(address));
					}
					tv.setAdapter(adapter);
				}
			});
		}

		/* (non-Javadoc)
		 * @see com.invisiblearchitecture.bartleby.BartlebyGeocoderListener#onError(java.io.IOException)
		 */
		@Override
		public void onError(IOException e) {
			e.printStackTrace();
		}
		
	}
}
