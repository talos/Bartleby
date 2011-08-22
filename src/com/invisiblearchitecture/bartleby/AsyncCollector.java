/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.io.IOException;
import java.util.Vector;

import com.invisiblearchitecture.scraper.Collector;
import com.invisiblearchitecture.scraper.Gatherer;
import com.invisiblearchitecture.scraper.Gatherer.InsufficientInformationException;
import com.invisiblearchitecture.scraper.Information;

import android.os.AsyncTask;


/**
 * The anonymous AsyncTask encapsulated by the gatherer.
 * A gatherer can re-make these at will.
 * Returns TRUE if it was able to figure out the page at all, false if there was some sort of unsolvable error.
 */

public final class AsyncCollector implements Collector {

	private final boolean preview;
	public AsyncCollector(boolean p) {
		preview = p;
	}
	
	private class AsyncExecuteGatherer extends AsyncTask<Information, Void, Information> {
		private final Gatherer gatherer;
		private final boolean preview;
		private IOException ioException;
		private final Looper looper;
		
		private InsufficientInformationException insufficientInformationException;
		public AsyncExecuteGatherer(Gatherer g, boolean p, Looper l) {
			gatherer = g;
			preview = p;
			looper = l;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Information doInBackground(Information... informations) {
			Information information = informations[0];
			try {
				gatherer.execute(information, preview);
			} catch(InsufficientInformationException e) { // We just have to try again.
				insufficientInformationException = e;
				cancel(true);
			} catch(IOException e) { // Major error here, we won't be able to collect information from this gatherer.
				Bartleby.logger.e("Cancelling download of gatherer.", e);
				ioException = e;
				cancel(true);
			}
			return information;
		}
		
		@Override
		protected void onPostExecute(Information information) {
			if(isCancelled()) {
				if(insufficientInformationException != null) {
					Bartleby.logger.e("Delaying gatherer because of insufficientInformationException", insufficientInformationException);
					//counter.tryGathererLater(gatherer, insufficientInformationException);
					looper.tryGathererLater(gatherer);
				} else if(ioException != null) {
					looper.gatherersRun++;
					Bartleby.logger.e("Major error collecting information from gatherer " + gatherer.id, ioException);
				}
			} else {
				looper.gatherersRun++;
				information.publishProgress(looper.gatherersRun);
			}
		}
		
	}
	
	private final class Looper {
		public int gatherersRun = 0;
		private final Vector<Gatherer> gatherers;
		private final Information information;
		
		@SuppressWarnings("unchecked")
		public Looper(Information i) {
			information = i;
			gatherers = information.gatherers;
		}
		public void tryGathererLater(Gatherer g) {
			gatherers.add(g);
		}
		public void run() {
			while(gatherers.size() > 0) {
				Gatherer gatherer = (Gatherer) gatherers.firstElement();
				gatherers.removeElementAt(0);
				Bartleby.logger.i("Executing gatherer " + gatherer.id);
				AsyncExecuteGatherer asyncExecuteGatherer = new AsyncExecuteGatherer(gatherer, preview, this);
				asyncExecuteGatherer.execute(information);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.invisiblearchitecture.scraper.Collector#collect(com.invisiblearchitecture.scraper.Information)
	 */
	@Override
	public void collect(Information information) {
		Looper looper = new Looper(information);
		looper.run();
	}
}