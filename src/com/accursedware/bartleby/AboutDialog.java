/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The about dialog for Bartleby.
 * @author talos
 *
 */
class AboutDialog extends Dialog {
	public AboutDialog(Activity activity) {
		super(activity);
    	
    	setContentView(R.layout.about);

     	setCanceledOnTouchOutside(true);
     	
    	// Give it a title.
    	setTitle(activity.getString(R.string.about_title));
    	
    	// Get all our paragraphs, stick them in using adapter.
    	String[] about_paragraphs = activity.getResources().getStringArray(R.array.about_paragraphs);
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
    			R.layout.paragraph_item, about_paragraphs);
    	ListView text = (ListView) findViewById(R.id.about_text);
    	text.setAdapter(adapter);
    	
    	TextView versionText = (TextView) findViewById(R.id.version_text);
		StringBuilder versionStr = new StringBuilder(activity.getString(R.string.app_name));
		
		// Display version info if we can get it.
		try {
			PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			
			int versionCode = info.versionCode;
			String versionName = info.versionName;
			
			versionText.setText(
					versionStr.append(" ").append(versionName).append(" (")
					.append(Integer.toString(versionCode)).append(")"));
			
		} catch(PackageManager.NameNotFoundException e) {
			versionText.setText(
					versionStr.append(" (").append(activity.getString(R.string.no_package_info)).append(")"));
		}
	}
}
