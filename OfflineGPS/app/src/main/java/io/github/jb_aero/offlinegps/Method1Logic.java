package io.github.jb_aero.offlinegps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.EditText;

import java.util.Locale;

public class Method1Logic implements LocationListener {

	EditText _lat1, _long1, _provider;
	float lat1, long1;

	MainActivity main;

	Method1Logic(MainActivity mainActivity) {
		main = mainActivity;
		_lat1 = (EditText) main.findViewById(R.id.lat1);
		_long1 = (EditText) main.findViewById(R.id.long1);
		_provider = (EditText) main.findViewById(R.id.provider);
	}

	public class LocationWork implements Runnable {

		Location loc;

		public LocationWork(Location location)
		{
			loc = location;
		}

		@Override
		public void run() {

			main.lastBestLoc = main.checkBestLocation(loc);
			lat1 = (float) main.lastBestLoc.getLatitude();
			long1 = (float) main.lastBestLoc.getLongitude();
			_lat1.setText(String.format(Locale.US, "%f", lat1));
			_long1.setText(String.format(Locale.US, "%f", long1));
			_provider.setText(main.lastBestLoc.getProvider());

			main.updateDistance();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		main.myHandler.post(new LocationWork(location));
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
