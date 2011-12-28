/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author talos
 *
 */
class GenericDataView implements DatabaseListener {

	private final Database db;
	
	private String scope;
	
	private final TextView title;
	private final ListView data;
	private final BartlebyRequester requester;
	private final ScrollView view;
	//private final LinearLayout view;
	
	GenericDataView(Database db, BartlebyRequester requester, ViewGroup parent) {
		this.db = db;
		this.db.addListener(this);
		
		View.inflate(parent.getContext(), R.layout.generic_data_view, parent);
		view = (ScrollView) parent.findViewById(R.id.generic_data_view);
		//view = (LinearLayout) View.inflate(parent.getContext(), R.layout.generic_data_view, parent);
		
		this.title = (TextView) view.findViewById(R.id.title);
		this.data = (ListView) view.findViewById(R.id.data);
		this.requester = requester;
	}

	@Override
	public void updated(String updatedScope) {
		if(scope != null) {
			if(scope.equals(updatedScope)) {
				redraw();
			}
		}
	}
	
	ScrollView getUnderlyingView() {
		return view;
	}
	
	void setScope(String scope, String title) {
		if(!this.scope.equals(scope)) {
			this.scope = scope;
			redraw();
			this.title.setText(title);
		}
	}
	
	private void redraw() {
		data.setAdapter(new DataAdapter(db, requester, this, scope));
	}
}
