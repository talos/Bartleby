/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

/**
 * Superclass for all {@link Exception}s stemming from Bartleby.
 * @author talos
 *
 */
public class BartlebyException extends Exception {

	protected BartlebyException() {}
	protected BartlebyException(String detailMessage) {
		super(detailMessage);
	}
	protected BartlebyException(Throwable throwable) {
		super(throwable);
	}
	protected BartlebyException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
