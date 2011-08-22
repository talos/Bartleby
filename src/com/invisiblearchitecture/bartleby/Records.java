/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.invisiblearchitecture.scraper.Utils;

/**
 * @author john
 *
 */
public class Records {
	public static final String NAME = "Name";
	public static final String ADDRESS = "Address";
	public static class Party {
		public final String name;
		public final UniversalAddress address;
		public final AbstractDocument document;
		/**
		 * A Party can be initialized with a name and address components.
		 * @param n Name.  A NullPointerException is thrown if this is null.
		 * @param a A UniversalAddress. Optional.
		 * @param a Document of origin.  Optional.
		 */
		public Party(String n, UniversalAddress a, AbstractDocument doc) throws IllegalArgumentException {
			name = n;
			processName(name);
			
			if(a != null) {
				address = a;
			} else {
				address = null;
			}
			if(doc != null) {
				document = doc;
			} else {
				document = null;
			}
		}
		
		/**
		 * Initialize a Party with name only.  The address and document will be null.
		 * @param n
		 */
		public Party(String n) throws IllegalArgumentException {
			name = processName(n);
			
			address = null;
			document = null;
		}
		
		public String toString() {
			if(address != null) {
				return name + " at " + address;
			} else {
				return name;
			}
		}
		
		/**
		 * Test whether the parties are the same based off of identical non-null addresses or
		 * similar names.
		 * @param that
		 * @return
		 */
		public boolean equals(Party that) {
			if(this.address != null && that.address != null) {
				if(this.address.equals(that.address))
					return true;
			}
			if(this.name.indexOf(that.name) != -1 || that.name.indexOf(this.name) != -1)
				return true;
			return false;
		}
		
		private String processName(String name) throws IllegalArgumentException {
			if(name == null) {
				throw new NullPointerException("Must have a non-null name to create a Document.Party.");
			} else {
				name = name.trim();
				if(name.equals(""))
					throw new IllegalArgumentException("Cannot have blank party name.");
				return name;
			}
		}
		
		/**
		 * Create an array of parties from an array of names.  Addresses and Documents in these parties will be null.
		 * @param names
		 * @return
		 */
		public static Party[] fromNames(String[] names) {
			if(names == null) return null;
			// If addresses & names aren't of same length, just make Parties with names.
			//Party[] parties = new Party[names.length];
			
			List<Party> parties = new ArrayList<Party>(names.length);
			for(int i = 0; i < names.length; i++) {
				try {
					parties.add(new Party(names[i]));
				} catch(IllegalArgumentException e) {};
			}
			
			return parties.toArray(new Party[0]);
		}

		/**
		 * Create an array of parties from an array of names and a Document.  Addresses in these parties will be null.
		 * @param names
		 * @return
		 */
		public static Party[] fromNamesAndDocument(String[] names, AbstractDocument document) {
			if(names == null) return null;
			// If addresses & names aren't of same length, just make Parties with names.

			List<Party> parties = new ArrayList<Party>(names.length);
			for(int i = 0; i < names.length; i++) {
				try {
					parties.add(new Party(names[i], null, document));
				} catch(IllegalArgumentException e) {};
			}
			
			return parties.toArray(new Party[0]);

		}
		
		/**
		 * Create an array of Parties from an array of names and addresses of the same length.  Must be from one Document.
		 * @param names
		 * @param addresses
		 * @param document
		 * @return
		 */
		public static Party[] fromNamesAddressesAndDocument(String[] names, UniversalAddress[] addresses, AbstractDocument document) {
			if(names == null) return null;

			if(addresses == null)
				return fromNamesAndDocument(names, document);
			if(addresses.length == names.length) {
				List<Party> parties = new ArrayList<Party>(names.length);
				for(int i = 0; i < names.length; i++) {
					try {
						parties.add(new Party(names[i], addresses[i], document));
					} catch(IllegalArgumentException e) {};
				}
				
				return parties.toArray(new Party[0]);
				
			} else {
				return fromNamesAndDocument(names, document);
			}
			
			
		}
		
