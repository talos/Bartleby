/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.HashSet;
import java.util.Set;

import net.caustic.ScraperListener;
import net.caustic.database.DatabaseListenerException;
import net.caustic.http.HttpBrowser;
import net.caustic.instruction.Instruction;
import net.caustic.scope.Scope;
import net.caustic.util.StringUtils;

import android.app.Activity;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * An {@link OverlayItem} with a {@link ThreePartAddress}.
 * @author talos
 *
 */

final class BartlebyItem extends OverlayItem {
	/**
	 * String searched for as key for relevant results.
	 */
	private static final String OWNER = "Owner";
	
	private final ThreePartAddress address;
	private final Activity activity;
	private final ScraperListener listener = new BartlebyOverlayItemScraperListener();
	
	public BartlebyItem(Activity activity, GeoPoint gp,
			ThreePartAddress address) {
		super(gp, null, null);
		this.address = address;
		this.activity = activity;
		
		BartlebyScraper scraper = new BartlebyScraper(activity);
		scraper.addListener(listener);
		scraper.scrape(address);
	}
	
	public final ThreePartAddress getAddress() {
		return address;
	}
	
	public final setOnOwnerFoundListener() {
		
	}

	/**
	 * Update {@link #dialog} with information from the scraper.
	 * @author talos
	 *
	 */
	private class BartlebyOverlayItemScraperListener implements ScraperListener {
		
		/**
		 * Unique {@link Set} of owners.
		 */
		private Set<String> owners = new HashSet<String>();
		
		@Override
		public void put(Scope scope, String key, String value)
				throws DatabaseListenerException {
			addOwner(key, value);
		}
		
		@Override
		public void newScope(Scope scope) throws DatabaseListenerException { }
		
		@Override
		public void newScope(Scope parent, String key, Scope child)
				throws DatabaseListenerException { }

		@Override
		public void newScope(Scope parent, String key, String value, Scope child)
				throws DatabaseListenerException {
			addOwner(key, value);
		}

		@Override
		public void scrape(Instruction instruction, Scope scope, String source,
				HttpBrowser browser) { }

		@Override
		public void success(Instruction instruction, Scope scope,
				String source, HttpBrowser browser) { }

		@Override
		public void missing(Instruction instruction, Scope scope,
				String source, HttpBrowser browser, String[] missingTags) { }

		@Override
		public void failed(Instruction instruction, Scope scope, String source,
				String failedBecause) { }

		@Override
		public void terminated(int successful, int missing, int failed) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					//loading.setVisibility(View.GONE);
				}
			});
		}
		
		@Override
		public void crashed(Instruction instruction, Scope scope,
				String source, Throwable e) {
			e.printStackTrace();
		}
		
		/**
		 * Add an owner to {@link #owners} if the key is an owner, and update
		 * the dialog.
		 */
		private void addOwner(String key, String value) {
			if(key.contains(OWNER)) {
				owners.add(value);
			}
			final String ownersStr = StringUtils.join(owners.toArray(new String[owners.size()] ), ", ");
			activity.runOnUiThread(new Runnable() {
				public void run() {
					//snippet.setVisibility(View.VISIBLE);
					//snippet.setText(ownersStr);
					//content.setVisibility(View.VISIBLE);
					//Log.i("bartleby", "owners str: " + ownersStr);
				}
			});
		}
	}
	
	public static interface FoundOwnerListener {
		public void onFoundOwner(Set<String> owners);
	}
}