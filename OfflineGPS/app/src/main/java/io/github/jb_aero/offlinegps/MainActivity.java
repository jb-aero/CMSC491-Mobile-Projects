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

public class MainActivity extends AppCompatActivity {

	final float TWO_MINUTES = 1000 * 60 * 2;
	Location lastBestLoc;
	LocationManager locationManager;
	SensorManager sensorManager;

	Sensor accelerometer, gyroscope, compass;

	boolean hasLocation, hasCompass;

	Handler myHandler = new Handler();

	Method1Logic method1;
	Method2Logic method2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		hasLocation = false;
		hasCompass = false;

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
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, method1);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, method1);

		sensorManager.registerListener(method2, accelerometer, 0);
		sensorManager.registerListener(method2, gyroscope, 0);
		sensorManager.registerListener(method2, compass, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		locationManager.removeUpdates(method1);
		sensorManager.unregisterListener(method2);
	}

	public Location checkBestLocation(Location rec) {

		float timeDiff = rec.getTime() - lastBestLoc.getTime();
		float accuracyDiff = rec.getAccuracy() - lastBestLoc.getTime();

		if (timeDiff > TWO_MINUTES) {
			if (!(hasCompass && hasLocation)) {
				//lat2 = (float) rec.getLatitude();
				//long2 = (float) rec.getLongitude();
				hasLocation = true;
			}
			return rec;
		}

		if (timeDiff > 0 && accuracyDiff < 0) {
			if (!(hasCompass && hasLocation)) {
				//lat2 = (float) rec.getLatitude();
				//long2 = (float) rec.getLongitude();
				hasLocation = true;
			}
			return rec;
		}

		return lastBestLoc;
	}


}
