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
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.Log;
import android.provider.Settings;

public class FragmentTab1 extends Fragment implements OnItemSelectedListener{
	
	static final String SEND_LOCATION_NOTIFICATIONS = "com.skanderjabouzi.favoriteplace.SEND_LOCATION_NOTIFICATIONS";
	private Spinner method, asr, hijri, autoLocation, adhan;
	private EditText latitude, longitude, timezone, city, country;
	private Button btnsaveSettings, btnDetectLocation;
	private OptionsDataSource odatasource;
	private Options options;
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
		rootView = inflater.inflate(R.layout.settings, container, false);
		receiver = new LocationReceiver();
        filter = new IntentFilter( LocationService.LOCATION_INTENT );
        locationIntent = new Intent(getActivity(), LocationService.class);
        odatasource = new OptionsDataSource(getActivity());
		odatasource.open();
		options = odatasource.getOptions(1);
		setSpinnerItemSelection();
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
        odatasource.open();
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
        odatasource.close();
    }
    
    @Override
    public void onStop() {
        super.onPause();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
			receiver = null;
		}
        ldatasource.close();
        odatasource.close();
    }
    
	@Override
    public void onDestroy() {
        super.onPause();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
			receiver = null;
		}
        ldatasource.close();
        odatasource.close();
    }
    
    public void setSpinnerItemSelection() {

		method = (Spinner) rootView.findViewById(R.id.calculation);
		method.setOnItemSelectedListener(this);
		pos = options.getMethod() - 1;
		if (pos < 0) pos = 0;
		method.setSelection(pos);

		asr = (Spinner) rootView.findViewById(R.id.asr);
		asr.setOnItemSelectedListener(this);
		pos = options.getAsr() - 1;
		if (pos < 0) pos = 0;
		asr.setSelection(pos);
		
		adhan = (Spinner) rootView.findViewById(R.id.adhan);
		adhan.setOnItemSelectedListener(this);
		pos = options.getAdhan() - 1;
		if (pos < 0) pos = 0;
		adhan.setSelection(pos);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

    public void setLocationTexts() {
		
		latitude = (EditText) rootView.findViewById(R.id.latitude);
		latitude.setText(fmt(location.getLatitude()));
		longitude = (EditText) rootView.findViewById(R.id.longitude);
		longitude.setText(fmt(location.getLongitude()));		
		timezone = (EditText) rootView.findViewById(R.id.timezone);
		timezone.setText(fmt(location.getTimezone()));
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
		
				method = (Spinner) rootView.findViewById(R.id.calculation);
				asr = (Spinner) rootView.findViewById(R.id.asr);
				
				options.setId(1);
				
				pos = method.getSelectedItemPosition() + 1;
				options.setMethod(pos);

				pos = asr.getSelectedItemPosition() + 1;
				options.setAsr(pos);
				
				options.setHijri(0);

				options.setAutoLocation(0);
				
				pos = adhan.getSelectedItemPosition() + 1;
				options.setAdhan(pos);

				odatasource.updateOptions(options);
		
				location.setId(1);
				latitude = (EditText) rootView.findViewById(R.id.latitude);
				location.setLatitude(Float.parseFloat(String.valueOf(latitude.getText())));

				longitude = (EditText) rootView.findViewById(R.id.longitude);
				location.setLongitude(Float.parseFloat(String.valueOf(longitude.getText())));

				timezone = (EditText) rootView.findViewById(R.id.timezone);
				location.setTimezone(Float.parseFloat(String.valueOf(timezone.getText())));

				city = (EditText) rootView.findViewById(R.id.city);
				location.setCity(String.valueOf(city.getText()));

				country = (EditText) rootView.findViewById(R.id.country);
				location.setCountry(String.valueOf(country.getText()));

				ldatasource.updateLocation(location);
				
				getActivity().finish();
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
				location.setTimezone(Float.parseFloat(geolocation[2]));
				location.setCity(geolocation[3]);
				location.setCountry(geolocation[4]);
				setLocationTexts();
			}
        }
    }
 
}
