package com.invisiblearchitecture.bartleby;

import java.io.IOException;
import java.util.Locale;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ZoomControls;

import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.invisiblearchitecture.bartleby.BartlebyLocation.LocationResult;
import com.invisiblearchitecture.bartleby.BartlebyOverlay.BartlebyOverlayItem;
import com.invisiblearchitecture.scraper.Information;
import com.invisiblearchitecture.scraper.JSONInformationFactory;
import com.invisiblearchitecture.scraper.JSONInterface;
import com.invisiblearchitecture.scraper.Publisher;
import com.invisiblearchitecture.scraper.HttpInterface;
import com.invisiblearchitecture.scraper.LogInterface;
import com.invisiblearchitecture.scraper.RegexInterface;
import com.invisiblearchitecture.scraper.impl.ApacheHttpInterface;
import com.invisiblearchitecture.scraper.impl.JSONME;
import com.invisiblearchitecture.scraper.impl.JavaUtilRegexInterface;
import com.invisiblearchitecture.bartleby.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.StateListDrawable;
import android.location.Geocoder;
import android.location.Location;

import android.view.View.OnClickListener;


public class Bartleby extends MapActivity {	

	public static final HttpInterface httpInterface = new ApacheHttpInterface();
	public static final RegexInterface regexInterface = new JavaUtilRegexInterface();
	public static final LogInterface logger = new AndroidLogInterface();
	public static final JSONInterface jsonInterface = new JSONME();
	
	// TODO: Clean this area up.
	private MapView mapView;
	private BartlebyOverlay propertyOverlay;
	private MapController mapController;
	private TextView error_view;
	private Button lookup_button;
	private ZoomControls zoomControls;
	
	private ProgressDialog progressDialog;
	//private AlertDialog alertDialog;
	
	private final Context context = this;
	
	// Address display
	private EditText streetNumEditText;
	private EditText streetNameEditText;
	private EditText cityEditText;
	
	private Geocoder geocoder;
	
	// Information display
	private PropertyInfoDialog propertyInfoDialog;
	//private InformationUpdater informationUpdater;
	
	// Dialog constants.
	private final int propertyInfoDialogID = 1;
	private final int wrongAddressAlertDialogID = 2;
	
