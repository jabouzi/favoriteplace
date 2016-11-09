package com.skanderjabouzi.favoriteplace;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class SettingsActivity extends ActionBarActivity {
	// Declare Tab Variable
	ActionBar.Tab Tab1, Tab2;
	LocationTab locationTab = new LocationTab();
    FavoriteTab favoriteTab = new FavoriteTab();

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpTabs(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //            save the selected tab's index so it's re-selected on orientation change
        outState.putInt("tabIndex", getSupportActionBar().getSelectedNavigationIndex());
    }

    private void setUpTabs(Bundle savedInstanceState) {
		ActionBar actionBar = getSupportActionBar();
		Log.d("SETTINGS", String.valueOf(actionBar));

		// Hide Actionbar Icon
		actionBar.setDisplayShowHomeEnabled(false);

		// Hide Actionbar Title
		actionBar.setDisplayShowTitleEnabled(false);

		// Create Actionbar Tabs
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set Tab Icon and Titles
        Tab Tab1 = actionBar.newTab().setText(this.getString(R.string.titleLocationTab));
        Tab Tab2 = actionBar.newTab().setText(this.getString(R.string.titleFavoriteTab));

		// Set Tab Listeners
		Tab1.setTabListener(new TabListener(locationTab));
		Tab2.setTabListener(new TabListener(favoriteTab));

		// Add tabs to actionbar
		actionBar.addTab(Tab1);
		actionBar.addTab(Tab2);

	}
}
