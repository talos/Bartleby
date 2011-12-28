/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import net.caustic.Request;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

final class DataAdapter implements ListAdapter {

	private static final int DATA_ROW = 0;
	private static final int WAIT_ROW = 1;
	private static final int CHILD_CONTAINER = 2;
	
	private final Map<String, String> data;
	private final Map<String, Request> waits;
	private final Map<String, Map<String, String>> children;
	
	private final TreeMap<String, Integer> dataTypes = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
	private final String[] keys;
	private final BartlebyRequester requester;
	private final GenericDataView dataView;
	
	DataAdapter(Database db, BartlebyRequester requester, GenericDataView dataView, String scope) {
		this.requester = requester;
		this.dataView = dataView;
		data = db.getData(scope);
		waits = db.getWait(scope);
		children = db.getChildren(scope);
		
		// load up info for dataTypes
		for(String key : data.keySet()) {
			dataTypes.put(key, DATA_ROW);
		}
		for(String key : waits.keySet()) {
			dataTypes.put(key, WAIT_ROW);
		}
		for(String key : children.keySet()) {
			dataTypes.put(key, CHILD_CONTAINER);
		}
		new ArrayList<String>(dataTypes.keySet());
		this.keys = dataTypes.keySet().toArray(new String[dataTypes.size()]);
	}
	
	@Override
	public int getCount() {
		return keys.length;
	}

	@Override
	public Object getItem(int position) {
		String name = getName(position);
		int type = getItemViewType(position);
		switch(type) {
		case DATA_ROW:
			return data.get(name);
		case WAIT_ROW:
			return waits.get(name);
		case CHILD_CONTAINER:
			return children.get(name);
		}
		throw new IllegalArgumentException("Illegal view type: " + type);
	}

	@Override
	public long getItemId(int position) {
		return position;  // TODO
	}

	@Override
	public int getItemViewType(int position) {
		return dataTypes.get(getName(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String name = getName(position);
		final int type = getItemViewType(position);
		final Context context = parent.getContext();
		
		final LinearLayout row;
		switch(type) {
		case DATA_ROW:
			row = DataRow.initialize(context, parent, name, data.get(name));
			break;
		case WAIT_ROW:
			row = WaitRow.initialize(context, parent, name, requester, waits.get(name));
			break;
		case CHILD_CONTAINER:
			row = ChildContainer.initialize(context, parent, name, dataView, children.get(name));
			break;
		default:
			throw new IllegalArgumentException("Illegal view type: " + type);
		}
		return row;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public boolean hasStableIds() {
		return false; // TODO: ?
	}

	@Override
	public boolean isEmpty() {
		return keys.length == 0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		if(keys.length > position) {
			return true;
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	private String getName(int position) {
		return keys[position];
	}
}