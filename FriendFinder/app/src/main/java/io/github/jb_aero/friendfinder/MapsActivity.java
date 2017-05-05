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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

public class MapsActivity extends FragmentActivity
		implements OnMapReadyCallback, LocationListener, View.OnClickListener {

	private GoogleMap mMap;
	LocationManager locationManager;
	Location lastLoc;
	boolean ready;
	String[] input;
	InvokeWebservice updater;
	TextView debug;
	EditText distance;
	Button spoof, center;
	Handler handler;
	SupportMapFragment mapFragment;
	MapsStringRunnable mapRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		ready = false;
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		input = new String[5];
		input[0] = "?id=" + getIntent().getStringExtra("id");
		input[3] = "&username=" + getIntent().getStringExtra("username");
		distance = (EditText) findViewById(R.id.distance);
		debug = (TextView) findViewById(R.id.debug);
		spoof = (Button) findViewById(R.id.spoof);
		spoof.setOnClickListener(this);
		center = (Button) findViewById(R.id.centerButton);
		center.setOnClickListener(this);
		handler = new Handler();
		mapRunnable = new MapsStringRunnable();

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
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
			case R.id.centerButton:
				mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude())));
				mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);
				break;
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setBuildingsEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude())));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);
		ready = true;
	}

	@Override
	public void onLocationChanged(Location location) {

		lastLoc = location;
		try {
			if (FFUtility.isBetterAndUpdate(location)) {

				double dist;

				try {
					dist = Math.abs(Double.valueOf(distance.getText().toString()));
				} catch (NumberFormatException e) {
					dist = 1;
				}

				updater = new InvokeWebservice("UpdateLocation", handler, mapRunnable);
				input[1] = "&latitude=" + location.getLatitude();
				input[2] = "&longitude=" + location.getLongitude();
				input[4] = "&distance=" + dist;
				updater.execute(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MapsStringRunnable extends StringRunnable {

		@Override
		public void run() {
			//debug.setText(theString);
			if (!ready)
				return;
			mMap.clear();
			BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			String title = "My Location";
			LatLng mloc = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
			mMap.addMarker(new MarkerOptions().position(mloc).title(title).icon(icon));
			try {
				JSONArray json = new JSONArray(theString);
				for (int i = 0; i < json.length(); i++)
				{
					JSONArray item = new JSONArray(json.getString(i));
					mloc = new LatLng(item.getDouble(2), item.getDouble(3));
					title = item.getString(0) + " @ " + item.getString(1) + "\n(" + item.getString(4) + " km)";
					icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
					mMap.addMarker(new MarkerOptions().position(mloc).title(title).icon(icon));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
