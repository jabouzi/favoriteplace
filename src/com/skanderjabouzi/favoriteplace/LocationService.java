package com.skanderjabouzi.favoriteplace;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.os.Bundle;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import java.util.Locale;
import java.util.TimeZone;
import java.io.IOException;
import android.app.Service;
import java.util.List;
import java.io.IOException;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
import android.os.Handler;
import android.location.Address;
import android.location.Geocoder;
import java.util.Arrays;
import android.widget.Toast;



public class LocationService extends Service implements LocationListener{

	private static final String TAG = "LocationService";
	public static final String LOCATION_INTENT = "com.skanderjabouzi.favoriteplace.LOCATION_INTENT";
    public static final String LOCATION = "LOCATION";
    public static final String RECEIVE_LOCATION_NOTIFICATIONS = "com.skanderjabouzi.favoriteplace.RECEIVE_LOCATION_NOTIFICATIONS";
    private LocationManager locationManager;
    private final Context context = LocationService.this;
    boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	boolean runService = true;
	double latitude; 
	double longitude; 
	private String Address1 = "", Address2 = "", City = "", State = "", Country = "", County = "", PIN = "";
	Location location;
	TimeZone tz;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	private int mInterval = 5000;
	private final Handler mHandler = new Handler();
	private Runnable mUpdateTimeTask;
	private LocationDataSource ldatasource;
	private com.skanderjabouzi.favoriteplace.Location salatLocation;
	int saveLocation = 0;
	String receiverSource = "";
    
    @Override
     public IBinder onBind(Intent arg0) {
		return null;
    }
    
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ldatasource = new LocationDataSource(this);
		ldatasource.open();
		saveLocation = Integer.parseInt(intent.getStringExtra("SAVE"));
		receiverSource = intent.getStringExtra("SOURCE");
		Log.i(TAG,"SOURCE : " + receiverSource);
		Log.d(TAG, String.valueOf(getTimeZone()));
		mUpdateTimeTask = new Runnable() {
			public void run() {
				Log.i(TAG,"runService : " + runService);
				getLocation();
				if (runService)	mHandler.postDelayed (mUpdateTimeTask, mInterval);
			}
		};
		mHandler.removeCallbacks(mUpdateTimeTask);
		if (runService)	mHandler.postDelayed(mUpdateTimeTask, 100);
		

		return super.onStartCommand(intent, flags, startId);
	}
    
    public void getLocation() {

		if (saveLocation == 1 && receiverSource.equals("TIMEZONE"))
		{
			ldatasource.updateTimeZoneLocation(getTimeZone());
			Log.i(TAG,"SAVE_TIMEZONE_LOCATION 1");
		}

		try {
			locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
	
			} 
			else 
			{
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}

				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}
		}		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		getGeoLocation();
	}
	
	public void getGeoLocation() {
		if (location == null)
		{
			Log.i(TAG,"LOCATION == NULL");
		}
		else
		{
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			Log.i(TAG,"LAT : "+latitude);
			Log.i(TAG,"LON : "+longitude);
			
			Geocoder geocoder= new Geocoder(this, Locale.ENGLISH);
		
			try {
        	  
			  //Place your latitude and longitude
			  List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);
			  if(addresses != null && !addresses.isEmpty()) {
				  Log.i("GEOLOC", java.util.Arrays.asList(addresses.toArray()).toString());
				  if ( addresses.get(0).getLocality() != null) Log.i("CITY", addresses.get(0).getLocality());
				  if ( addresses.get(0).getCountryName() != null) Log.i("COUN", addresses.get(0).getCountryName());
				  if ( addresses.get(0).getAdminArea() != null) Log.i("ADMI", addresses.get(0).getAdminArea());
				  if ( addresses.get(0).getAddressLine(0) != null) Log.i("ADR1", addresses.get(0).getAddressLine(0));
				  if ( addresses.get(0).getAddressLine(1) != null) Log.i("ADR2", addresses.get(0).getAddressLine(1));
			  }
        	 
        	  //if(addresses != null && !addresses.isEmpty()) {
        	  
        		  //Address fetchedAddress = addresses.get(0);
        		  //StringBuilder strAddress = new StringBuilder();
        	   
        		  //for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
        			  	//strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
        		  //}
        	   
        		  //Log.i("GEOLOC", "I am at: " +strAddress.toString());
        	  
        	  //}
        	  
        	  else
        		  Log.i("GEOLOC","No location found..!");
         
        
				
				//String locationValues = String.valueOf(location.getLatitude());
				//locationValues += "|" + String.valueOf(location.getLongitude());
				//locationValues += "|" + String.valueOf(getTimeZone());
				//locationValues += "|" + City;
				//locationValues += "|" + Country;
				//if (saveLocation == 1 && receiverSource.equals("NETWORK") )
				//{
					//salatLocation = new com.skanderjabouzi.favoriteplace.Location();
					//salatLocation.setId(1);
					//salatLocation.setLatitude((float)location.getLatitude());
					//salatLocation.setLongitude((float)location.getLongitude());
					//salatLocation.setTimezone(getTimeZone());
					//salatLocation.setCity(City);
					//salatLocation.setCountry(Country);
					//ldatasource.updateLocation(salatLocation);
					//Log.i(TAG,"SAVE_LOCATION");
				//}
				//sendNotification(locationValues);
				 
			} 
			catch (IOException e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
					 Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
			}
			
			stopHandler();
			cleanLocation();
		}
	}

	
	public float getTimeZone()
	{
		tz = TimeZone.getDefault();
		return (tz.getRawOffset()/3600*1000+tz.getDSTSavings()/3600*1000)/1000000;
	}
	
	public void sendNotification(String extra)
	{
		runService = false;
		Intent intent;
		intent = new Intent(LOCATION_INTENT);
		intent.putExtra(LOCATION, extra);
		sendBroadcast(intent, RECEIVE_LOCATION_NOTIFICATIONS);
		Log.i(TAG,"SEND NOTIFICATION");
	}
    
    @Override
    public void onDestroy() 
    {
		Log.i(TAG,"destroy");
		if (ldatasource.isOpen()) ldatasource.close();
        super.onDestroy();
    }
    
    private void stopService()
    {
		Log.i(TAG,"stop");
		stopHandler();
		if (ldatasource.isOpen()) ldatasource.close();
        stopService(new Intent(this, LocationService.class));
    }

    public void onLocationChanged(Location location) {
		Log.i(TAG,"LocationChanged");
    }

    public void onProviderDisabled(String provider) {
		Log.i(TAG,"ProviderDisabled");
    }

    public void onProviderEnabled(String provider) {
		Log.i(TAG,"ProviderEnabled");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(TAG,"tatusChanged");
    }

    private void cleanLocation() {
		Log.i(TAG,"cleanLocation");
        locationManager.removeUpdates(this);
        stopService();
    }
    
    public void stopHandler() {
		Log.i(TAG,"stopHandler");
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
}
