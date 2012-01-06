/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import net.caustic.android.activity.DataAdapter;
import net.caustic.android.activity.DataUpdateReceiver;
import net.caustic.android.service.CausticServiceIntent.CausticRefreshIntent;

import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
	
	private final Context context;
	private final DataUpdateReceiver receiver;
	
	/**
	 * @param context
	 * @param balloonBottomOffset
	 */
	public PropertyBalloon(Context context, int balloonBottomOffset, DataAdapter adapter,
			DataUpdateReceiver receiver) {
		super(context, balloonBottomOffset);
		//db.addListener(this);
		
		this.context = context;
		this.receiver = receiver;
		//this.activity = activity;
		//title = (TextView) findViewById(R.id.balloon_item_title);
		//snippet = (TextView) findViewById(R.id.balloon_item_snippet);
		
		ViewGroup innerLayout = (ViewGroup) findViewById(R.id.balloon_inner_layout);
		ListView dataView = (ListView) View.inflate(context, R.layout.data_view, null);
		dataView.setAdapter(adapter);
		innerLayout.addView(dataView);
		
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
		
		String id = item.getAddress().getID().toString();
		receiver.listenTo(id);
		
		context.startService(CausticRefreshIntent.newRefresh(id));
		
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