		/**
		 * Create an array of parties with an array of names, and parallel arrays of stret numbers, street names, cities and zips.
		 * Document is null.  The parallel arrays may be null, but may not be of a different length.
		 * @param names
		 * @param streetNums
		 * @param streetNames
		 * @param cities
		 * @param zips
		 * @return
		 */
		public static Party[] fromNamesAndAddressComponents(String[] names, String[] streetNums, String[] streetNames, String[] cities, String[] zips) {
			if(names == null) return null;
			List<Party> parties = new ArrayList<Party>(names.length);
			
			for(int i = 0; i < names.length; i++) {
				UniversalAddress address;
				// TODO: how to handle addresses? Reverse geocode now?
				/*try {
					address = new BasicAddress(streetNums[i] + ' ' + streetNames[i], cities[i], zips[i]);
				} catch(NullPointerException e) {*/
					address = null;
				//}
				try {
					parties.add(new Party(names[i], address, null));
				} catch(IllegalArgumentException e) {};
			
			}
			return parties.toArray(new Party[0]);
		}
		
		public static Party[][] fromAoAs(String[][] nameAoA, String[][] streetNumAoA, String[][] streetNameAoA, String[][] cityAoA, String[][] zipAoA) {
			if(nameAoA == null)
				return null;
			Party[][] partyAoA = new Party[nameAoA.length][];
			for(int i = 0; i < nameAoA.length; i++) {
				String[] streetNumArray = null;
				String[] streetNameArray = null;
				String[] cityArray = null;
				String[] zipArray = null;
				if(streetNumAoA != null) { streetNumArray = streetNumAoA[i]; }
				if(streetNameAoA != null) { streetNameArray = streetNameAoA[i]; }
				if(cityAoA != null) { cityArray = cityAoA[i]; }
				if(zipAoA != null) { zipArray = zipAoA[i]; }
				partyAoA[i] = fromNamesAndAddressComponents(nameAoA[i], streetNumArray, streetNameArray, cityArray, zipArray);
			}
			return partyAoA;
		}
		
		/**
		 * Compare two party arrays.  Returns the number of matches between the two arrays.
		 * @param thisArray
		 * @param thatArray
		 * @return
		 */
		public static int compareArrays(Party[] thisArray, Party[] thatArray) {
			if(thisArray == null || thatArray == null) return 0;
			int numMatches = 0;
			for(int i = 0; i < thisArray.length; i++) {
				for(int j = 0; j < thatArray.length; j++) {
					if(thisArray[i].equals(thatArray[j]))
						numMatches++;
				}
			}
			return numMatches;
		}
		
		/**
		 * Turns an array of parties into a string formatted like "Name A, Name B, and Name C".
		 * @param parties
		 * @return
		 */
		public static String namesFromArray(Party[] parties) {
			if(parties == null)
				return null;
			String concat = "";
			for(int i = 0; i < parties.length; i++) {
				concat += parties[i].name;
				if(i < parties.length - 2)
					concat += ", ";
				if(i == parties.length - 2)
					concat += ", and ";
			}
			return concat;
		}
	}
	
	public static class AbstractDocument {
		public final Party[] party1s;
		public final Party[] party2s;
		public final Date date;
		public final Float amount;
		public final String id;
		//public final int order;
		public final int type;
		
		public static final int MORTGAGE = 1;
		public static final int ASSIGNMENT = 2;
		public static final int SATISFACTION = 3;
		public static final int DEED = 4;
		public static final int LIS_PENDENS = 5;
		
		public AbstractDocument(int t,
				Party[] p1s, Party[] p2s,
				String d, Float a, String n_id, int n_order) {
			party1s = p1s;
			party2s = p2s;
			type = t;
			Date _d;
			try {
				_d = new Date(d);
			} catch(Exception e) {
				_d = null;
			}
			date = _d;
			amount = a;
			id = n_id;
		}
		
		public AbstractDocument(int t, AbstractDocument d) {
			if(t != d.type) throw new IllegalArgumentException();
			party1s = d.party1s;
			party2s = d.party2s;
			type = d.type;
			date = d.date;
			amount = d.amount;
			id = d.id;
		}
				
		public String toString() {
			return Party.namesFromArray(party1s) + " to " + Party.namesFromArray(party2s) +
				" for " + amount + " on " + date + " in document " + id;
		}
		public int relationTo(AbstractDocument that) {
			if(that.date == null || this.date == null)
				return Date.UNKNOWN;
			return(this.date.relationTo(that.date));
		}
		
