/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * The about dialog for Bartleby.
 * @author talos
 *
 */
class AboutDialog extends Dialog {
	public AboutDialog(Context context) {
		super(context);
    	
    	setContentView(R.layout.about);

     	setCanceledOnTouchOutside(true);
     	
    	// Give it a title.
    	setTitle(context.getString(R.string.about_title));
    	
    	// Get all our paragraphs, stick them in using adapter.
    	String[] about_paragraphs = context.getResources().getStringArray(R.array.about_paragraphs);
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
    			R.layout.paragraph_item, about_paragraphs);
    	ListView text = (ListView) findViewById(R.id.about_text);
    	text.setAdapter(adapter);
	}
}
