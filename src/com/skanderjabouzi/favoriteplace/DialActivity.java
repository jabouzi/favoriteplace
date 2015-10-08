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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.graphics.Color;

public class DialActivity extends Activity implements SensorEventListener {

	private View dialView;
    private OnSwipeTouchListener onSwipeTouchListener;
	private ImageView image;
	private ImageView image1;
	private ImageView image2;
	private int sensorAccuracy;
	private float currentDegree = 0f;
	private float directionDegree = 0f;
	//private float currentDegree2 = 178f;
	private SensorManager mSensorManager;
	TextView compassDegree;
	TextView compassDegreeTitle;
	TextView dialDegree;	
	TextView dialDegreeTitle;	
	TextView favoriteName;	
	TextView distanceValue;	
	private LocationDataSource ldatasource;
	private Location location;
	private FavoriteDataSource fdatasource;
	private Favorite favorite;
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
		//image1 = (ImageView) findViewById(R.id.dial1);
		image2 = (ImageView) findViewById(R.id.dial2);
		compassDegree = (TextView) findViewById(R.id.degree);
		compassDegreeTitle = (TextView) findViewById(R.id.degree_title);
		dialDegree = (TextView) findViewById(R.id.dial_degree);
		dialDegreeTitle = (TextView) findViewById(R.id.dial_degree_title);
		favoriteName = (TextView) findViewById(R.id.favoriteName);
		distanceValue = (TextView) findViewById(R.id.distanceValue);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);		
		ldatasource = new LocationDataSource(this);
		ldatasource.open();
		location = ldatasource.getLocation(1);
		fdatasource = new FavoriteDataSource(this);
		fdatasource.open();
		favorite = fdatasource.getFavorite(1);
		directionDegree = (int)getdial();
		compassDegreeTitle.setText(this.getString(R.string.titleDegree));
		dialDegreeTitle.setText(this.getString(R.string.titleDialDegree));
		dialDegree.setText(String.format("%d", (int)directionDegree));
		favoriteName.setText(String.format("%s",getFavoriteName()));
		distanceValue.setText(String.format("%s",getDistance()));
		//rotate(image2, 0, 0-(int)getdial(), 300);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
		favorite = fdatasource.getFavorite(1);
		dialDegree.setText(String.format("%d",(int)getdial()));
		favoriteName.setText(String.format("%s",getFavoriteName()));
		distanceValue.setText(String.format("%s",getDistance()));
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
        if (ldatasource.isOpen()) ldatasource.close();
        //finish();
        //mSensorManager.unregisterListener(this);
    }
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
        if (ldatasource.isOpen()) ldatasource.close();
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
		
		if ((int)degree == (int)getdial())
		{
			background_changed = true;
			favoriteName.setTextColor(Color.parseColor("#F7CE1E"));
		}
		else
		{
			if (background_changed)
			{
				background_changed = false;
				favoriteName.setTextColor(Color.parseColor("#FFFFFF"));
			}
		}
		
		compassDegree.setText(String.format("%d",(int)degree));
		//if (Math.abs((float)(int)currentDegree - (float)(int)degree) > 1)
		//{
			//rotate(image2, (float)(int)currentDegree + (float)(int)directionDegree, (float)(int)degree + (float)(int)directionDegree, 2000);
			rotate(image, (float)(int)currentDegree, (float)(int)degree, 1000);
			//Log.i("CURRENTDEGREE : ", String.valueOf((float)(int)currentDegree));
			//Log.d("DEGREE : ", String.valueOf((float)(int)degree));
			//Log.i("CURRENTDEGREE 2 : ", String.valueOf((float)(int)currentDegree + (float)(int)directionDegree));
			//Log.d("DEGREE 2 : ", String.valueOf((float)(int)degree + (float)(int)directionDegree));
			currentDegree = -degree;
		//}
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
		final float MLONG = favorite.getLongitude();
		final float MLAT = favorite.getLatitude();    
		
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
	
	private String getDistance()
	{
		final float dlon = favorite.getLongitude() - location.getLongitude();
		final float dlat = favorite.getLatitude() - location.getLatitude();
		double a = Math.pow(Math.sin(dlat/2),2) + Math.cos(location.getLatitude()) * Math.cos(favorite.getLatitude()) * Math.pow(Math.sin(dlon/2),2);
		double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a)); 
		double d = (double)6373 * c;
		
		return this.getString(R.string.distanceTitle) + " " + String.valueOf((int)d) + " KM";
	}
	
	private String getFavoriteName()
	{
		return favorite.getCity() + " " + favorite.getCountry();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  getMenuInflater().inflate(R.menu.menu, menu);
	  return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	  switch (item.getItemId()) {
	  case R.id.settings:
		startActivity(new Intent(this, SettingsActivity.class)
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
