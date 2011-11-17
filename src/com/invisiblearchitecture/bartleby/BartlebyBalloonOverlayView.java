/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.HashSet;
import java.util.Set;

import net.caustic.ScraperListener;
import net.caustic.database.DatabaseListenerException;
import net.caustic.http.HttpBrowser;
import net.caustic.instruction.Instruction;
import net.caustic.scope.Scope;
import net.caustic.util.StringUtils;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;
import com.readystatesoftware.mapviewballoons.R;

/**
 * @author talos
 *
 */
public class BartlebyBalloonOverlayView extends BalloonOverlayView<BartlebyItem> {

	
	private final Activity activity;
	private final TextView title;
	private final TextView snippet;
	
	/**
	 * @param context
	 * @param balloonBottomOffset
	 */
	public BartlebyBalloonOverlayView(Activity activity, int balloonBottomOffset) {
		super(activity, balloonBottomOffset);
		
		this.activity = activity;
		title = (TextView) findViewById(R.id.balloon_item_title);
		snippet = (TextView) findViewById(R.id.balloon_item_snippet);
		
	}
	
	/**
	 * Override {@link #setData} to asynchronously populate balloon view.
	 */
	@Override
	public void setData(BartlebyItem item) {
		super.setData(item);
		
		
		title.setVisibility(View.VISIBLE);
		title.setText(address.number + " " + address.street);
	}
	
}
