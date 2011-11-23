/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.util.Linkify;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

/**
 * An {@link ItemizedOverlay} that holds {@link Property}s.
 * @author john
 *
 */
class PropertyOverlay extends BalloonItemizedOverlay<Property> {
	private final List<Property> items = new ArrayList<Property>();
	private final Activity activity;
	private final BartlebyScraper scraper;
	
	/**
	 * 
	 * @param marker The {@link Drawable} marker that will be used in the {@link ItemizedOverlay}.
	 */
	public PropertyOverlay(Activity activity, Drawable marker, MapView mapView, BartlebyScraper scraper) {
		super(boundCenter(marker), mapView);
		setDrawFocusedItem(true);
		//int the = android.R.drawable.ic_menu_search;
		this.scraper = scraper;
		this.activity = activity;
		
		populate();
	}
	
	public void addItem(BartlebyAddress address) {
		Property item = new Property(address);
	    items.add(item);
	    populate();
	    setFocus(item);
	}
	
	@Override
	protected Property createItem(int i) {
		return items.get(i);
	}
	
	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	protected BalloonOverlayView<Property> createBalloonOverlayView() {
		return new PropertyBalloonOverlayView(activity, getBalloonBottomOffset(), scraper);
	}
}