	private JSONInformationFactory factory;
			
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		try {
		    // Set up dialog info display.
		    propertyInfoDialog = new PropertyInfoDialog(this);
		    progressDialog = new ProgressDialog(this);

			// Set up property factory.
			factory = new JSONInformationFactory("http://www.simplescraper.net:4567/client", "test", httpInterface, logger,
					regexInterface, jsonInterface,
					new AsyncCollector(false), new Publisher() {
						@Override
						public void publishProgress(Information information,
								int progressPart, int progressTotal) {
							//publishAddress(information);
							logger.i("publishing progress " + Integer.toString(progressPart) + " of "  + Integer.toString(progressTotal) +  " on propertyinfodialog");
							propertyInfoDialog.publish(information, progressPart, progressTotal);							
						}
						@Override
						public void publish(Information information) {
							//publishAddress(information);
							logger.i("publishing propertyinfodialog");
							propertyInfoDialog.publish(information, 1, 1);
						}
			});
			
			lookup_button = (Button) findViewById(R.id.button_lookup);
			error_view = (TextView) findViewById(R.id.text_error);
			
			// Set up address inputs.
			streetNumEditText = (EditText) findViewById(R.id.streetNum);
			streetNameEditText = (EditText) findViewById(R.id.streetName);
			
			cityEditText = (EditText) findViewById(R.id.city);
			
			mapView = (MapView) findViewById(R.id.mapview);
			mapView.setSatellite(true);
			
			geocoder = new Geocoder(this, Locale.getDefault());
			
			zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
			zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                    mapController.zoomIn();
		            }
		    });
		    zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                    mapController.zoomOut();
		            }
		    });
			
			mapController = mapView.getController();
			mapController.setZoom(18);
			
			// Add the GeograpeOverlay, which is itemized.
			StateListDrawable marker = (StateListDrawable) this.getResources().getDrawable(R.drawable.marker);
					
			propertyOverlay = new BartlebyOverlay(marker, mapController);
			
			// An overlay to receive clicks that fell through PropertyOverlay.
			Overlay bottomOverlay = new Overlay() {
				/* (non-Javadoc)
				 * @see com.google.android.maps.Overlay#onTouchEvent(android.view.MotionEvent, com.google.android.maps.MapView)
				 */
				@Override
				public boolean onTap(GeoPoint gp, MapView mapView) {
					try {
				        addPropertyAtPoint(gp);
						return true;
					} catch(Exception e) {
						error_view.setText(e.toString());
						e.printStackTrace();
						return false;
					}
				}
			};
			mapView.getOverlays().add(bottomOverlay);
			mapView.getOverlays().add(propertyOverlay);
			
			BartlebyLocation myLocation = new BartlebyLocation();
			myLocation.getLocation(this, new HandleLocationUpdate());
			
			// What to do when the user asks for information.
			lookup_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String streetNum = streetNumEditText.getText().toString();
					String streetName = streetNameEditText.getText().toString();
					String city = cityEditText.getText().toString();
					// Add the property if the user has changed the text inputs.
					// This will NOT add repetitive properties, as addProperty()
					// will check before adding.
					if(propertyOverlay.getFocus() != null) {
						UniversalAddress oldAddress = propertyOverlay.getFocus().address;
						if(oldAddress.equals(streetNum + " " + streetName, city) == false) {
							addPropertyAtEnteredString(streetNum, streetName, city);
						} else {
							showDialog(propertyInfoDialogID);
						}
					}
				}
			});
		} catch (Exception e) {
			logger.e("Deadly exception.", e);
		}
    }
    
    private class HandleLocationUpdate extends LocationResult {
		@Override
	    public void gotLocation(final Location location){
	        //Got the location!
			
			try {
				GeoPoint curPoint = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() *1E6));
		       // mapController.animateTo(curPoint);
		        addPropertyAtPoint(curPoint);
			} catch(Exception e) {
				error_view.setText(e.toString());
				e.printStackTrace();
			}
	    }
		public void fakeIt() {
			Location fakeLocation = new Location("Fake provider.");
			
			fakeLocation.setLatitude(40.627307916989615);
			fakeLocation.setLongitude(-73.96820068359375);
			/*fakeLocation.setLatitude(41.9);
			fakeLocation.setLongitude(-87.65);*/
			
			fakeLocation.setAccuracy(10);
			fakeLocation.setProvider("Fake Location");
			
			gotLocation(fakeLocation);
		}
	}
    
    
    // TODO: deprecated method as of API Level 8, should include newer version too.
    // This is only called the first time the dialog is created.
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    		case(propertyInfoDialogID):
    			BartlebyOverlayItem gItem = propertyOverlay.getFocus();
    			if(gItem != null) {
    				return propertyInfoDialog;
    			} else {
    				return null;
    			}
    		case(wrongAddressAlertDialogID):
    			return null;
    	}
    	return null;
    }
    /*
    // This is called every time the dialog is brought up.
    // 
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	switch(id) {
    		// Refresh property info dialog with the currently focused property.
    		case(propertyInfoDialog):
    			((PropertyInfoDialog) dialog).refresh(overlay.getFocus());
    			break;
    	}
    }
    */
    
	public void publishAddress(UniversalAddress address) {
		streetNumEditText.setText("");
		streetNameEditText.setText("");
		
		logger.i("updating address");
		streetNumEditText.setText(address.streetNumber());
		streetNameEditText.setText(address.streetNameFull());
		cityEditText.setText(address.city());
	}
    
    // Required by MapActivity.
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    /**
     * Asynchronously add a property via a clicked point.
     */
    private void addPropertyAtPoint(GeoPoint gp) {
		final GoogleAddress.ViaGeocoder createProperty = new GoogleAddress.ViaGeocoder(geocoder, gp) {
			@Override
			protected void handleNull() {
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(getString(R.string.error_reverse_geocode));
				AlertDialog alert = builder.create();
				alert.show();
			}
			@Override
			protected void handleCancelled() {
				progressDialog.dismiss();
			}
			@Override
			protected void handleAddress(UniversalAddress address) {
    			try {
    				BartlebyInformation info = correctPropertyFromAddress(address);
	    			propertyOverlay.addProperty(info);
	    			progressDialog.dismiss();
					showDialog(propertyInfoDialogID);
    			} catch(Exception e) {
    				logger.e("Error obtaining information.", e);
    				handleNoInformationType(address);
    			}
			}
			@Override
			protected void handleNoInformationType(UniversalAddress address) {
				warnUserNoInformationTypeForAddress(address);
				progressDialog.dismiss();
			}
		};
		progressDialog.setMessage("Finding address from selection...");
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				createProperty.cancel(true);
			}
		});
		progressDialog.show();
		createProperty.execute();
    }
    
    /**
     * Asynchronously add a property via a streetNum/streetName/city combo.
     */
    private void addPropertyAtEnteredString(final String streetNum, final String streetName, final String city) {
    	final GoogleAddress.ViaGeocoder createProperty = new GoogleAddress.ViaGeocoder(geocoder,
    			streetNum, streetName, city) {
    		@Override
    		protected void handleNull() {
    			progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(getString(R.string.error_geocode, streetNum, streetName, city));
				AlertDialog alert = builder.create();
				alert.show();
    		}
    		@Override
    		protected void handleCancelled() {
    			progressDialog.dismiss();
    		}
    		@Override
    		protected void handleAddress(UniversalAddress address) {
    			try {
    				BartlebyInformation info = correctPropertyFromAddress(address);
	    			propertyOverlay.addProperty(info);
	    			publishAddress(address);
	    			progressDialog.dismiss();
					showDialog(propertyInfoDialogID);
    			} catch(Exception e) {
    				logger.e("Error obtaining information.", e);
    				handleNoInformationType(address);
    			}
    		}
			@Override
			protected void handleNoInformationType(UniversalAddress address) {
				warnUserNoInformationTypeForAddress(address);
				progressDialog.dismiss();
			}
    	};
    	progressDialog.setMessage("Locating " + streetNum + " " + streetName + ", " + city + "...");
    	progressDialog.setCancelable(true);
    	progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				createProperty.cancel(true);
			}
		});
    	progressDialog.show();
    	createProperty.execute();
    }
    
    /**
     * Get the correct property type from a UniversalAddress
     * @param address
     * @return Returns a single property.
     * @throws IOException if there was a problem using the factory.
     */
    private BartlebyInformation correctPropertyFromAddress(UniversalAddress address) throws Exception {
    	return new BartlebyInformation(factory.get("US Zip " + address.zip(),"Property"), address);
    }
    
    private void warnUserNoInformationTypeForAddress(UniversalAddress address) {
    	// TODO
    	logger.i("Could not find an information type for address " + address.toString());
    }
}