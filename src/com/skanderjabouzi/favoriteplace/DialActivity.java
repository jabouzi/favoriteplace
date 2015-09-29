package com.skanderjabouzi.favoriteplace;

import android.view.View;
import android.view.MotionEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Display;
import android.util.Log;
import android.widget.Toast;

public class DialActivity extends Activity {

    static final String SEND_SALATTIME_NOTIFICATIONS = "com.skanderjabouzi.favoriteplace.SEND_SALATTIME_NOTIFICATIONS";
    String sataTimes[] = new String[7];
    String[] hijriDates = new String[4];
    IntentFilter filter;
    View dialView;
    OnSwipeTouchListener onSwipeTouchListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dial);
        dialView = findViewById(R.id.dialView);
        
        onSwipeTouchListener = new OnSwipeTouchListener(this) {
			public void onSwipeTop() {
				//Toast.makeText(DialActivity.this, "top", Toast.LENGTH_SHORT).show();
			}
			public void onSwipeRight() {
				//Toast.makeText(DialActivity.this, "right", Toast.LENGTH_SHORT).show();
				DialActivity.this.startActivity(new Intent(DialActivity.this, CoverActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				DialActivity.this.finish();
				DialActivity.this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
			}
			public void onSwipeLeft() {
				//Toast.makeText(DialActivity.this, "left", Toast.LENGTH_SHORT).show();
				DialActivity.this.startActivity(new Intent(DialActivity.this, CoverActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				DialActivity.this.finish();
				DialActivity.this.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
			}
			public void onSwipeBottom() {
				//Toast.makeText(DialActivity.this, "bottom", Toast.LENGTH_SHORT).show();
			}
		};	
		dialView.setOnTouchListener(onSwipeTouchListener);
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
}
