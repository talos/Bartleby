/**
 * Geogrape
 * A project to enable public access to public building information.
 */
package com.invisiblearchitecture.bartleby;

import com.invisiblearchitecture.bartleby.R;
import com.invisiblearchitecture.scraper.Information;
import com.invisiblearchitecture.scraper.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author john
 *
 */
public final class PropertyInfoDialog extends Dialog {
	private TextView ownersHeader;
	private TextView lendersHeader;
	private TextView violationsComplaintsHeader;
	
	private TextView ownersList;
	private TextView lendersList;
	private TextView originalLoansPrincipalText;
	
	private ProgressBar progress;
	
	private TextView noOwner;
	private TextView noLenders;
	private TextView purchasePriceView;
	private TextView purchaseDateView;
	private TextView complaintsView;
	private TextView violationsView;
	
	// Show this progressDialog whenever the property info is first blanked.
	private Context context = this.getContext();
	
	// The information updater works on the propertyInfoDialog, and lives here.
	//public final InformationUpdater informationUpdater = new InformationUpdater();
	
	public PropertyInfoDialog(Context context) {
		super(context);
		this.setCanceledOnTouchOutside(true);
		
		setContentView(R.layout.property_info_dialog);
		complaintsView = (TextView) findViewById(R.id.complaints_view);
		violationsView = (TextView) findViewById(R.id.violations_view);
		
		progress = (ProgressBar) findViewById(R.id.progress);
		//ownersProgress = (ProgressBar) findViewById(R.id.owners_progress);
		//lendersProgress = (ProgressBar) findViewById(R.id.lenders_progress);
		
		noOwner = (TextView) findViewById(R.id.no_owner);
		noLenders = (TextView) findViewById(R.id.no_lenders);
		originalLoansPrincipalText = (TextView) findViewById(R.id.original_loans_principal);
		
		violationsComplaintsHeader = (TextView) findViewById(R.id.violations_complaints_header);
		ownersHeader = (TextView) findViewById(R.id.owners_header);
		lendersHeader = (TextView) findViewById(R.id.lenders_header);
		
		purchasePriceView = (TextView) findViewById(R.id.purchase_price);
		purchaseDateView = (TextView) findViewById(R.id.purchase_date);
		
		ownersList = (TextView) findViewById(R.id.owners_list);
		lendersList = (TextView) findViewById(R.id.lenders_list);
	}
	
	public void publish(Information property, int partProgress, int totalProgress) {
		setTitle("");
		
		progress.setMax(totalProgress);
		progress.setProgress(partProgress);
		progress.setVisibility(View.VISIBLE);
		
		noOwner.setVisibility(View.GONE);
		ownersList.setVisibility(View.GONE);
		ownersHeader.setVisibility(View.GONE);
		
		noLenders.setVisibility(View.GONE);
		lendersList.setVisibility(View.GONE);
		lendersHeader.setVisibility(View.GONE);
		
		purchasePriceView.setVisibility(View.GONE);
		purchaseDateView.setVisibility(View.GONE);
		
		violationsComplaintsHeader.setVisibility(View.GONE);
		violationsView.setVisibility(View.GONE);
		complaintsView.setVisibility(View.GONE);		
		
		
		
		/*
		setTitle(property.getAddress().toString());			
		
		Information[] owners = property.getCurrentOwners();
		Information[] deeds = property.getDeeds();
		Information[] mortgages =property.getMortgages();
		Information[] assignments = property.getAssignments();
		Information[] satisfactions = property.getSatisfactions();
		Information[] lisPendens = property.getLisPendens();
		
		Analyzer analyzer = new Analyzer(owners, deeds, mortgages, assignments, satisfactions, lisPendens);

		Information[] currentOwners = analyzer.currentOwners;
		if(currentOwners != null) {
			if(currentOwners.length > 0) {
				ownersHeader.setVisibility(View.VISIBLE);
				ownersList.setText(Utils.joinWithCommasAnd(Information.getFieldArray(currentOwners, Records.NAME)));				
				ownersList.setVisibility(View.VISIBLE);
			} else {
				noOwner.setVisibility(View.VISIBLE);
			}
		} else {
		}
		
		Information[] currentLenders = analyzer.currentLenders;
		if(currentLenders != null) {
			if(currentLenders.length > 0) {
				lendersHeader.setVisibility(View.VISIBLE);
				lendersList.setText(Utils.joinWithCommasAnd(Information.getFieldArray(currentLenders, Records.NAME)));
				lendersList.setVisibility(View.VISIBLE);
			} else {
				noLenders.setVisibility(View.VISIBLE);
			}
		} else {
		}
		
		Float originalLoansPrincipal = analyzer.originalLoansPrincipal;
		if(originalLoansPrincipal != null) {
			
			originalLoansPrincipalText.setVisibility(View.VISIBLE);
		}
		
		String violationsCount = property.getViolations();
		if(violationsCount != null) {
			violationsComplaintsHeader.setVisibility(View.VISIBLE);
			violationsView.setVisibility(View.VISIBLE);
			violationsView.setText(context.getString(R.string.violations_count, violationsCount));
		}
		
		String complaintsCount = property.getComplaints();
		if(complaintsCount != null) {
			violationsComplaintsHeader.setVisibility(View.VISIBLE);
			complaintsView.setVisibility(View.VISIBLE);
			complaintsView.setText(context.getString(R.string.complaints_count, complaintsCount));
		}*/
	}
}
