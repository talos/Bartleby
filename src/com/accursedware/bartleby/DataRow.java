/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author talos
 *
 */
class DataRow {
	static LinearLayout initialize(Context context, String name, String value) {
		LinearLayout row = (LinearLayout) View.inflate(context, R.layout.data_row, null);
		TextView nameView = (TextView) row.findViewById(R.id.name);
		nameView.setText(name);
		TextView valueView = (TextView) row.findViewById(R.id.value);
		valueView.setText(value);
		return row;
	}
}
