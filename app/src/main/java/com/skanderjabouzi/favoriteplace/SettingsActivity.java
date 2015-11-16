package com.skanderjabouzi.favoriteplace;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class SettingsActivity extends Activity {
	// Declare Tab Variable
	ActionBar.Tab Tab1, Tab2;
	Fragment locationTab = new LocationTab();
	Fragment favoriteTab = new FavoriteTab();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar actionBar = getActionBar();
		Log.d("SETTINGS", String.valueOf(actionBar));

		// Hide Actionbar Icon
		actionBar.setDisplayShowHomeEnabled(false);

		// Hide Actionbar Title
		actionBar.setDisplayShowTitleEnabled(false);

		// Create Actionbar Tabs
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set Tab Icon and Titles
		Tab1 = actionBar.newTab().setText(this.getString(R.string.titleLocationTab));
		Tab2 = actionBar.newTab().setText(this.getString(R.string.titleFavoriteTab));

		// Set Tab Listeners
		Tab1.setTabListener(new TabListener(locationTab));
		Tab2.setTabListener(new TabListener(favoriteTab));

		// Add tabs to actionbar
		actionBar.addTab(Tab1);
		actionBar.addTab(Tab2);

	}
}
