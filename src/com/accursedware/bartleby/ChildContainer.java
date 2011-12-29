/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author talos
 *
 */
class ChildContainer {
	
	static LinearLayout initialize(Context context, String name, DataView dataView, Map<String, String> children) {
		LinearLayout view = (LinearLayout) View.inflate(context, R.layout.child_container, null);
		ListView childrenListView = (ListView) view.findViewById(R.id.children);
		childrenListView.setAdapter(new ChildAdapter(dataView, children));
		return view;
	}
	
	private static class ChildAdapter implements ListAdapter {

		private final TreeMap<String, String> children;
		private final String[] keys;
		private final DataView dataView;
		private ChildAdapter(DataView dataView, Map<String, String> children) {
			this.dataView = dataView;
			// sorted children.
			this.children = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
			this.children.putAll(children);
			this.keys = this.children.keySet().toArray(new String[this.children.size()]);
		}
		
		@Override
		public int getCount() {
			return children.size();
		}

		@Override
		public Object getItem(int position) {
			return children.get(this.keys[position]);
		}

		@Override
		public long getItemId(int position) {
			return position; // ??
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String childID = keys[position];
			String value = children.get(childID);
			return ChildRow.initialize(parent.getContext(), value, dataView, childID);
		}
		
		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return keys.length == 0;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO ?
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO ?
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		/* (non-Javadoc)
		 * @see android.widget.ListAdapter#isEnabled(int)
		 */
		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
