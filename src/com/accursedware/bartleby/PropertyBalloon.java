/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

import net.caustic.log.AndroidLogger;
import net.caustic.util.StringUtils;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This view shows a balloon with the currently selected property's data.
 * @author talos
 *
 */
class PropertyBalloon extends BalloonOverlayView<Property> implements DatabaseListener {
	/**
	 * String searched for as key for relevant results.
	 */
	private static final String OWNER = "Owner";
	
	private final Activity activity;
	private final TextView title;
	private final BartlebyRequester requester;
	private final Database db;
	private final LinearLayout innerLayout;
	private final ListView owners;
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
		this.db = db;
		title = (TextView) findViewById(R.id.balloon_item_title);
		//snippet = (TextView) findViewById(R.id.balloon_item_snippet);
		
		innerLayout = (LinearLayout) findViewById(R.id.balloon_inner_layout);
		
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		inflater.inflate(R.layout.owners, innerLayout);
		inflater.inflate(R.layout.loading, innerLayout);
		
		owners = (ListView) innerLayout.findViewById(R.id.owners);
		loading = (LinearLayout) innerLayout.findViewById(R.id.loading);
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
		
		owners.setVisibility(GONE);
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
					// Check to see if we know about this scope.
					Map<String, String> data = db.getData(updatedID);
					
					Map<String, List<String>> children = db.getChildren(updatedID);
					
					// This creates a new ArrayAdapter from the existing String array of
					// owners, and then gives it to the ListView.
					// Might it be more efficient to store ArrayAdapters directly in 
					// ownersByAddress?
					ArrayAdapter<String> adapter =
							new ArrayAdapter<String>(activity, R.layout.owner,
									ownersByAddress.get(address));
					owners.setAdapter(adapter);
					owners.setVisibility(VISIBLE);
					loading.setVisibility(GONE);
				}
			}
		});
	}
}
