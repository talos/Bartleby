/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.invisiblearchitecture.scraper.Information;
import com.invisiblearchitecture.scraper.Utils;

/**
 * @author john
 *
 */
public class Analyzer {
	
	/** Fields. */
	private static final String NAME = Records.NAME;
	@SuppressWarnings("unused")
	private static final String ADDRESS = Records.ADDRESS;
	
	private static final String AMOUNT = "Amount";
	private static final String DATE = "Date";
	
	/** Links to child informations. */
	private static final String BUYERS = "Buyer";
	private static final String SELLERS = "Seller";
	private static final String MORTGAGE_LENDERS = "Lender";
	private static final String MORTGAGE_BORROWERS = "Borrower";
	private static final String ASSIGNEES = "Assignee";
	private static final String ASSIGNORS = "Assignor";
	private static final String SATISFACTION_LENDERS = "Lender";
	private static final String SATISFACTION_BORROWERS = "Borrower";
	//private static final String OWNERS = BartlebyInformation.OWNERS;
	//private static final String DEEDS = BartlebyInformation.DEEDS;
	private static final String MORTGAGES = BartlebyInformation.MORTGAGES;
	private static final String ASSIGNMENTS = BartlebyInformation.ASSIGNMENTS;
	private static final String SATISFACTIONS = BartlebyInformation.SATISFACTIONS;
	
	private static final String NEXT_DOCUMENT = "Next Document";
	private static final String PREVIOUS_DOCUMENT = "Previous Document";
		
	public final Information[] currentLenders;
	public final Information[] currentOwners;
	public final Float originalLoansPrincipal;
	
	/**
	 * An analyzer can take a set of documents and find the maximum amount of information
	 * about the current owners, as well as the active mortgage note holders.  Any of the
	 * initialization fields may be null, but this can result in null activeMortgages and
	 * currentOwners result fields.
	 * 
	 * @param currentOwnersNaive What we know about the currentOwners.  More information can
	 * be culled from the other fields.
	 * @param deeds Historical deeds.
	 * @param mortgages All historical mortgages.
	 * @param assignments  All historical mortgage assignments.
	 * @param satisfactions All historical satisfactions.
	 * @param lisPendens  All historical Lis Pendens.
	 * @return
	 */
	public Analyzer(Information[] currentOwnersNaive, Information[] deeds, Information[] mortgages,
			Information[] assignments, Information[] satisfactions, Information[] lisPendens) {
		Bartleby.logger.i("Analyzing information:");
		Bartleby.logger.i("Input:");
		Bartleby.logger.i("currentOwnersNaive:");
		Information.arrayPublishToLog(currentOwnersNaive);
		Bartleby.logger.i("deeds:");
		Information.arrayPublishToLog(deeds);
		Bartleby.logger.i("mortgages:");
		Information.arrayPublishToLog(mortgages);
		Bartleby.logger.i("assignments:");
		Information.arrayPublishToLog(assignments);
		Bartleby.logger.i("satisfactions:");
		Information.arrayPublishToLog(satisfactions);
		Bartleby.logger.i("lisPendens:");
		Information.arrayPublishToLog(lisPendens);
		
		crossReference(mortgages, assignments, satisfactions);
					
		Information[] armsLengthDeeds = armsLengthDeeds(deeds);
		Information[] recentMortgages;
		
		if(mortgages != null && armsLengthDeeds != null) {
			recentMortgages = mortgagesAfterDocument(mortgages, mostRecentDocument(armsLengthDeeds));
		} else if(mortgages != null) {
			recentMortgages = mortgages;
		} else {
			recentMortgages = null;
		}
		
		if(recentMortgages != null) {
			currentLenders = lendersFromMortgages(recentMortgages);
			originalLoansPrincipal = sum(recentMortgages);
		} else {
			currentLenders = null;
			originalLoansPrincipal = null;
		}
		
		Information[] currentOwnersRedundant;
		if(currentOwnersNaive != null) {
			currentOwnersRedundant = currentOwnersNaive;
		} else if(mostRecentDocument(deeds) != null) {
			currentOwnersRedundant = mostRecentDocument(deeds).children(BUYERS);
		} else if(mostRecentDocument(recentMortgages) != null) {
			currentOwnersRedundant = mostRecentDocument(recentMortgages).children(MORTGAGE_BORROWERS);
		} else if(mostRecentDocument(satisfactions) != null) {
			currentOwnersRedundant = mostRecentDocument(satisfactions).children(SATISFACTION_BORROWERS);
		} else {
			currentOwnersRedundant = null;
		}
		if(currentOwnersRedundant != null) {
			currentOwners = Information.eliminateRedundancyAlong(new String[] {Records.NAME},
					currentOwnersRedundant, Bartleby.logger);
		} else {
			currentOwners = null;
		}
		Bartleby.logger.i("Output:");
		Bartleby.logger.i("currentOwners: ");
		Information.arrayPublishToLog(currentOwners);
		Bartleby.logger.i("currentLenders:");
		Information.arrayPublishToLog(currentLenders);
		if(originalLoansPrincipal != null) {
			Bartleby.logger.i("originalLoansPrincipal:" + originalLoansPrincipal.toString());
		}
	}
	
