package com.skanderjabouzi.favoriteplace;

import android.app.Activity;
import android.os.Build;
//import android.support.annotation.RequiresApi;
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
import android.text.TextUtils;
import android.animation.ObjectAnimator;
import java.util.ArrayList;
import java.util.List;
import android.view.animation.LinearInterpolator;
import android.animation.AnimatorSet;

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
//		image1 = (ImageView) findViewById(R.id.pointer);
		image2 = (ImageView) findViewById(R.id.pointer2);
		compassDegree = (TextView) findViewById(R.id.currentAngleValue);
		dialDegree = (TextView) findViewById(R.id.favoriteAngleValue);
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
		dialDegree.setText(String.format("%d", (int) directionDegree));
		favoriteName.setText(String.format("%s",getFavoriteName()));
		distanceValue.setText(String.format("%s",getDistance()));
//		rotate(image2, 0, 0 - (int) getdial(), 300);
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
    public boolean onTouchEvent(MotionEvent event) {
          // TODO Auto-generated method stub
		DialActivity.this.openOptionsMenu();
		return super.onTouchEvent(event);
   }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        onSwipeTouchListener.getGestureDetector().onTouchEvent(ev); 
            return super.dispatchTouchEvent(ev);   
    }

	//@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
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
		if (Math.abs((int)currentDegree - (int)degree) > 5)
		{
            /*List<Integer> ranges = new ArrayList<Integer>();
            List<Integer> degreesRange = tearDown((int)currentDegree,(int)degree, ranges);
            Log.d("SENSOR : ", String.valueOf(sensorAccuracy));
            for (int i = 0; i < degreesRange.size() - 1; i++)
            {

                rotate(image, -(float)(int)degreesRange.get(i), (float)(int)degreesRange.get(i + 1), getdial(), 1000 + i*1000);
            }*/

            degree *= -1;
            rotate(image, (float)(int)currentDegree, (float)(int)degree, getdial(), 2000);
//			Log.i("CURRENTDEGREE : ", String.valueOf((float)(int)currentDegree));
//			Log.d("DEGREE : ", String.valueOf((float)(int)degree));
//            Log.d("DEGREES RANGE : ", String.valueOf(degreesRange));
			currentDegree = degree;

		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		sensorAccuracy = accuracy;
		Log.d("SENSOR : ", String.valueOf(sensorAccuracy));
	}

	//@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void rotate(ImageView imgview, float currentDegree, float degree, float direction, int duration) {

        Log.i("ROTATE CURRENT : ", String.valueOf(currentDegree));
        Log.d("ROTATE DEGREE : ", String.valueOf(degree));
        Log.d("ROTATE DURATION : ", String.valueOf(duration));
        if (Math.abs((int)currentDegree - (int)degree) > 1) {
            List<Integer> ranges = new ArrayList<Integer>();
            List<Integer> degreesRange = tearDown((int) currentDegree, (int) degree, ranges);
            Log.d("SENSOR : ", String.valueOf(sensorAccuracy));
            Log.d("DEGREES RANGE : ", String.valueOf(degreesRange));
            for (int i = 0; i < degreesRange.size() - 1; i++) {
                ArrayList<ObjectAnimator> arrayListObjectAnimators = new ArrayList<ObjectAnimator>();
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(image, "rotation", (float) (int) degreesRange.get(i), (float) (int) degreesRange.get(i + 1));
                arrayListObjectAnimators.add(anim1);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(image2, "rotation", (float) (int) degreesRange.get(i) + direction, (float) (int) degreesRange.get(i + 1) + direction);
                arrayListObjectAnimators.add(anim2);

                ObjectAnimator[] objectAnimators = arrayListObjectAnimators.toArray(new ObjectAnimator[arrayListObjectAnimators.size()]);
                AnimatorSet animSetXY = new AnimatorSet();
                animSetXY.playTogether(objectAnimators);
                animSetXY.setInterpolator(new LinearInterpolator());
                animSetXY.setDuration((long) duration - i * 100);
                animSetXY.start();
//                rotate(image, (float)(int)degreesRange.get(i), (float)(int)degreesRange.get(i + 1), getdial(), 1000 + i*1000);
            }

        }
	}
	
	private float getdial()
	{    
		final float FLONG = getFloatValue(favorite.getLongitude());
		final float FLAT = getFloatValue(favorite.getLatitude());
		final float LLONG = getFloatValue(location.getLongitude());
		final float LLAT = getFloatValue(location.getLatitude());

		float x1 = (float)Math.sin((-LLONG+FLONG)*Math.PI/180f);
		float y1 = (float)Math.cos(LLAT*Math.PI/180f) * (float)Math.tan(FLAT*Math.PI/180f);
		float y2 = (float)Math.sin(LLAT*Math.PI/180f) * (float)Math.cos((-LLONG+FLONG)*Math.PI/180f);
		float dial_angle = (float)Math.atan(x1/(y1-y2))*180f/(float)Math.PI;
		if (dial_angle < 0) dial_angle = 360.0f + dial_angle;
		
		if ((LLONG < FLONG) && (LLONG > FLONG-180f)) {
			if (dial_angle > 180f) dial_angle = dial_angle - 180f;
		}
		if (dial_angle > 360f) dial_angle = dial_angle - 360f;    
		return dial_angle;        
	}
	
	private String getDistance()
	{
		final float FLONG = getFloatValue(favorite.getLongitude());
		final float FLAT = getFloatValue(favorite.getLatitude());
		final float LLONG = getFloatValue(location.getLongitude());
		final float LLAT = getFloatValue(location.getLatitude());

		final float dlon = FLONG - LLONG;
		final float dlat = FLAT - LLAT;
		
		double a = Math.pow(Math.sin(dlat/2),2) + Math.cos(LLAT) * Math.cos(FLAT) * Math.pow(Math.sin(dlon/2),2);
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
		  startActivity(new Intent(this, AboutActivity.class)
				  .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		break;
	  }
	  return true;
	}

	public float getFloatValue(String str)
	{
		if(str != null && !str.isEmpty())
		{
			return Float.parseFloat(str);
		}
		else
		{
			return 0f;
		}
	}

    public static List<Integer> tearDown(int start, int end, List<Integer> ranges)
    {
        ranges.add(start);
        if (Math.abs(end - start) > 1)
        {

            tearDown((start + end)/2, end, ranges);
        }
        else
        {
            ranges.add(end);
        }

        return ranges;
    }
}
