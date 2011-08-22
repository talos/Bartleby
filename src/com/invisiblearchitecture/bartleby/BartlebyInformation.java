/**
 * Bartleby Android
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.Hashtable;

import com.invisiblearchitecture.scraper.Information;
import com.invisiblearchitecture.bartleby.UniversalAddress;

/**
 * @author john
 *
 */

/**
 * Wrapper class for AbstractInformation that casts specific data types correctly.
 * @author john
 *
 */
public final class BartlebyInformation {
	
	public static final String VIOLATIONS = "Violations";
	public static final String COMPLAINTS = "Complaints";
	
	public static final String OWNERS = "Owner";
	public static final String DEEDS = "Deed";
	public static final String MORTGAGES = "Mortgage";
	public static final String ASSIGNMENTS = "Assignment";
	public static final String SATISFACTIONS = "Satisfaction";
	public static final String LIS_PENDENS = "Lis Pendens";
	
	public static final String MARKET_VALUES = "Market Values";
	
	private final Information information;
	private final UniversalAddress address;
	public BartlebyInformation(Information info, UniversalAddress addr) {
		information = info;
		address = addr;
		information.putFields(new Hashtable<String, String>(address.toMap()));
	}
	/**
	 * We do NOT automatically recursively collect.
	 * @throws InterruptedException 
	 */
	public void collect() throws InterruptedException {
		information.collect(false);
	}
	public void publishProgress(int gatherersFinished) {
		information.publishProgress(gatherersFinished);
	}
	
	public Information[] getCurrentOwners()  {
		return information.children(information.namespace, OWNERS);
	}
	
	public Information[] getDeeds() {
		return information.children(information.namespace, DEEDS);
	}
	public Information[] getMortgages() {
		return information.children(information.namespace, MORTGAGES);
	}
	public Information[] getAssignments() {
		return information.children(information.namespace, ASSIGNMENTS);
	}
	public Information[] getSatisfactions() {
		return information.children(information.namespace, SATISFACTIONS);
	}
	public Information[] getLisPendens() {
		return information.children(information.namespace, LIS_PENDENS);
	}
	public Information[] getMarketValues() {
		return information.children(information.namespace, MARKET_VALUES);
	}

	public UniversalAddress getAddress() {
		return address;
	}
	public String getViolations() {
		return information.getField(VIOLATIONS);
	}
	public String getComplaints() {
		return information.getField(COMPLAINTS);
	}
}