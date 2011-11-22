/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * An {@link OverlayItem} with a {@link ThreePartAddress}.
 * @author talos
 *
 */

final class BartlebyItem extends OverlayItem {
	private final ThreePartAddress address;
	
	public BartlebyItem(GeoPoint gp, ThreePartAddress address) {
		super(gp, null, null);
		this.address = address;
	}
	
	public final ThreePartAddress getAddress() {
		return address;
	}
}