	public static Information mostRecentDocument(Information[] documents) {
		if(documents != null)
			if(documents.length > 0)
				return documents[0];
		return null;
	}
	
	/**
	 * Return only mortgages from after the cutoff deed.
	 * @param documents
	 * @param cutoffDocuments
	 * @return
	 */
	private static Information[] mortgagesAfterDocument(Information[] mortgages, Information document) {
		if(document != null) {
			List<Information> mortgagesAfter = new ArrayList<Information>(mortgages.length);
			for(int i = 0; i < mortgages.length; i++) {
				Information mortgage = mortgages[i];
	//			if(mortgage.relationTo(document) == Date.SAME || mortgage.relationTo(document) == Date.AFTER)
				int relationship = Date.compare(mortgage.getField(DATE), document.getField(DATE));
				if(relationship == Date.SAME || relationship == Date.AFTER)
					mortgagesAfter.add(mortgage);
			}
			
			return(mortgagesAfter.toArray(new Information[0]));
		} else {
			return mortgages;
		}
	}
	
	/**
	 * Identifies active lenders from a set of mortgages.  Most effective if crossReference() has been
	 * run beforehand.
	 * @return
	 */
	private static Information[] lendersFromMortgages(Information[] mortgages) {
		Vector<Information> lendersVector = new Vector<Information>();
		for(int i = 0; i < mortgages.length; i++) {
			Information mortgage = mortgages[i];
			if(mortgage.children(SATISFACTIONS) == null) {
				if(mortgage.children(ASSIGNMENTS) == null) {
					Utils.arrayIntoVector(mortgage.children(MORTGAGE_LENDERS), lendersVector);
				} else {
					Information assignment = mortgage.children(ASSIGNMENTS)[0];
					// Pursue the lenders in the assignment recursively.
					while(assignment.children(ASSIGNMENTS)[0] != null) {
						assignment = assignment.children(ASSIGNMENTS)[0];
					}
					// If the final assignment is not satisfied, add the lenders.
					if(assignment.children(SATISFACTIONS) == null)
						Utils.arrayIntoVector(assignment.children(ASSIGNEES), lendersVector);
				}
			}
		}
		Information[] lendersArray = new Information[lendersVector.size()];
		lendersVector.copyInto(lendersArray);
		return lendersArray;
	}
	
	/**
	 * Eliminate deeds with similar party names and amounts under $1,000.
	 * @param deeds
	 * @return
	 */
	private static Information[] armsLengthDeeds(Information[] allDeeds) {
		if(allDeeds == null)
			return null;
		Vector<Information> deedsVector = new Vector<Information>(allDeeds.length, 1);
		for(int i = 0; i < allDeeds.length; i++) {
			Information deed = allDeeds[i];
			if(hasEqualElement(
					Information.getFieldArray(deed.children(BUYERS), NAME),
					Information.getFieldArray(deed.children(SELLERS), NAME))
					&& getAmount(deed) < 1000) {
				// negative!
			} else {
				deedsVector.addElement(deed);
			}
		}
		Information[] armsLengthDeeds = new Information[deedsVector.size()];
		deedsVector.copyInto(armsLengthDeeds);
		return armsLengthDeeds;
	}
	
