/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.util.Hashtable;

import android.content.Context;
import net.caustic.Scraper;
import net.caustic.ScraperListener;
import net.caustic.log.AndroidLogger;

/**
 * A wrapper around {@link Scraper} to get property information off a four-part
 * address.
 * @author talos
 *
 */
class BartlebyScraper {
	/**
	 * How many threads to use when scraping.
	 */
	private static int NUM_THREADS = 2;
	
	private static final String NUMBER = "Number";
	private static final String STREET = "Street";
	
	private final Scraper scraper;

	/**
	 * The root url for getting instructions.  ZIP/ is appended to the end of this.
	 */
	private final String rootURL;
	
	public BartlebyScraper(Context context) {
		this.scraper = new Scraper(NUM_THREADS);
		
		// if we're in debug mode, log things.
		if(context.getResources().getBoolean(R.bool.debug) == true) {
			this.scraper.register(new AndroidLogger(context));
		}
		this.rootURL = context.getString(R.string.root_url);
	}
	
	public void scrape(BartlebyAddress address, ScraperListener listener) {
		Hashtable<String, String> input = new Hashtable<String, String>();
		input.put(NUMBER, address.number);
		input.put(STREET, address.street);
		
		scraper.scrape(rootURL + address.zip + "/", input, listener);
	}
}
