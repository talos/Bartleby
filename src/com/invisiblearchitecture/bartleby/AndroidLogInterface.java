/**
 * Bartleby
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import com.invisiblearchitecture.scraper.LogInterface;

/**
 * @author john
 *
 */
public class AndroidLogInterface implements LogInterface {

	/* (non-Javadoc)
	 * @see com.invisiblearchitecture.scraper.LogInterface#e(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void e(String errorText, Throwable e) {
		if(errorText == null) errorText ="";
		e.printStackTrace();
		android.util.Log.e("Bartleby", errorText);
	}

	/* (non-Javadoc)
	 * @see com.invisiblearchitecture.scraper.LogInterface#i(java.lang.String)
	 */
	@Override
	public void i(String infoText) {
		if(infoText == null) infoText = "";
		android.util.Log.i("Bartleby", infoText);
	}

}
