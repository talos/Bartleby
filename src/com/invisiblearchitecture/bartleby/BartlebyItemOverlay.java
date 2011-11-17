/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

/**
 * An {@link ItemizedOverlay} that holds {@link BartlebyItem}s.
 * @author john
 *
 */
class BartlebyItemOverlay extends BalloonItemizedOverlay<BartlebyItem> {
	private final List<BartlebyItem> items = new ArrayList<BartlebyItem>();
	private final Activity activity;
	private final BartlebyScraper scraper;
	//private final OnFocusChangeListener focusChangedListener = new BartlebyFocusChangedListener();
		
	/**
	 * This lets us pan to the selection easily.
	 */
	//private final MapController controller;
	
	/**
	 * 
	 * @param marker The {@link Drawable} marker that will be used in the {@link ItemizedOverlay}.
	 */
	public BartlebyItemOverlay(Activity activity, Drawable marker, MapView mapView, BartlebyScraper scraper) {
		super(boundCenterBottom(marker), mapView);
		setDrawFocusedItem(true);
		
		this.scraper = scraper;
		this.activity = activity;
		
		populate();
	}
	
	public void addItem(GeoPoint gp, ThreePartAddress address) {
		BartlebyItem item = new BartlebyItem(gp, address);
	    items.add(item);
	    populate();
	    setFocus(item);
	    //item.showDialog();
	}
	
	@Override
	protected BartlebyItem createItem(int i) {
		return items.get(i);
	}
	
	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	protected BalloonOverlayView<BartlebyItem> createBalloonOverlayView() {
		return new BartlebyBalloonOverlayView(activity, getBalloonBottomOffset(), scraper);
	}
}
