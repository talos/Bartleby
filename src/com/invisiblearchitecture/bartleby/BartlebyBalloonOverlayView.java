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

import net.caustic.ScraperListener;
import net.caustic.database.Database;
import net.caustic.http.HttpBrowser;
import net.caustic.instruction.Instruction;
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
	
	private final Map<ThreePartAddress, String[]> addressOwners =
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
		if(curAddress != null) {
			synchronized(this.curAddress) {
				curAddress = address;
			}
		} else {
			curAddress = address;
		}
		
		title.setVisibility(View.VISIBLE);
		title.setText(address.number + " " + address.street);
		
		synchronized(addressOwners) {
			if(addressOwners.containsKey(address)) {
				// if addressOwners already contains text, set it to the text.
				displayCurrentOwners();
			} else {
				snippet.setVisibility(View.GONE);
				scraper.scrape(address, new BartlebyBalloonOverlayItemScraperListener(address));
			}
		}
	}

	/**
	 * Only call this when syncrhonized on {@link #addressOwners}.
	 */
	private void displayCurrentOwners() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				snippet.setText(StringUtils.join(addressOwners.get(curAddress), ", "));
				snippet.setVisibility(View.VISIBLE);				
			}
		});
	}
	
	/**
	 * Update {@link #dialog} with information from the scraper.
	 * @author talos
	 *
	 */
	private class BartlebyBalloonOverlayItemScraperListener implements ScraperListener {
		
		private final ThreePartAddress address;
		BartlebyBalloonOverlayItemScraperListener(ThreePartAddress address) {
			this.address = address;
		}
		
		@Override
		public void onScrape(Instruction instruction, Database db, Scope scope,
				Scope parent, String parentSource, HttpBrowser browser) { }
		
		@Override
		public void onSuccess(Instruction instruction, Database db,
				Scope scope, Scope parent, String source, String key,
				String[] results) {
			// if we found an owner, put it in da hash.
			if(key.contains(OWNER)) {
				// isolate distinct owners
				Set<String> owners = new HashSet<String>(Arrays.asList(results));
				synchronized(addressOwners) {
					addressOwners.put(address, owners.toArray(new String[owners.size()]));
					displayCurrentOwners();
				}
			}
		}
		
		@Override
		public void onMissingTags(Instruction instruction, Database db,
				Scope scope, Scope parent, String source, HttpBrowser browser,
				String[] missingTags) { }

		@Override
		public void onFailed(Instruction instruction, Database db, Scope scope,
				Scope parent, String source, String failedBecause) {  }

		@Override
		public void onFinish(int successful, int stuck, int failed) {

		}

		@Override
		public void onCrashed(Instruction instruction, Scope scope,
				Scope parent, String source, Throwable e) {
			e.printStackTrace();
		}
		
	}
}
