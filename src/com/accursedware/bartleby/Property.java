/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import com.google.android.maps.OverlayItem;

/**
 * An {@link OverlayItem} with a {@link BartlebyAddress}.
 * @author talos
 *
 */

final class Property extends OverlayItem {
	private final BartlebyAddress address;
	
	public Property(BartlebyAddress address) {
		super(address.getGeoPoint(), null, null);
		this.address = address;
	}
	
	public final BartlebyAddress getAddress() {
		return address;
	}
}