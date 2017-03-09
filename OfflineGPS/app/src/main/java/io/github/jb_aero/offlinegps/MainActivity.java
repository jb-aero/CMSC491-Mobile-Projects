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

	LocationManager locationManager;
	SensorManager sensorManager;
	EditText _lat1, _lat2, _long1, _long2, _method;
	float lat1, lat2, long1, long2, rotx, roty, rotz;

	final float TWO_MINUTES = 1000 * 60 * 2;

	Location lastBestLoc;
	Sensor accelerometer, gyroscope, compass;

	boolean hasLocation, hasCompass;

	Handler myHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		hasLocation = false;
		hasCompass = false;

		_lat1 = (EditText) findViewById(R.id.lat1);
		_long1 = (EditText) findViewById(R.id.long1);
		_lat2 = (EditText) findViewById(R.id.lat2);
		_long2 = (EditText) findViewById(R.id.long2);
		_method = (EditText) findViewById(R.id.method);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

		sensorManager.registerListener(this, accelerometer, 0);
		sensorManager.registerListener(this, gyroscope, 0);
		sensorManager.registerListener(this, compass, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		locationManager.removeUpdates(this);
		sensorManager.unregisterListener(this);
	}

	public class LocationWork implements Runnable {

		Location loc;

		public LocationWork(Location location)
		{
			loc = location;
		}

		@Override
		public void run() {

			lastBestLoc = checkBestLocation(loc);
			lat1 = (float) lastBestLoc.getLatitude();
			long1 = (float) lastBestLoc.getLongitude();
			_lat1.setText(String.format(Locale.US, "%f", lat1));
			_long1.setText(String.format(Locale.US, "%f", long1));
			_method.setText(lastBestLoc.getProvider());
		}
	}

	public Location checkBestLocation(Location rec) {

		float timeDiff = rec.getTime() - lastBestLoc.getTime();
		float accuracyDiff = rec.getAccuracy() - lastBestLoc.getTime();

		if (timeDiff > TWO_MINUTES) {
			if (!(hasCompass && hasLocation)) {
				lat2 = (float) rec.getLatitude();
				long2 = (float) rec.getLongitude();
				hasLocation = true;
			}
			return rec;
		}

		if (timeDiff > 0 && accuracyDiff < 0) {
			if (!(hasCompass && hasLocation)) {
				lat2 = (float) rec.getLatitude();
				long2 = (float) rec.getLongitude();
				hasLocation = true;
			}
			return rec;
		}

		return lastBestLoc;
	}

	@Override
	public void onLocationChanged(Location location) {
		myHandler.post(new LocationWork(location));
	}

	private class AccelWork implements Runnable {

		@Override
		public void run() {

		}
	}

	private class GyroWork implements Runnable {

		@Override
		public void run() {

		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType())
		{
			case Sensor.TYPE_LINEAR_ACCELERATION:
				myHandler.post(new AccelWork());
				break;
			case Sensor.TYPE_GYROSCOPE:
				myHandler.post(new GyroWork());
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				break;
			default:
				break;
		}
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
