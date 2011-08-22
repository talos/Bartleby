/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.ArrayList;

import android.graphics.drawable.StateListDrawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.OverlayItem;

/**
 * @author john
 *
 */
public class BartlebyOverlay extends ItemizedOverlay<BartlebyOverlay.BartlebyOverlayItem> {
	private final ArrayList<BartlebyOverlayItem> gOverlayItems = new ArrayList<BartlebyOverlayItem>();
	
	public final class BartlebyOverlayItem extends OverlayItem {
		private final BartlebyInformation information;
		public final UniversalAddress address;
		/**
		 * @param point
		 * @param title
		 * @param snippet
		 */
		public BartlebyOverlayItem(BartlebyInformation i) {
			super(new GeoPoint((int) (i.getAddress().getLatitude() * 1E6), (int) (i.getAddress().getLongitude() *1E6)), null,null);
			information = i;
			address = i.getAddress();
		}
	}
	
	/**
	 * This lets us pan to the selection easily.
	 */
	private final MapController controller;
	//private final StateListDrawable marker;
	
	public BartlebyOverlay(StateListDrawable m, MapController mc) {
		super(boundCenterBottom(m));
		setDrawFocusedItem(true);
		
		controller = mc;
		
		populate();
		setOnFocusChangeListener(new OnFocusChangeListener() {
			@SuppressWarnings("rawtypes") // In order to get the @Override to work...
			@Override
			public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
				if(newFocus == null) {
					return;
				}
				//BartlebyOverlayItem oldItem = (BartlebyOverlayItem) overlay.nextFocus(false);
				BartlebyOverlayItem newItem = (BartlebyOverlayItem) newFocus;
				
				// TODO stop collection
				/*
				if(oldItem != null) {
					oldItem.property.information.stop();
				}*/
				
				// Publish address info etc.
				newItem.information.publishProgress(0);
				try {
					newItem.information.collect();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void addProperty (BartlebyInformation information) {
		// Kill the addProperty if the addresses are equal.
		
		for(int i = 0; i < gOverlayItems.size(); i++) {
			if(information.getAddress().equals(gOverlayItems.get(i).information.getAddress())) {
				//Log.i("Equal addresses, did not addProperty.");
				populate();
				setFocus(gOverlayItems.get(i));
				return;
			}
		}
		
		//Property property = address.getProperty();
		BartlebyOverlayItem newGOverlayItem = new BartlebyOverlayItem(information);
		gOverlayItems.add(newGOverlayItem);
		
		controller.animateTo(new GeoPoint((int) (information.getAddress().getLatitude() * 1E6), (int) (information.getAddress().getLongitude() *1E6)));
		
		populate();
		setFocus(newGOverlayItem);
	}
	
	/**
	 * Add a property asynchronously via  apoint.
	 * @param context
	 * @param gp
	 */
	/*
    public void addProperty(Context context, GeoPoint gp) {
    }
	*/
    
	@Override
	protected BartlebyOverlayItem createItem(int i) {
		return gOverlayItems.get(i);
	}
	
	@Override
	public int size() {
		return gOverlayItems.size();
	}
	
	
	@Override
	/**
	 * Handle tap on an existing marker, this is called before onTap(GeoPoint tapPoint, MapView mv)
	 */
	protected boolean onTap (int index) {
		BartlebyOverlayItem gOverlayItem = gOverlayItems.get(index);
		//BartlebyLog.i("Tapped.");

		setFocus(gOverlayItem);
		return true;
	}
}