		/**
		 * Create an array of AbstractDocuments from arrays of components.  Scaled to the first dimension of the party1sAoA.
		 * @return
		 * @throws IndexOutOfBoundsException Throws this if one of the component arrays is longer than party1names.
		 */
		public static AbstractDocument[] fromArrays(
				int type, Party[][] party1sAoA, Party[][] party2sAoA,
				String[] dates, String[] amounts, String[] ids) throws IndexOutOfBoundsException {
			AbstractDocument[] documents = new AbstractDocument[party1sAoA.length];
			
			for(int i = 0; i < party1sAoA.length; i++) {
				Party[] party1array = party1sAoA[i];
				Party[] party2array = party2sAoA[i];
				String date;
				Float amount;
				String id;
				if(dates == null) {
					date = null;
				} else {
					date = dates[i];
				}
				if(amounts == null) {
					amount = null;
				} else {
					amount = Utils.stringToFloat(amounts[i]);
				}
				if(ids == null) {
					id = null;
				} else {
					id = ids[i];
				}
				documents[i] = new AbstractDocument(type, party1array, party2array, date, amount, id, i);
			}
			return documents;
		}
		public static Deed[] toDeeds(AbstractDocument[] documents) {
			if(documents == null) return null;
			Deed[] deeds = new Deed[documents.length];
			for(int i = 0; i < documents.length; i++) {
				deeds[i] = new Deed(documents[i]);
			}
			return deeds;
		}
		public static Mortgage[] toMortgages(AbstractDocument[] documents) {
			if(documents == null) return null;
			Mortgage[] mortgages = new Mortgage[documents.length];
			for(int i = 0; i < mortgages.length; i++) {
				mortgages[i] = new Mortgage(documents[i]);
			}
			return mortgages;
		}
		public static Assignment[] toAssignments(AbstractDocument[] documents) {
			if(documents == null) return null;
			Assignment[] assignments = new Assignment[documents.length];
			for(int i = 0; i < assignments.length; i++) {
				assignments[i] = new Assignment(documents[i]);
			}
			return assignments;
		}
		public static Satisfaction[] toSatisfactions(AbstractDocument[] documents) {
			if(documents == null) return null;
			Satisfaction[] satisfactions = new Satisfaction[documents.length];
			for(int i = 0; i < satisfactions.length; i++) {
				satisfactions[i] = new Satisfaction(documents[i]);
			}
			return satisfactions;
		}

		public static LisPendens[] toLisPendens(AbstractDocument[] documents) {
			if(documents == null) return null;
			LisPendens[] lisPendens = new LisPendens[documents.length];
			for(int i = 0; i < lisPendens.length; i++) {
				lisPendens[i] = new LisPendens(documents[i]);
			}
			return lisPendens;
		}
	}
	
	public static class Deed extends AbstractDocument {
		public Deed(Party[] p1s,
				Party[] p2s, String d,
				Float a, String n_id, int n_order) {
			super(DEED, p1s, p2s, d, a, n_id, n_order);
		}
		public Deed(AbstractDocument document) { super(DEED, document); }
		public final Party[] buyers = party2s;
		public final Party[] sellers = party1s;
		public final Float price = amount;
	}
	public static class Mortgage extends AbstractDocument {
		public Mortgage(Party[] p1s, Party[] p2s,  String d, Float a, String n_id,
				int n_order) {
			super(MORTGAGE, p1s, p2s, d, a, n_id, n_order);
		}
		public Mortgage(AbstractDocument document) { super(MORTGAGE, document); }
		public final Party[] lenders = party2s;
		public final Party[] borrowers = party1s;
		public final Float principal = amount;
		
		/**
		 * Later assignment of this mortgage, null if there is none.
		 */
		public Assignment assignment = null;
		
		/**
		 * Later satisfaction of this mortgage, null if there is none.
		 */
		public Satisfaction satisfaction = null;
		
	}
	
	public abstract static class DocumentFromAssignment extends AbstractDocument {
		public DocumentFromAssignment(int t, Party[] p1s, Party[] p2s, String d, Float a, String n_id,
				int n_order) {
			super(t, p1s, p2s, d, a, n_id, n_order);
		}
		public DocumentFromAssignment(int t, AbstractDocument document) { super(t, document); }
		public int previousDocumentType = 0;
		private AbstractDocument prevDoc = null;
		public void setPreviousDocument(int documentType, AbstractDocument prev) {
			if(prevDoc != null)
				throw new IllegalStateException("Previous document already assigned to this document.");
			previousDocumentType = documentType;
			prevDoc = prev;
		}
		
