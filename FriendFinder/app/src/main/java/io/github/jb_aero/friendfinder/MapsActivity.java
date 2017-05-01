package io.github.jb_aero.friendfinder;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

	private GoogleMap mMap;
	LocationManager locationManager;
	boolean ready;
	String[] input;
	LocationUpdater updater;
	TextView debug;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		ready = false;
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		input = new String[4];
		int id = 1337;
		input[0] = String.valueOf(id);
		updater = new LocationUpdater();
		debug = (TextView) findViewById(R.id.debugtext);
		handler = new Handler();

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1337);
			return;
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 30, 0, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1337);
			return;
		}
		locationManager.removeUpdates(this);
	}


	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		ready = true;
	}

	@Override
	public void onLocationChanged(Location location) {

		input[1] = String.valueOf(location.getLatitude());
		input[2] = String.valueOf(location.getLongitude());
		updater.execute(input);

		if (ready) {
			LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
			mMap.addMarker(new MarkerOptions().position(me).title("My Location"));
			mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
			mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);
		}
	}

	private class LocationUpdater extends AsyncTask<String,Integer,String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(final String s) {
			super.onPostExecute(s);
			handler.post(new Runnable() {
				@Override
				public void run() {
					debug.setText(s);
				}
			});
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... params) {

			URL url;
			int responseCode = -1337;
			String response = "";

			StringBuilder str = new StringBuilder("http://192.168.1.151/uploaddata.php");
			str.append("?id="+params[0]);
			str.append("&latitude="+params[1]).append("&longitude="+params[2]);
			final String requestURL = str.toString();

			try
			{
				url = new URL(requestURL);
				HttpURLConnection myconnection =  (HttpURLConnection) url.openConnection();
				myconnection.setReadTimeout(15000);
				myconnection.setConnectTimeout(15000);
				myconnection.setRequestMethod("GET");
				myconnection.setDoInput(true);
				myconnection.setDoOutput(true);

				responseCode = myconnection.getResponseCode();

				if(responseCode == HttpURLConnection.HTTP_OK)
				{
					String line;
					BufferedReader br = new BufferedReader(new InputStreamReader(
							myconnection.getInputStream()));

					line = br.readLine();
					while(line != null)
					{
						response += line;
						line = br.readLine();
					}
					br.close();
				}
				myconnection.disconnect();
				Log.d("HEYLOOK", response);

			}catch(Exception e)
			{
				e.printStackTrace();
			}

			return responseCode + ": " + response;
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
