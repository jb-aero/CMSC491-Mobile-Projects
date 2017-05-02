package io.github.jb_aero.friendfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

public class MapsActivity extends FragmentActivity
		implements OnMapReadyCallback, LocationListener, View.OnClickListener {

	private GoogleMap mMap;
	LocationManager locationManager;
	boolean ready;
	String[] input;
	InvokeWebservice updater;
	TextView debug;
	Button spoof;
	Handler handler;
	SupportMapFragment mapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		ready = false;
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		input = new String[4];
		int id = 1337;
		input[0] = "?id=" + id;
		debug = (TextView) findViewById(R.id.debugtext);
		spoof = (Button) findViewById(R.id.spoof);
		spoof.setOnClickListener(this);
		handler = new Handler();

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
			return;
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 0, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 0, this);
		mapFragment.getMapAsync(this);
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
		ready = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.spoof:
				onLocationChanged(LocationSpoof.spoof());
				break;
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setBuildingsEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		ready = true;
	}

	@Override
	public void onLocationChanged(Location location) {

		try {
			if (FFUtility.isBetterAndUpdate(location)) {

				updater = new InvokeWebservice("uploaddata", handler, new MapsStringRunnable());
				input[1] = "&latitude=" + location.getLatitude();
				input[2] = "&longitude=" + location.getLongitude();
				updater.execute(input);

				if (ready) {
					LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
					mMap.addMarker(new MarkerOptions().position(me).title("My Location"));
					mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
					mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MapsStringRunnable extends StringRunnable {

		@Override
		public void run() {
			StringBuilder objs = new StringBuilder();
			try {
				JSONArray json = new JSONArray(theString);
				for (int i = 0; i < json.length(); i++) {
					objs.append("|" + json.getJSONArray(i) + "|");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			debug.setText(theString + "\n" + objs.toString());
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
}
