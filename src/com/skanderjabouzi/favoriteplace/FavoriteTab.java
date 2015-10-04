package com.skanderjabouzi.favoriteplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.AlertDialog.Builder;
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
import android.widget.ListView;
import java.util.Arrays;
import android.view.inputmethod.InputMethodManager;

public class FavoriteTab extends Fragment{
	
	static final String SEND_LOCATION_NOTIFICATIONS = "com.skanderjabouzi.favoriteplace.SEND_LOCATION_NOTIFICATIONS";
	private EditText latitude, longitude, city, country, search;
	private Button btnsaveSettings, btnSearchFavorite;
	private int pos = 0;
	private FavoriteDataSource fdatasource;
	private Favorite favorite;
	private Context context = getActivity();
	private Intent favoriteIntent;
	private ArrayAdapter<String> adapter;
    FavoriteReceiver receiver;
    IntentFilter filter;
    View rootView;
    ListView searchList;
    List<String> locationNameList;
    private AlertDialog dialog;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.favorite, container, false);
		searchList = (ListView) rootView.findViewById(R.id.searchList);
		receiver = new FavoriteReceiver();
        filter = new IntentFilter( LocationService.LOCATION_INTENT );
        favoriteIntent = new Intent(getActivity(), LocationService.class);
        fdatasource = new FavoriteDataSource(getActivity());
		fdatasource.open();
		favorite = fdatasource.getFavorite(1);
		setFavoriteTexts();
		addListenerOnButton();
        locationNameList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, locationNameList);
		searchList.setAdapter(adapter);
        return rootView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, filter, SEND_LOCATION_NOTIFICATIONS, null);
        fdatasource.open();
    }

    @Override
    public void onPause() {
        super.onPause();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
			receiver = null;
		}
        fdatasource.close();
    }
    
    @Override
    public void onStop() {
        super.onPause();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
			receiver = null;
		}
        fdatasource.close();
    }
    
	@Override
    public void onDestroy() {
        super.onPause();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
			receiver = null;
		}
        fdatasource.close();
    }

    public void setFavoriteTexts() {
		
		latitude = (EditText) rootView.findViewById(R.id.latitude);
		latitude.setText(fmt(favorite.getLatitude()));
		longitude = (EditText) rootView.findViewById(R.id.longitude);
		longitude.setText(fmt(favorite.getLongitude()));		
		city = (EditText) rootView.findViewById(R.id.city);
		city.setText(favorite.getCity());
		country = (EditText) rootView.findViewById(R.id.country);
		country.setText(favorite.getCountry());
	}

    public void addListenerOnButton() {

		btnsaveSettings = (Button) rootView.findViewById(R.id.saveSettings);
		btnsaveSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		
				favorite.setId(1);
				latitude = (EditText) rootView.findViewById(R.id.latitude);
				favorite.setLatitude(Float.parseFloat(String.valueOf(latitude.getText())));

				longitude = (EditText) rootView.findViewById(R.id.longitude);
				favorite.setLongitude(Float.parseFloat(String.valueOf(longitude.getText())));

				city = (EditText) rootView.findViewById(R.id.city);
				favorite.setCity(String.valueOf(city.getText()));

				country = (EditText) rootView.findViewById(R.id.country);
				favorite.setCountry(String.valueOf(country.getText()));

				fdatasource.updateFavorite(favorite);
				
				getActivity().finish();
			}

		});
		
		btnSearchFavorite = (Button) rootView.findViewById(R.id.searchFavorite);
		btnSearchFavorite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LocationService.class);
				intent.putExtra("SAVE", "0");
				intent.putExtra("SOURCE", "FAVORITE");
				search = (EditText) rootView.findViewById(R.id.searchLocation);
				intent.putExtra("SEARCH", String.valueOf(search.getText()));
				getActivity().startService(intent);
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
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
	
	class FavoriteReceiver extends BroadcastReceiver {
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
				Log.i("RESULT", extraString);
				String[] geoAllfavorites = extraString.split("\\@");
				locationNameList.clear();
				for(String i : geoAllfavorites){
					String[] geofavorites = i.split("\\|");
					locationNameList.add(geofavorites[2] + " " + geofavorites[3] + " " + geofavorites[3]);
				}
				
				AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
				builder.setTitle("Colors");
			
				final CharSequence str[]={"Red","Yellow","Green","Orange","Blue"};
				
				builder.setItems(locationNameList.toArray(new String[locationNameList.size()]), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int position) {
						//TODO Auto-generated method stub
						Toast.makeText(getActivity(), "You are selected: "+str[position], Toast.LENGTH_SHORT).show();
					}
				});
				
				dialog=builder.create();
				dialog.show();
				//favorite.setLatitude(Float.parseFloat(geofavorite[0]));
				//favorite.setLongitude(Float.parseFloat(geofavorite[1]));
				//favorite.setCity(geofavorite[2]);
				//favorite.setCountry(geofavorite[3]);
				//setFavoriteTexts();
			}
        }
    }
 
}
