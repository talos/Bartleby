/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
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
	//private final OnFocusChangeListener focusChangedListener = new BartlebyFocusChangedListener();
		
	/**
	 * This lets us pan to the selection easily.
	 */
	//private final MapController controller;
	
	/**
	 * 
	 * @param marker The {@link Drawable} marker that will be used in the {@link ItemizedOverlay}.
	 */
	public BartlebyItemOverlay(Activity activity, Drawable marker, MapView mapView) {
		super(boundCenterBottom(marker), mapView);
		setDrawFocusedItem(true);
		
		this.activity = activity;
		
		populate();
	}
	
	public void addItem(GeoPoint gp, ThreePartAddress address) {
		BartlebyItem item = new BartlebyItem(activity, gp, address);
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
	//	return new BartlebyBalloonOverlayView(getMapView().getContext(), getBalloonBottomOffset());
		return new BartlebyBalloonOverlayView(activity, getBalloonBottomOffset());
	}
	
	/*@Override
	protected boolean onTap (int index) {		
		BartlebyItem item = items.get(index);
		item.showDialog();
		//item.scrapeInfo();
		//setFocus(item);
		return true;
	}*/
	
	/**
	 * This {@link OnFocusChangeListener} handles what happens when a new {@link OverlayItem}
	 * is clicked.
	 * @author talos
	 *
	 */
	/*
	private static class BartlebyFocusChangedListener implements OnFocusChangeListener {
		@SuppressWarnings("rawtypes") // In order to get the @Override to work...
		@Override
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
			if(newFocus == null) {
				return;
			}
			//BartlebyOverlayItem oldItem = (BartlebyOverlayItem) overlay.nextFocus(false);
			BartlebyOverlayItem newItem = (BartlebyOverlayItem) newFocus;
			
			// Publish address info etc.
			newItem.scrapeInfo();
		}
	}*/
}
