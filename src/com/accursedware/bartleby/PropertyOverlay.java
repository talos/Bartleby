/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.caustic.android.activity.DataAdapter;
import net.caustic.android.activity.DataUpdateReceiver;
import net.caustic.android.service.CausticServiceIntent.CausticRequestIntent;

import android.content.Context;
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
	private final Context context;
	private final DataAdapter adapter;
	private final DataUpdateReceiver receiver;
	private final URI baseUri;
	
	/**
	 * 
	 * @param marker The {@link Drawable} marker that will be used in the {@link ItemizedOverlay}.
	 */
	public PropertyOverlay(Context context, Drawable marker, MapView mapView, DataAdapter adapter,
			DataUpdateReceiver receiver) {
		super(boundCenter(marker), mapView);
		setDrawFocusedItem(true);
		this.context = context;
		this.adapter = adapter;
		this.receiver = receiver;
		this.baseUri = URI.create(context.getString(R.string.root_url));
		
		populate();
	}
	
	/**
	 * Make a caustic request for an address whenever it is created.
	 * @param address
	 */
	public void addItem(BartlebyAddress address) {
		Property item = new Property(address);
	    items.add(item);
	    populate();
	    setFocus(item);
	    
	    context.startService(CausticRequestIntent.newRequest(
	    		address.getID().toString(),
	    		address.getPath(baseUri),
	    		"",
	    		address.getMap(),
	    		null, true));
	}
	

	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	protected Property createItem(int i) {
		return items.get(i);
	}
	
	@Override
	protected BalloonOverlayView<Property> createBalloonOverlayView() {
		return new PropertyBalloon(context, getBalloonBottomOffset(), adapter, receiver);
	}
	
	@Override
	protected void hideBalloon() {
		super.hideBalloon();
	}
}
