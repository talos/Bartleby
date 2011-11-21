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
import android.view.View;
import android.widget.TextView;

import com.readystatesoftware.mapviewballoons.BalloonOverlayView;
//import com.readystatesoftware.mapviewballoons.R;

/**
 * @author talos
 *
 */
public class BartlebyBalloonOverlayView extends BalloonOverlayView<BartlebyItem> {
	/**
	 * String searched for as key for relevant results.
	 */
	private static final String OWNER = "Owner";
	
	private final Activity activity;
	private final TextView title;
	private final TextView snippet;
	private final BartlebyScraper scraper;
	
	private final Map<ThreePartAddress, String[]> snippets =
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
		snippet = (TextView) findViewById(R.id.balloon_item_snippet);
		
	}
	
	/**
	 * Override {@link #setData} to asynchronously populate balloon view.
	 */
	@Override
	public void setData(BartlebyItem item) {
		super.setData(item);
		
		ThreePartAddress address = item.getAddress();
		curAddress = address;
		
		title.setVisibility(View.VISIBLE);
		title.setText(address.number + " " + address.street);
		
		displaySnippetForAddress(address);
	}
	
	private void displaySnippetForAddress(final ThreePartAddress address) {
		
		synchronized(snippets) {
			if(snippets.containsKey(address)) {
				// if addressOwners already contains text, set it to the text.

				activity.runOnUiThread(new Runnable() {
					public void run() {
						if(curAddress.equals(address)) {
							snippet.setText(StringUtils.join(snippets.get(address), ", "));
							snippet.setVisibility(View.VISIBLE);
						}
					}
				});
			} else {
				snippet.setVisibility(View.GONE);
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
				synchronized(snippets) {
					snippets.put(address, owners.toArray(new String[owners.size()]));
				}
				displaySnippetForAddress(address);
			}
			super.onSuccess(instruction, db, scope, parent, source, key, results);
		}
		
		public void onFailed(Instruction instruction, Database db, Scope scope,
				Scope parent, String source, String failedBecause) {
			synchronized(snippets) {
				snippets.put(address, new String[] { failedBecause });
			}
			displaySnippetForAddress(address);
			
			super.onFailed(instruction, db, scope, parent, source, failedBecause);
		}
		

		public void onCrashed(Instruction instruction, Scope scope, Scope parent,
				String source, Throwable e) {
			synchronized(snippets) {
				snippets.put(address, new String[] { e.toString() });
			}
			
			super.onCrashed(instruction, scope, parent, source, e);
		}
	}
}
