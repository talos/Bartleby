/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author talos
 *
 */
class ChildRow {

	static LinearLayout initialize(Context context, ViewGroup parent, String value, GenericDataView dataView, String childID) {
		LinearLayout view = (LinearLayout) View.inflate(context, R.layout.child_row, parent);
		TextView valueView = (TextView) view.findViewById(R.id.value);
		valueView.setText(value);
		ImageButton goToChildButton = (ImageButton) view.findViewById(R.id.go_to_child_button);
		goToChildButton.setOnClickListener(new Listener(dataView, childID));
		return view;
	}
	
	private static class Listener implements OnClickListener {
		private final GenericDataView dataView;
		private final String childID;
		private Listener(GenericDataView dataView, String childID) {
			this.dataView = dataView;
			this.childID = childID;
		}
		@Override
		public void onClick(View v) {
			dataView.setScope(childID);
		}
	}
}