	/**
	 * Make documents cross-reference one another.
	 * @param mortgages
	 * @param assignments
	 * @param satisfactions
	 */
	private static void crossReference(Information[] mortgages, Information[] assignments, Information[] satisfactions) {
		// Cross reference mortgages to assignments.
		if(assignments != null && mortgages != null) {
			for(int i = 0; i < assignments.length; i++) {
				Information assignment = assignments[i];
				for(int j = 0; j < mortgages.length; j++) {
					Information mortgage = mortgages[j];
					int relationship = Date.compare(mortgage.getField(DATE), assignment.getField(DATE));
					if(hasEqualElement(
							Information.getFieldArray(assignment.children(ASSIGNORS), NAME),
							Information.getFieldArray(mortgage.children(MORTGAGE_LENDERS), NAME))
							&& assignment.children(PREVIOUS_DOCUMENT) == null
							&& mortgage.children(NEXT_DOCUMENT) == null
							&& (relationship == Date.BEFORE || relationship == Date.SAME)) {
						assignment.addChildInformations(MORTGAGES, new Information[] {mortgage});
						mortgage.addChildInformations(ASSIGNMENTS, new Information[] {assignment});
					}
				}
			}
		}
		
		// Cross reference satisfactions to assignments.			
		if(satisfactions != null && assignments != null) {
			for(int i = 0; i < satisfactions.length; i++) {
				Information satisfaction = satisfactions[i];
				for(int j = 0; j < assignments.length; j++) {
					Information assignment = assignments[j];
					int relationship = Date.compare(assignment.getField(DATE), satisfaction.getField(DATE));
					if(hasEqualElement(
							Information.getFieldArray(satisfaction.children(SATISFACTION_LENDERS), NAME), 
							Information.getFieldArray(assignment.children(ASSIGNEES), NAME))
							&& assignment.children(NEXT_DOCUMENT) == null
							&& satisfaction.children(PREVIOUS_DOCUMENT) == null
							&& relationship == Date.BEFORE) {
						satisfaction.addChildInformations(PREVIOUS_DOCUMENT, new Information[] {assignment});
						assignment.addChildInformations(NEXT_DOCUMENT, new Information[] {satisfaction});
					}
				}
			}
		}
		
		// Cross reference satisfactions to mortgages.
		if(satisfactions != null && mortgages != null) {					
			for(int i = 0; i < satisfactions.length; i++) {
				Information satisfaction = satisfactions[i];
				for(int j = 0; j < mortgages.length; j++) {
					Information mortgage = mortgages[j];
					int relationship = Date.compare(mortgage.getField(DATE), satisfaction.getField(DATE));
					if(hasEqualElement(
							Information.getFieldArray(satisfaction.children(SATISFACTION_BORROWERS), NAME),
							Information.getFieldArray(mortgage.children(MORTGAGE_BORROWERS), NAME))
							&& mortgage.children(NEXT_DOCUMENT) == null
							&& satisfaction.children(PREVIOUS_DOCUMENT) == null
							&& relationship == Date.BEFORE) {
						satisfaction.addChildInformations(PREVIOUS_DOCUMENT, new Information[] {mortgage});
						mortgage.addChildInformations(NEXT_DOCUMENT, new Information[] {satisfaction});
					}
				}
			}
		}
	}
	/**
	 * Default method for getting the amount from an information.  Returns NULL if not formatted correctly.
	 * Must get rid of commas.
	 */
	private static float getAmount(Information information) {
		try {
			return Float.parseFloat(information.getField(AMOUNT).replaceAll(",", "").trim());
		} catch(NumberFormatException e) {
			Bartleby.logger.e("Invalid document amount: " + information.getField(AMOUNT).replaceAll(",", "").trim(), e);
			return 0;
		} catch(NullPointerException e) {
			Bartleby.logger.e("No document amount.", e);
			return 0;
		}
	}
	
	
	/**
	 * Returns true if one element in either array is the same string as one element in the other array.
	 * Excludes null elements.
 	 * @param array1
	 * @param array2
	 * @return
	 */
	private static boolean hasEqualElement(String[] array1, String[] array2) {
		if(array1 == null || array2 == null)
			return false;
		for(int i = 0; i < array1.length; i++) {
			if(array1[i] == null)
				continue;
			for(int j = 0; j < array2.length; j++) {
				if(array2[j] == null)
					continue;
				if(array1[i].trim().equals(array2[j].trim())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sum the amounts of any set of documents.  Documents with null amounts are considered 0.
	 * @param documents
	 * @return
	 */
	public static float sum(Information[] documents) {
		float sum = 0;
		for(int i = 0; i < documents.length; i++) {
			Information doc = documents[i];
			try {
				sum += getAmount(doc);
			} catch(NullPointerException e) { }
		}
		return sum;
	}

	/**
	 * A simple class for dealing with dates.  Initialized with "[digits]/[digits]/[digits]"
	 */
	private static final class Date {
		public final int year, month, day;
		/**
		 * Initialize with a date line, such as "[digits]/[digits]/[digits]".  This function is very naive, and will
		 * concatenate any digits together before a third slash.
		 * @param dateLine
		 * @throws NumberFormatException If one of the numbers poses a problem.
		 * @throws IllegalArgumentException If one of the numbers is unrealistic (ie. 50 for the month or day.)
		 */
		private Date(String dateLine) throws NumberFormatException {
			String monthString = "";
			String dayString = "";
			String yearString = "";
			short slashesFound = 0;
			for(int i = 0; i < dateLine.length(); i++) {
				char character = dateLine.charAt(i);
				if(character == '/') {
					slashesFound ++;
					if(slashesFound > 2)
						break;
				} else if(character > 47 && character < 58) { // is a digit
					switch(slashesFound) {
						case 0:
							monthString += character;
							break;
						case 1:
							dayString += character;
							break;
						case 2:
							yearString += character;
							break;
					}
				}
			}
			// These will throw NumberFormatException.
			month = Integer.parseInt(monthString);
			day = Integer.parseInt(dayString);
			year = Integer.parseInt(yearString);
			
			if(month > 12)
				throw new IllegalArgumentException(Integer.toString(month) + " is illegal value for month, greater than 12.");
			if(day > 31)
				throw new IllegalArgumentException(Integer.toString(day) + " is illegal value for day, greater than 31.");
			if((year > 2500 || year < 1500) && (year > 100 || year < 0))
				throw new IllegalArgumentException(Integer.toString(year) + " is illegal value for year.");
			/*
			if(year < 20)
				year += 2000;
			if(year >= 20 && year < 100)
				year += 1900;
				*/
		}
		
		public static final int UNKNOWN = 1;
		public static final int BEFORE = 2;
		public static final int SAME = 3;
		public static final int AFTER = 4;
		public static int compare(String thisDateString, String thatDateString) {
			if(thisDateString != null && thatDateString != null) {
				try {
					Date thisDate = new Date(thisDateString);
					Date thatDate = new Date(thatDateString);
					if(thisDate.equals(thatDate) == true)
						return SAME;
					if(thisDate.isAfter(thatDate) == true)
						return AFTER;
					return BEFORE;
				} catch(NumberFormatException e) {
					Bartleby.logger.e("Could not parse a date.", e);
				} catch(IllegalArgumentException e) {
					Bartleby.logger.e("Could not parse a date.", e);
				}
			}
			return UNKNOWN;
		}
		
		private boolean isAfter(Date that) {
			if(this.year > that.year)
				return true;
			if(this.year >= that.year && this.month > that.month)
				return true;
			if(this.year >= that.year && this.month >= that.month && this.day > that.day)
				return true;
			return false;
		}
		
		public String toString() {
			return Integer.toString(month) + '/' + Integer.toString(day) + '/' + Integer.toString(year);
			}
			
			public boolean equals(Date that) {
				if(this.year == that.year && this.month == that.month && this.day == that.day)
					return true;
				return false;
		}
	}
}