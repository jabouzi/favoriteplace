package com.skanderjabouzi.favoriteplace;

import android.view.View;
import android.view.MotionEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.Display;
import android.util.Log;
import android.widget.Toast;

public class CoverActivity extends Activity {

	IntentFilter filter;
	View coverView;
	OnSwipeTouchListener onSwipeTouchListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		coverView = findViewById(R.id.coverView);

		onSwipeTouchListener = new OnSwipeTouchListener(this) {
			public void onSwipeTop() {
				//Toast.makeText(CoverAvtivity.this, "top", Toast.LENGTH_SHORT).show();
			}
			public void onSwipeRight() {
				//Toast.makeText(CoverAvtivity.this, "right", Toast.LENGTH_SHORT).show();
				CoverAvtivity.this.startActivity(new Intent(CoverAvtivity.this, DialAvtivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				CoverAvtivity.this.finish();
				CoverAvtivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
			public void onSwipeLeft() {
				//Toast.makeText(CoverAvtivity.this, "left", Toast.LENGTH_SHORT).show();
				CoverAvtivity.this.startActivity(new Intent(CoverAvtivity.this, DialAvtivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				CoverAvtivity.this.finish();
				CoverAvtivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
			public void onSwipeBottom() {
				//Toast.makeText(CoverAvtivity.this, "bottom", Toast.LENGTH_SHORT).show();
			}
		};
		coverView.setOnTouchListener(onSwipeTouchListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		onSwipeTouchListener.getGestureDetector().onTouchEvent(ev);
			return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  getMenuInflater().inflate(R.menu.menu, menu);
	  return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	  switch (item.getItemId()) {
	  case R.id.hijri:
		startActivity(new Intent(this, HijriActivity.class)
			.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		break;
	  case R.id.settings:
		startActivity(new Intent(this, SettingsActivity.class)
			.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		break;
	  case R.id.qibla:
		startActivity(new Intent(this, coverQibla.class)
			.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		break;
	  case R.id.about:
		AboutDialog about = new AboutDialog(this);
		about.setTitle(this.getString(R.string.about));
		about.show();
		break;
	  }
	  return true;
	}

}