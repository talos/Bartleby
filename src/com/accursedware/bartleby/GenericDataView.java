/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

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
	
	GenericDataView(Database db, BartlebyRequester requester, ScrollView genericDataView) {
		this.db = db;
		this.db.addListener(this);
		
		this.title = (TextView) genericDataView.findViewById(R.id.title);
		this.data = (ListView) genericDataView.findViewById(R.id.data);
		this.requester = requester;
		
	}

	public void updated(String updatedScope) {
		
		if(scope != null) {
			if(scope.equals(updatedScope)) {
				redraw();
			}
		}
	}
	
	void setScope(String scope) {
		if(!this.scope.equals(scope)) {
			this.scope = scope;
			redraw();
		}
	}
	
	private void redraw() {
		data.setAdapter(new DataAdapter(db, requester, this, scope));
	}
}
