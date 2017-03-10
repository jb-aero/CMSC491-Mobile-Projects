package io.github.jb_aero.offlinegps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

	final int EARTH_RADIUS = 6371000;
	final float TWO_MINUTES = 1000 * 60 * 2;
	Location lastBestLoc;
	LocationManager locationManager;
	SensorManager sensorManager;

	// The sensors we will use
	Sensor accelerometer, gyroscope, compass;

	// A handler to prevent race conditions
	Handler myHandler = new Handler();

	Method1Logic method1;
	Method2Logic method2;
	EditText distance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		method1 = new Method1Logic(this);
		method2 = new Method2Logic(this);
		distance = (EditText) findViewById(R.id.difference);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		// This will unregister itself
		sensorManager.registerListener(method2, compass, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check if we have permission, and request it if we don't
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COURSE_LOCATION"}, 1337);
			return;
		}
		lastBestLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, method1);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, method1);

		sensorManager.registerListener(method2, accelerometer, 0);
		sensorManager.registerListener(method2, gyroscope, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		// Unregister any listening we're doing on pause
		locationManager.removeUpdates(method1);
		sensorManager.unregisterListener(method2);
	}

	public Location checkBestLocation(Location rec) {

		float timeDiff = rec.getTime() - lastBestLoc.getTime();
		float accuracyDiff = rec.getAccuracy() - lastBestLoc.getTime();

		// if the previous time is a certain age, replace it anyway
		if (timeDiff > TWO_MINUTES) {
			if (!(method2.hasCompass && method2.hasLocation)) {
				method2.lat2 = (float) rec.getLatitude();
				method2.long2 = (float) rec.getLongitude();
				method2.hasLocation = true;
			}
			return rec;
		}

		// If the next location is newer and at least as accurate, replace the old one
		if (timeDiff > 0 && accuracyDiff <= 0) {
			if (!(method2.hasCompass && method2.hasLocation)) {
				method2.lat2 = (float) rec.getLatitude();
				method2.long2 = (float) rec.getLongitude();
				method2.hasLocation = true;
			}
			return rec;
		}

		return lastBestLoc;
	}

	void updateDistance()
	{
		float dlon = method2.long2 - method1.long1;
		float dlat = method2.lat2 - method1.lat1;
		double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(method1.lat1) * Math.cos(method2.lat2) * Math.pow(Math.sin(dlon/2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		distance.setText(String.valueOf(EARTH_RADIUS * c));
	}
}