		public AbstractDocument previousDocument() {
			return prevDoc;
		}
	}
	
	public static class Assignment extends DocumentFromAssignment {
		public Assignment(Party[] p1s, Party[] p2s, String d, Float a, String n_id,
				int n_order) {
			super(ASSIGNMENT, p1s, p2s, d, a, n_id, n_order);
		}
		public Assignment(AbstractDocument document) { super(ASSIGNMENT, document); }
		public final Party[] assignees = party2s;
		public final Party[] assignors = party1s;
		public final Float principal = amount;
				
		/**
		 * Later assignment of this mortgage, null if there is none.
		 */
		public Assignment assignment = null;
		
		/**
		 * Later satisfaction of this mortgage, null if there is none.
		 */
		public Satisfaction satisfaction = null;
		
	}
	
	public static class Satisfaction extends DocumentFromAssignment {
		
		public Satisfaction(Party[] p1s, Party[] p2s, String d, Float a, String n_id,
				int n_order) {
			super(SATISFACTION, p1s, p2s, d, a, n_id, n_order);
		}
		public Satisfaction(AbstractDocument document) { super(SATISFACTION, document); }
		public final Party[] lenders = party2s;
		public final Party[] borrowers = party1s;
		public final Float principal = amount;
		public Mortgage mortgage = null;
	}
	
	public static class LisPendens extends AbstractDocument {
		public LisPendens(Party[] p1s, Party[] p2s, String d, Float a, String n_id,
				int n_order) {
			super(LIS_PENDENS, p1s, p2s, d, a, n_id, n_order);
		}
		public LisPendens(AbstractDocument document) { super(LIS_PENDENS, document); }
		public final Party[] lenders = party1s;
		public final Party[] borrowers = party2s;
		public final Float principal = amount;
	}

	/**
	 * An analyzer can take a set of documents and find the maximum amount of information about
	 * the current owners, as well as the active mortgage note holders.
	 * @author john
	 *
	 */
	public static class Analyzer {
		public final Party[] currentLenders;
		public final Party[] currentOwners;
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
		public Analyzer(Party[] currentOwnersNaive,
				Deed[] deeds, Mortgage[] mortgages, Assignment[] assignments,
				Satisfaction[] satisfactions, LisPendens[] lisPendens) {
						
			crossReference(mortgages, assignments, satisfactions);
						
			Deed[] armsLengthDeeds = armsLengthDeeds(deeds);
			Mortgage[] recentMortgages;
			
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
			
			if(currentOwnersNaive != null) {
				currentOwners = currentOwnersNaive;
			} else if(mostRecentDocument(deeds) != null) {
				currentOwners = new Deed(mostRecentDocument(deeds)).buyers;
			} else if(mostRecentDocument(recentMortgages) != null) {
				currentOwners = new Mortgage(mostRecentDocument(recentMortgages)).borrowers;
			} else if(mostRecentDocument(satisfactions) != null) {
				currentOwners = new Satisfaction(mostRecentDocument(satisfactions)).borrowers;
			} else {
				currentOwners = null;
			}
		}
		
