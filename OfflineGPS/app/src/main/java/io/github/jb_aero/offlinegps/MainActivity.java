package io.github.jb_aero.offlinegps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

	boolean readMethod2;
	LocationManager locationManager;
	SensorManager sensorManager;
	EditText _lat1, _lat2, _long1, _long2;
	float lat1, lat2, long1, long2;

	Handler myHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		readMethod2 = true;

		_lat1 = (EditText) findViewById(R.id.lat1);
		_long1 = (EditText) findViewById(R.id.long1);
		_lat2 = (EditText) findViewById(R.id.lat2);
		_long2 = (EditText) findViewById(R.id.long2);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COURSE_LOCATION"}, 1337);
			return;
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		locationManager.removeUpdates(this);
	}

	public class LocationWork implements Runnable {

		float lat, lon;

		public LocationWork(double lati, double longi)
		{
			lat = (float) lati;
			lon = (float) longi;
		}

		@Override
		public void run() {
			_lat1.setText(String.format(Locale.US, "%f", lat));
			_long1.setText(String.format(Locale.US, "%f", lon));
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		myHandler.post(new LocationWork(location.getLatitude(), location.getLongitude()));
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
