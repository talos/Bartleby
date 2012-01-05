/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * This view shows a balloon with the currently selected property's data.
 * @author talos
 *
 */
class PropertyBalloon extends BalloonOverlayView<Property> {
		
	/**
	 * The currently displayed scope
	 */
	//private BartlebyAddress curAddress;
	
	/**
	 * @param context
	 * @param balloonBottomOffset
	 */
	public PropertyBalloon(Context context, int balloonBottomOffset) {
		super(context, balloonBottomOffset);
		//db.addListener(this);
		
		//this.activity = activity;
		//title = (TextView) findViewById(R.id.balloon_item_title);
		//snippet = (TextView) findViewById(R.id.balloon_item_snippet);
		
		ViewGroup innerLayout = (ViewGroup) findViewById(R.id.balloon_inner_layout);
		
		
		//dataView = new GenericDataView(db, requester, innerLayout);
		//innerLayout.addView(dataView.getUnderlyingView());
		//loading = (LinearLayout) View.inflate(getContext(), R.layout.loading, innerLayout);
	}
	
	/**
	 * Override {@link #setData} to asynchronously populate balloon view.
	 */
	@Override
	public void setData(Property item) {
		super.setData(item);
		
		BartlebyAddress address = item.getAddress();
		//curAddress = address;
		
		//title.setVisibility(VISIBLE);
		//title.setText(address.getLocalString());
		
		//dataView.getUnderlyingView().setVisibility(GONE);
		//loading.setVisibility(VISIBLE);
		
		//dataView.setScope(item.getAddress().getID().toString(), address.getLocalString());
		
		// TODO this needs to be requested!
		//requester.request(address);
	}
}