		public static AbstractDocument mostRecentDocument(AbstractDocument[] documents) {
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
		private static Mortgage[] mortgagesAfterDocument(Mortgage[] mortgages, AbstractDocument document) {
			if(document != null) {
				List<Mortgage> mortgagesAfter = new ArrayList<Mortgage>(mortgages.length);
				for(int i = 0; i < mortgages.length; i++) {
					Mortgage mortgage = mortgages[i];
					if(mortgage.relationTo(document) == Date.SAME || mortgage.relationTo(document) == Date.AFTER)
						mortgagesAfter.add(mortgage);
				}
				
				return(mortgagesAfter.toArray(new Mortgage[0]));
			} else {
				return mortgages;
			}
		}
		
		/**
		 * Identifies active lenders from a set of mortgages.  Most effective if crossReference() has been
		 * run beforehand.
		 * @return
		 */
		private static Party[] lendersFromMortgages(Mortgage[] mortgages) {
			Vector<Party> lendersVector = new Vector<Party>();
			for(int i = 0; i < mortgages.length; i++) {
				Mortgage mortgage = mortgages[i];
				if(mortgage.satisfaction == null) {
					if(mortgage.assignment == null) {
						Utils.arrayIntoVector(mortgage.lenders, lendersVector);
					} else {
						Assignment assignment = mortgage.assignment;
						while(assignment.assignment != null) {
							assignment = assignment.assignment;
						}
						if(assignment.satisfaction == null)
							Utils.arrayIntoVector(assignment.assignees, lendersVector);
					}
				}
			}
			Party[] lendersArray = new Party[lendersVector.size()];
			lendersVector.copyInto(lendersArray);
			return lendersArray;
		}
		
		/**
		 * Eliminate deeds with similar party names and amounts under $1,000.
		 * @param deeds
		 * @return
		 */
		private static Deed[] armsLengthDeeds(Deed[] allDeeds) {
			if(allDeeds == null)
				return null;
			Vector<Deed> deedsVector = new Vector<Deed>(allDeeds.length, 1);
			for(int i = 0; i < allDeeds.length; i++) {
				Deed deed = allDeeds[i];
				if(Party.compareArrays(deed.buyers, deed.sellers) > 0 && deed.price < 1000) {
					// negative!
				} else {
					deedsVector.addElement(deed);
				}
			}
			Deed[] armsLengthDeeds = new Deed[deedsVector.size()];
			deedsVector.copyInto(armsLengthDeeds);
			return armsLengthDeeds;
		}
		
		/**
		 * Make documents cross-reference one another.
		 * @param mortgages
		 * @param assignments
		 * @param satisfactions
		 */
		private static void crossReference(Mortgage[] mortgages, Assignment[] assignments, Satisfaction[] satisfactions) {
			// Cross reference mortgages to assignments.
			if(assignments != null && mortgages != null) {
				for(int i = 0; i < assignments.length; i++) {
					Assignment assignment = assignments[i];
					for(int j = 0; j < mortgages.length; j++) {
						Mortgage mortgage = mortgages[j];
						if(Party.compareArrays(assignment.assignors, mortgage.lenders) > 0
								&& assignment.previousDocument() == null
								&& mortgage.assignment == null
								&& mortgage.relationTo(assignment) == Date.BEFORE || mortgage.relationTo(assignment) == Date.SAME) {
							assignment.setPreviousDocument(AbstractDocument.MORTGAGE, mortgage);
							mortgage.assignment = assignment;
						}
					}
				}
			}
			
			// Cross reference satisfactions to assignments.			
			if(satisfactions != null && assignments != null) {
				for(int i = 0; i < satisfactions.length; i++) {
					Satisfaction satisfaction = satisfactions[i];
					for(int j = 0; j < assignments.length; j++) {
						Assignment assignment = assignments[j];
						if(Party.compareArrays(satisfaction.lenders, assignment.assignees) > 0
								&& assignment.satisfaction == null
								&& satisfaction.previousDocument() == null
								&& assignment.relationTo(satisfaction) == Date.BEFORE) {
							satisfaction.setPreviousDocument(AbstractDocument.ASSIGNMENT, assignment);
							assignment.satisfaction = satisfaction;
						}
					}
				}
			}
			
			// Cross reference satisfactions to mortgages.
			if(satisfactions != null && mortgages != null) {					
				for(int i = 0; i < satisfactions.length; i++) {
					Satisfaction satisfaction = satisfactions[i];
					for(int j = 0; j < mortgages.length; j++) {
						Mortgage mortgage = mortgages[j];
						if(Party.compareArrays(satisfaction.borrowers, mortgage.borrowers) > 0
								&& mortgage.satisfaction == null
								&& satisfaction.mortgage == null
								&& mortgage.relationTo(satisfaction) == Date.BEFORE) {
							satisfaction.mortgage = mortgage;
							mortgage.satisfaction = satisfaction;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Sum the amounts of any set of documents.  Documents with null amounts are considered 0.
	 * @param documents
	 * @return
	 */
	public static float sum(AbstractDocument[] documents) {
		float sum = 0;
		for(int i = 0; i < documents.length; i++) {
			AbstractDocument doc = documents[i];
			if(doc.amount != null) {
				sum += doc.amount;
			}
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
		public Date(String dateLine) throws NumberFormatException {
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
		public int relationTo(Date that) {
			if(equals(that) == true)
				return SAME;
			if(isAfter(that) == true)
				return AFTER;
			return BEFORE;
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
