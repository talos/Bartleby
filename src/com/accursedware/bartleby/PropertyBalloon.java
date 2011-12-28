/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This view shows a balloon with the currently selected property's data.
 * @author talos
 *
 */
class PropertyBalloon extends BalloonOverlayView<Property> implements DatabaseListener {
	
	private final Activity activity;
	private final TextView title;
	private final BartlebyRequester requester;
	private final LinearLayout innerLayout;
	private final GenericDataView dataView;
	private final LinearLayout loading;
	
	/**
	 * The currently displayed scope
	 */
	private BartlebyAddress curAddress;
	
	/**
	 * @param context
	 * @param balloonBottomOffset
	 */
	public PropertyBalloon(Activity activity, int balloonBottomOffset, BartlebyRequester requester,
			Database db) {
		super(activity, balloonBottomOffset);
		
		this.activity = activity;
		this.requester = requester;
		title = (TextView) findViewById(R.id.balloon_item_title);
		//snippet = (TextView) findViewById(R.id.balloon_item_snippet);
		
		innerLayout = (LinearLayout) findViewById(R.id.balloon_inner_layout);
		
		dataView = new GenericDataView(db, requester, innerLayout);
		loading = (LinearLayout) View.inflate(getContext(), R.layout.loading, innerLayout);
	}
	
	/**
	 * Override {@link #setData} to asynchronously populate balloon view.
	 */
	@Override
	public void setData(Property item) {
		super.setData(item);
		
		BartlebyAddress address = item.getAddress();
		curAddress = address;
		
		title.setVisibility(VISIBLE);
		title.setText(address.getLocalString());
		
		dataView.getUnderlyingView().setVisibility(GONE);
		loading.setVisibility(VISIBLE);
		requester.request(address);
	}

	/* (non-Javadoc)
	 * @see com.accursedware.bartleby.DatabaseListener#updated(java.lang.String)
	 */
	@Override
	public void updated(final String updatedID) {
			
		// if we know about it, show about it.
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if(updatedID.equals(curAddress.getID().toString())) {
					
					dataView.getUnderlyingView().setVisibility(VISIBLE);
					loading.setVisibility(GONE);
				}
			}
		});
	}
}
