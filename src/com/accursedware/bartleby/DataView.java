/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author talos
 *
 */
class DataView implements DatabaseListener {

	private final Database db;
	
	private String scope;
	
	private final TextView title;
	private final ListView data;
	private final BartlebyRequester requester;
	private final ScrollView view;
	private final Activity activity;
	//private final LinearLayout view;
	
	DataView(Activity activity, Database db, BartlebyRequester requester) {
		this.db = db;
		this.db.addListener(this);
		this.activity = activity;
		
		view = (ScrollView) View.inflate(activity, R.layout.data_view, null);
		//view = (ScrollView) parent.findViewById(R.id.generic_data_view);
		//view = (LinearLayout) View.inflate(parent.getContext(), R.layout.generic_data_view, parent);
		
		this.title = (TextView) view.findViewById(R.id.title);
		this.data = (ListView) view.findViewById(R.id.data);
		this.requester = requester;
	}

	@Override
	public void updated(String updatedScope) {
		if(updatedScope.equals(this.scope)) {
			redraw();
		}
	}
	
	ScrollView getUnderlyingView() {
		return view;
	}
	
	void setScope(String scope, String title) {
		if(!scope.equals(this.scope)) {
			this.scope = scope;
			this.title.setText(title);
			redraw();
		}
	}
	
	private void redraw() {
		final DataAdapter adapter = new DataAdapter(db, requester, this, scope);
		
		activity.runOnUiThread(new Runnable() {
			public void run() {
				data.setAdapter(adapter);
			}
		});
	}

	/**
	 * 
	 */
	public void expand() {
		// TODO Auto-generated method stub
		
	}
}
