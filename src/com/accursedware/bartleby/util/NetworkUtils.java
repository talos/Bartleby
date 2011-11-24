/**
 * Bartleby Android Dev
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @author talos
 *
 */
public class NetworkUtils {
	/**
	 * From http://androidadvice.blogspot.com/2010/09/check-android-network-connection-sample.html
	 * @return <code>true</code> if there is Internet access, <code>false</code> otherwise.
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
