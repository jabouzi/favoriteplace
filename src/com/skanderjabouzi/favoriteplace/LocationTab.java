package com.skanderjabouzi.favoriteplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.BroadcastReceiver;
import java.util.ArrayList;
import java.util.List;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.app.AlarmManager;
import android.widget.AdapterView;
import android.util.Log;
import android.provider.Settings;

public class LocationTab extends Fragment{
	
	static final String SEND_LOCATION_NOTIFICATIONS = "com.skanderjabouzi.favoriteplace.SEND_LOCATION_NOTIFICATIONS";
	private EditText latitude, longitude, city, country;
	private Button btnsaveSettings, btnDetectLocation;
	private int pos = 0;
	private LocationDataSource ldatasource;
	private Location location;
	private Context context = getActivity();
	private Intent locationIntent;
    LocationReceiver receiver;
    IntentFilter filter;
    View rootView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.location, container, false);
		receiver = new LocationReceiver();
        filter = new IntentFilter( LocationService.LOCATION_INTENT );
        locationIntent = new Intent(getActivity(), LocationService.class);
        ldatasource = new LocationDataSource(getActivity());
		ldatasource.open();
		location = ldatasource.getLocation(1);
		setLocationTexts();
		addListenerOnButton();
        
        return rootView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, filter, SEND_LOCATION_NOTIFICATIONS, null);
        ldatasource.open();
    }

    @Override
    public void onPause() {
        super.onPause();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
			receiver = null;
		}
        ldatasource.close();
    }
    
    @Override
    public void onStop() {
        super.onPause();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
			receiver = null;
		}
        ldatasource.close();
    }
    
	@Override
    public void onDestroy() {
        super.onPause();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
			receiver = null;
		}
        ldatasource.close();
    }

    public void setLocationTexts() {
		
		latitude = (EditText) rootView.findViewById(R.id.latitude);
		latitude.setText(fmt(location.getLatitude()));
		longitude = (EditText) rootView.findViewById(R.id.longitude);
		longitude.setText(fmt(location.getLongitude()));		
		city = (EditText) rootView.findViewById(R.id.city);
		city.setText(location.getCity());
		country = (EditText) rootView.findViewById(R.id.country);
		country.setText(location.getCountry());
	}

    public void addListenerOnButton() {

		btnsaveSettings = (Button) rootView.findViewById(R.id.saveSettings);
		btnsaveSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		
				location.setId(1);
				latitude = (EditText) rootView.findViewById(R.id.latitude);
				location.setLatitude(Float.parseFloat(String.valueOf(latitude.getText())));

				longitude = (EditText) rootView.findViewById(R.id.longitude);
				location.setLongitude(Float.parseFloat(String.valueOf(longitude.getText())));

				city = (EditText) rootView.findViewById(R.id.city);
				location.setCity(String.valueOf(city.getText()));

				country = (EditText) rootView.findViewById(R.id.country);
				location.setCountry(String.valueOf(country.getText()));

				ldatasource.updateLocation(location);
				
				Toast.makeText(getActivity(), R.string.savedsucess,	Toast.LENGTH_LONG).show();
			}

		});
		
		btnDetectLocation = (Button) rootView.findViewById(R.id.detectLocation);
		btnDetectLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LocationService.class);
				intent.putExtra("SAVE", "0");
				intent.putExtra("SOURCE", "SETTINGS");
				getActivity().startService(intent);
			}

		});

	}
	
	public void showSettingsAlert(int type) {
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Localisation error");
        alertDialog.setMessage("Please enable Internet and try again");
        if (type == 1) alertDialog.setMessage("Please enable GPS or Internet and try again");
 
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        alertDialog.show();
	}
	
	public String fmt(float d)
	{
		if(d == (int) d)
			return String.valueOf((int)d);
		else
			return String.valueOf(d);
	}
	
	class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String extraString = intent.getStringExtra("LOCATION");
            if (extraString.equals("GEO_NULL"))
            {
				showSettingsAlert(1);
			}
            else if (extraString.equals("LOCATION_NULL"))
            {
				showSettingsAlert(0);
			}
			else
			{
				String[] geolocation = extraString.split("\\|");
				location.setLatitude(Float.parseFloat(geolocation[0]));
				location.setLongitude(Float.parseFloat(geolocation[1]));
				location.setCity(geolocation[2]);
				location.setCountry(geolocation[3]);
				setLocationTexts();
			}
        }
    }
 
}
