package com.skanderjabouzi.favoriteplace;

import android.app.Activity;
import android.view.MotionEvent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.text.Html;
import android.util.Log;
import android.content.Context;
import android.content.Intent;

public class DialActivity extends Activity implements SensorEventListener {

	private View dialView;
    private OnSwipeTouchListener onSwipeTouchListener;
	private ImageView image;
	private ImageView image2;
	private int sensorAccuracy;
	private float currentDegree = 0f;
	//private float currentDegree2 = 178f;
	private SensorManager mSensorManager;
	TextView compassDegree;
	TextView compassDegreeTitle;
	TextView dialDegree;	
	TextView dialDegreeTitle;	
	private LocationDataSource datasource;
	private Location location;
	private boolean background_changed = false;
	private Context context = DialActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//startService(new Intent(context, LocationService.class));
		setContentView(R.layout.dial);
        dialView = findViewById(R.id.dialView);
        
        onSwipeTouchListener = new OnSwipeTouchListener(this) {
			public void onSwipeTop() {
				//Toast.makeText(DialActivity.this, "top", Toast.LENGTH_SHORT).show();
			}
			public void onSwipeRight() {
				//Toast.makeText(DialActivity.this, "right", Toast.LENGTH_SHORT).show();
			}
			public void onSwipeLeft() {
				//Toast.makeText(DialActivity.this, "left", Toast.LENGTH_SHORT).show();

			}
			public void onSwipeBottom() {
				//Toast.makeText(DialActivity.this, "bottom", Toast.LENGTH_SHORT).show();
				DialActivity.this.startActivity(new Intent(DialActivity.this, CoverActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				DialActivity.this.finish();
				DialActivity.this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
			}
		};	
		dialView.setOnTouchListener(onSwipeTouchListener);
        
		image = (ImageView) findViewById(R.id.dial);
		image2 = (ImageView) findViewById(R.id.dial2);
		//rotate(image2, 0, 178f, 0);
		compassDegree = (TextView) findViewById(R.id.degree);
		compassDegreeTitle = (TextView) findViewById(R.id.degree_title);
		dialDegree = (TextView) findViewById(R.id.dial_degree);
		dialDegreeTitle = (TextView) findViewById(R.id.dial_degree_title);
		//dialDegree.setText(Float.toString(58.64f));
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);		
		datasource = new LocationDataSource(this);
		datasource.open();
		location = datasource.getLocation(1);
		compassDegreeTitle.setText(this.getString(R.string.titleDegree));
		dialDegreeTitle.setText(this.getString(R.string.titleDialDegree));
		dialDegree.setText(String.format("%d",(int)getdial()));
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        //finish();
        mSensorManager.unregisterListener(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (datasource.isOpen()) datasource.close();
        //finish();
        //mSensorManager.unregisterListener(this);
    }
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
        if (datasource.isOpen()) datasource.close();
        //finish();
        //mSensorManager.unregisterListener(this);
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        onSwipeTouchListener.getGestureDetector().onTouchEvent(ev); 
            return super.dispatchTouchEvent(ev);   
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		float degree = Math.round(event.values[0]);
		
		//if ((int)degree == (int)getdial())
		//{
			//background_changed = true;
			//dialLayout.setBackgroundResource(R.drawable.bg2);
		//}
		//else
		//{
			//if (background_changed)
			//{
				//background_changed = false;
				//dialLayout.setBackgroundResource(R.drawable.bg1);
			//}
		//}
		
		compassDegree.setText(String.format("%d",(int)degree));
		rotate(image, currentDegree, degree, 300);
		currentDegree = -degree;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		sensorAccuracy = accuracy;
		Log.d("SENSOR : ", String.valueOf(sensorAccuracy));
	}
	
	private void rotate(ImageView imgview, float currentDegree, float degree, int duration) {
		RotateAnimation rotateAnim = new RotateAnimation(currentDegree, -degree,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);

		rotateAnim.setDuration(duration);
		rotateAnim.setFillAfter(true);
		imgview.startAnimation(rotateAnim);
	}
	
	private float getdial()
	{    
		final float MLONG = 39.823333f;
		final float MLAT = 21.42333f;    
		//final float Math.PI = 4.0f*Math.atan(1.0f);
		
		float x1 = (float)Math.sin((-location.getLongitude()+MLONG)*Math.PI/180f);
		float y1 = (float)Math.cos(location.getLatitude()*Math.PI/180f) * (float)Math.tan(MLAT*Math.PI/180f);
		float y2 = (float)Math.sin(location.getLatitude()*Math.PI/180f) * (float)Math.cos((-location.getLongitude()+MLONG)*Math.PI/180f);
		float dial_angle = (float)Math.atan(x1/(y1-y2))*180f/(float)Math.PI;
		if (dial_angle < 0) dial_angle = 360.0f + dial_angle;
		
		if ((location.getLongitude() < MLONG) && (location.getLongitude() > MLONG-180f)) {
			if (dial_angle > 180f) dial_angle = dial_angle - 180f;
		}
		if (dial_angle > 360f) dial_angle = dial_angle - 360f;    
		return dial_angle;        
	}
}
