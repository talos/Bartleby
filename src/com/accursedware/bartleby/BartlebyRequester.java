/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.accursedware.bartleby;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.caustic.DefaultScraper;
import net.caustic.Request;
import net.caustic.Response;
import net.caustic.Scraper;
import net.caustic.http.Cookies;
import net.caustic.log.Loggable;
import net.caustic.log.Logger;
import net.caustic.log.MultiLog;
import net.caustic.util.CollectionStringMap;
import net.caustic.util.StringMap;
import net.caustic.util.StringUtils;

/**
 * @author talos
 *
 */
public class BartlebyRequester implements Loggable {

	private final ExecutorService loadSvc = Executors.newSingleThreadExecutor();
	private final ExecutorService findSvc = Executors.newSingleThreadExecutor();
	
	private final Scraper scraper = new DefaultScraper();
	private final URI rootURL;
	
	private final Database db;
	private final MultiLog log = new MultiLog();
	
	public BartlebyRequester(String rootURL, Database db) {
		this.rootURL = URI.create(rootURL);
		this.db = db;
	}
	
	public void register(Logger logger) {
		log.register(logger);
	}
	
	public void request(BartlebyAddress address) {
		String id = address.getID().toString();
		request(id, StringUtils.quote(rootURL.resolve(address.getPath()).toString()),
				"", null, true); // immediately force load on these.
	}

	public void request(String id, String instruction, String uri, String input, boolean force) {
		StringMap tags = new CollectionStringMap(db.getData(id));
		Cookies cookies = db.getCookies(id);
		request(new Request(id, instruction, uri, input, tags, cookies, force));
	}
	
	public void request(Request request) {
		RunnableRequest rRequest = new RunnableRequest(this, scraper, request);
		if(request.force) {
			log.i(StringUtils.quote(request.toString()) + " requested on loadSvc.");
			loadSvc.submit(rRequest); // save laggy loads for this thread.
		} else {
			log.i(StringUtils.quote(request.toString()) + " requested on findSvc.");
			findSvc.submit(rRequest);
		}
	}
	
	public void finished(Request request, Response response) {		
		log.i(StringUtils.quote(request.toString()) + " is finished: " + StringUtils.quote(response.serialize()));
		if(response.values != null) {
			handleFindResponse(request, response.name, response.values, response.uri, response.children);
		} else if(response.wait){
			handleWaitResponse(request, response.description);
		} else if(response.content != null || response.cookies != null) {
			handleLoadResponse(request, response.content, response.cookies, response.uri, response.children);
		}else if(response.children != null) {
			handleReferenceResponse(request, response.uri, response.children);
		}  else if(response.missingTags != null) {
			handleMissingTags(request, response.missingTags);
		} else if(response.failedBecause != null) {
			handleFailure(request, response.failedBecause);
		} else {
			throw new RuntimeException("Invalid response: " + response.serialize());
		}
	}
	
	private void handleFindResponse(Request request, String name, String[] values, String uri, String[] children) {
		log.i(StringUtils.quote(request.toString()) + " responded with find: " + StringUtils.quote(values));
		final boolean isBranch = values.length > 1;

		for(String value : values) {
			final String id;
			
			// if this is a branch, generate new id, branch cookies, and branch tags
			if(isBranch) {
				id = uuid();
				db.saveRelationship(id, request.id, name, value);
			} else {
				id = request.id;
			}
			db.saveData(id, name, value);
			
			//output.print(id, request.id, response.name, response.values[i]);
			for(String child : children) {
				request(id, child, uri, value, false);
			}
		}
		
		// retry stuck instructions that may now be un-stuck.
		List<Request> retry = db.popMissingTags(request.id);
		for(Request retryRequest : retry) {
			request(retryRequest.id, retryRequest.instruction, retryRequest.uri, retryRequest.input, false);
		}
	}
	
	private void handleWaitResponse(Request request, String description) {
		log.i(StringUtils.quote(request.toString()) + " responded with wait: " + StringUtils.quote(description));
		db.saveWait(request.id, request.instruction, request.uri, description);
	}
	
	private void handleLoadResponse(Request request, String content, Cookies cookies, String uri, String[] children) {
		log.i(StringUtils.quote(request.toString()) + " responded with load, adding cookies: " + cookies.toString());
		db.saveCookies(request.id, cookies);
		for(String child : children) {
			request(request.id, child, uri, content, false);
		}		
	}
	
	private void handleReferenceResponse(Request request, String uri, String[] children) {
		log.i(StringUtils.quote(request.toString()) + " responded with reference uri: " + StringUtils.quote(uri));
		for(String child : children) {
			// follow the response's uri, but keep everything else the same as request.
			request(request.id, child, uri, request.input, request.force);
		}
	}
	
	private void handleMissingTags(Request request, String[] missingTags) {
		log.i(StringUtils.quote(request.toString()) + " missing tags: " + StringUtils.quoteJoin(missingTags, ","));
		db.saveMissingTags(request.id, request.instruction, request.uri, request.input, missingTags);
	}
	
	private void handleFailure(Request request, String failedBecause) {
		log.i(StringUtils.quote(request.toString()) + " failed: " + StringUtils.quote(failedBecause));
	}

	public void interrupt(Throwable why) {
		log.e(why);
		//loadSvc.shutdownNow();
		//findSvc.shutdownNow();
	}

	/**
	 * 
	 * @return A {@link String} UUID.
	 */
	private String uuid() {
		return UUID.randomUUID().toString();
	}
}
