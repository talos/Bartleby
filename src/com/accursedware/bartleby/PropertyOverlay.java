/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.ArrayList;
import java.util.List;

import net.caustic.android.activity.AndroidRequester;
import net.caustic.android.activity.DataView;

import android.app.Activity;
import android.app.PendingIntent;
import android.graphics.drawable.Drawable;

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
	private final AndroidRequester requester;
	private final DataView dataView;
	
	/**
	 * 
	 * @param marker The {@link Drawable} marker that will be used in the {@link ItemizedOverlay}.
	 */
	public PropertyOverlay(Activity activity, Drawable marker, MapView mapView,
			AndroidRequester requester, DataView dataView) {
		super(boundCenter(marker), mapView);
		setDrawFocusedItem(true);
		this.requester = requester;
		this.activity = activity;
		this.dataView = dataView;
		
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
		PendingIntent.getBroadcast(context, requestCode, intent, flags)
		return new PropertyBalloon(activity, getBalloonBottomOffset(), requester, dataView);
	}
	
	@Override
	protected void hideBalloon() {
		super.hideBalloon();
	}
}
