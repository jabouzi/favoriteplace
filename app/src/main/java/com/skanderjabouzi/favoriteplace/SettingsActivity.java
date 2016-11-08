package com.skanderjabouzi.favoriteplace;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;

import static android.app.ActionBar.NAVIGATION_MODE_TABS;

public class SettingsActivity extends AppCompatActivity {
	// Declare Tab Variable
	android.support.v7.app.ActionBar.Tab Tab1;
    android.support.v7.app.ActionBar.Tab Tab2;
	Fragment locationTab = new LocationTab();
	Fragment favoriteTab = new FavoriteTab();

	@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		Log.d("SETTINGS", String.valueOf(actionBar));

		// Hide Actionbar Icon
		actionBar.setDisplayShowHomeEnabled(false);

		// Hide Actionbar Title
		actionBar.setDisplayShowTitleEnabled(false);

		// Create Actionbar Tabs
		actionBar.setNavigationMode(NAVIGATION_MODE_TABS);

		// Set Tab Icon and Titles
		Tab1 = actionBar.newTab().setText(this.getString(R.string.titleLocationTab));
		Tab2 = actionBar.newTab().setText(this.getString(R.string.titleFavoriteTab));

		// Set Tab Listeners
		Tab1.setTabListener((android.support.v7.app.ActionBar.TabListener) new TabListener(locationTab));
		Tab2.setTabListener((android.support.v7.app.ActionBar.TabListener) new TabListener(favoriteTab));

		// Add tabs to actionbar
		actionBar.addTab(Tab1);
		actionBar.addTab(Tab2);

	}
}
