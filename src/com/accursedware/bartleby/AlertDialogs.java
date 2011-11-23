/**
 * Bartleby Android Dev
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.app.AlertDialog;
import android.content.Context;

/**
 * This class holds static methods to obtain alert dialogs.
 * @author talos
 *
 */
class AlertDialogs {
	
	/**
	 * 
	 * @param context
	 * @return An {@link AlertDialog} that lets the user know that there is
	 * no location service available.
	 */
	static AlertDialog NoLocation(Context context) {
		AlertDialog.Builder builder = getBuilder(context);
		builder.setMessage(context.getString(R.string.no_location_service));
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
	
	/**
	 * 
	 * @param context
	 * @return The {@link AlertDialog.Builder} for this <code>context</code>
	 */
	private static AlertDialog.Builder getBuilder(Context context) {
		return new AlertDialog.Builder(context);
	}
}
