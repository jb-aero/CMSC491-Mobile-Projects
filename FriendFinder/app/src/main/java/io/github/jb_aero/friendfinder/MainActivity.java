package io.github.jb_aero.friendfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

	LocationManager locationManager;
	TextView debug;
	Button php, map;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		debug = (TextView) findViewById(R.id.mainDebug);
		php = (Button) findViewById(R.id.phpbutton);
		map = (Button) findViewById(R.id.mapbutton);
		handler = new Handler();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		php.setOnClickListener(this);
		map.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
			return;
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
			return;
		}
		locationManager.removeUpdates(this);
	}

	@Override
	public void onClick(View v) {

		Intent intent;
		switch (v.getId())
		{
			case R.id.phpbutton:
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				break;
			case R.id.mapbutton:
				intent = new Intent(this, MapsActivity.class);
				startActivity(intent);
				break;
		}
	}

	private class LocationWork implements Runnable {

		private double lat, lon;
		public LocationWork(Location loc) {
			lat = loc.getLatitude();
			lon = loc.getLongitude();
		}
		@Override
		public void run() {
			debug.setText(lat + " " + lon);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		handler.post(new LocationWork(location));
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
}
