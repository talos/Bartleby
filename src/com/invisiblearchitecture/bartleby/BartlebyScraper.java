/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.Hashtable;

import android.content.Context;
import android.util.Log;
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
	private final AndroidLogger logger;

	/**
	 * The root url for getting instructions.  ZIP/ is appended to the end of this.
	 */
	private final String rootURL;
	
	public BartlebyScraper(Context context) {
		this.scraper = new Scraper(NUM_THREADS);
		this.logger = new AndroidLogger(context);
		this.scraper.register(logger);
		this.rootURL = context.getString(R.string.root_url);
	}
	
	public void scrape(ThreePartAddress address, ScraperListener listener) {
		Hashtable<String, String> input = new Hashtable<String, String>();
		input.put(NUMBER, address.number);
		input.put(STREET, address.street);
		
		input.put("Borough", "3");
		input.put("Apt", "");
		
		scraper.scrape(rootURL + address.zip + "/", input, listener);
	}
}
