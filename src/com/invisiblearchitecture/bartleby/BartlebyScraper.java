/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.HashMap;
import java.util.Map;

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
	
	/**
	 * The base url for getting instructions.  ZIP is appended to the end of this.
	 */
	private static final String BASE_URL = "http://192.168.1.4/~talos/bartleby/nyc-property-owner.json";
	
	private final Scraper scraper;
	private final AndroidLogger logger;
	
	public BartlebyScraper(Context context) {
		this.scraper = new Scraper(NUM_THREADS);
		this.logger = new AndroidLogger(context);
		this.scraper.register(logger);
	}
	
	public void scrape(ThreePartAddress address) {
		Map<String, String> input = new HashMap<String, String>();
		input.put(NUMBER, address.number);
		input.put(STREET, address.street);
		
		input.put("Borough", "3");
		input.put("Apt", "");
		scraper.scrape(BASE_URL, input);
		//scraper.scrape(BASE_URL + address.zip, input);
	}
	
	public void addListener(ScraperListener listener) {
		scraper.addListener(listener);
	}
}
