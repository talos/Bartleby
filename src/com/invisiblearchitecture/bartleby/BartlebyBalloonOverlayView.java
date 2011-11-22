/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.caustic.LogScraperListener;
import net.caustic.ScraperListener;
import net.caustic.database.Database;
import net.caustic.http.HttpBrowser;
import net.caustic.instruction.Instruction;
import net.caustic.log.AndroidLogger;
import net.caustic.scope.Scope;
import net.caustic.util.StringUtils;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.readystatesoftware.mapviewballoons.BalloonOverlayView;
//import com.readystatesoftware.mapviewballoons.R;
import com.readystatesoftware.mapviewballoons.R;

/**
 * This view shows a balloon with the currently selected property's data.
 * @author talos
 *
 */
class BartlebyBalloonOverlayView extends BalloonOverlayView<BartlebyItem> {
	/**
	 * String searched for as key for relevant results.
	 */
	private static final String OWNER = "Owner";
	
	private final Activity activity;
	private final TextView title;
	private final BartlebyScraper scraper;
	private final LinearLayout innerLayout;
	private final ListView owners;
	private final LinearLayout loading;
	
	private final Map<ThreePartAddress, String[]> ownersByAddress =
			Collections.synchronizedMap(new HashMap<ThreePartAddress, String[]>());
	
	/**
	 * The currently displayed address.
	 */
	private ThreePartAddress curAddress;
	
	/**
	 * @param context
	 * @param balloonBottomOffset
	 */
	public BartlebyBalloonOverlayView(Activity activity, int balloonBottomOffset, BartlebyScraper scraper) {
		super(activity, balloonBottomOffset);
		
		this.activity = activity;
		this.scraper = scraper;
		title = (TextView) findViewById(R.id.balloon_item_title);
		//snippet = (TextView) findViewById(R.id.balloon_item_snippet);
		
		innerLayout = (LinearLayout) findViewById(R.id.balloon_inner_layout);
		
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		inflater.inflate(R.layout.owners, innerLayout);
		inflater.inflate(R.layout.loading, innerLayout);
		
		owners = (ListView) innerLayout.findViewById(R.id.owners);
		loading = (LinearLayout) innerLayout.findViewById(R.id.loading);
	}
	
	/**
	 * Override {@link #setData} to asynchronously populate balloon view.
	 */
	@Override
	public void setData(BartlebyItem item) {
		super.setData(item);
		
		ThreePartAddress address = item.getAddress();
		curAddress = address;
		
		title.setVisibility(VISIBLE);
		title.setText(address.number + " " + address.street);
		
		displaySnippetForAddress(address);
	}
	
	private void displaySnippetForAddress(final ThreePartAddress address) {
		
		synchronized(ownersByAddress) {
			
			// if we know the owner, show it.
			if(ownersByAddress.containsKey(address)) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						if(curAddress.equals(address)) {
							
							// This creates a new ArrayAdapter from the existing String array of
							// owners, and then gives it to the ListView.
							// Might it be more efficient to store ArrayAdapters directly in 
							// ownersByAddress?
							ArrayAdapter<String> adapter =
									new ArrayAdapter<String>(activity, R.layout.owner_item,
											ownersByAddress.get(address));
							owners.setAdapter(adapter);
							owners.setVisibility(VISIBLE);
							loading.setVisibility(GONE);
							//snippet.setText(StringUtils.join(snippets.get(address), ", "));
							//snippet.setVisibility(View.VISIBLE);
						}
					}
				});
			} else {
				
				// if we don't know the owner, start the scraping request and show the
				// loading icon.
				owners.setVisibility(GONE);
				loading.setVisibility(VISIBLE);
				scraper.scrape(address, new BartlebyBalloonOverlayItemScraperListener(address));
			}
		}
	}
	
	/**
	 * Update {@link #dialog} with information from the scraper.
	 * @author talos
	 *
	 */
	private class BartlebyBalloonOverlayItemScraperListener extends LogScraperListener {
		
		private final ThreePartAddress address;
		
		BartlebyBalloonOverlayItemScraperListener(ThreePartAddress address) {
			super(new AndroidLogger(activity));
			this.address = address;
		}
		
		@Override
		public void onReady(Instruction instruction, Database db, Scope scope,
				Scope parent, String source, HttpBrowser browser, Runnable start) {
			
			// TODO: this auto-scrapes children.
			start.run();
		}
		
		@Override
		public void onSuccess(Instruction instruction, Database db,
				Scope scope, Scope parent, String source, String key,
				String[] results) {
			// if we found an owner, put it in da hash.
			if(key.contains(OWNER)) {
				// isolate distinct owners
				Set<String> owners = new HashSet<String>(Arrays.asList(results));
				synchronized(ownersByAddress) {
					ownersByAddress.put(address, owners.toArray(new String[owners.size()]));
				}
				displaySnippetForAddress(address);
			}
			super.onSuccess(instruction, db, scope, parent, source, key, results);
		}
		
		public void onFailed(Instruction instruction, Database db, Scope scope,
				Scope parent, String source, String failedBecause) {
			synchronized(ownersByAddress) {
				//ownersByAddress.put(address, new String[] { failedBecause });
				ownersByAddress.put(address, new String[] {
						activity.getString(R.string.failed)
						});
			}
			displaySnippetForAddress(address);
			
			super.onFailed(instruction, db, scope, parent, source, failedBecause);
		}
		

		public void onCrashed(Instruction instruction, Scope scope, Scope parent,
				String source, Throwable e) {
			synchronized(ownersByAddress) {
				//ownersByAddress.put(address, new String[] { e.toString() });
				ownersByAddress.put(address, new String[] {
						activity.getString(R.string.crashed)
						});
			}
			
			super.onCrashed(instruction, scope, parent, source, e);
		}
	}
}
