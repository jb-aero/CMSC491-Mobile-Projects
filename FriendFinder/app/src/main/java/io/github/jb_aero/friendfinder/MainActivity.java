package io.github.jb_aero.friendfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	LocationManager locationManager;
	EditText username, password;
	Button php, map, login, register;
	Handler handler;
	Location lastLoc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		php = (Button) findViewById(R.id.phpbutton);
		map = (Button) findViewById(R.id.mapbutton);
		login = (Button) findViewById(R.id.logButton);
		register = (Button) findViewById(R.id.toRegButt);
		username = (EditText) findViewById(R.id.logUser);
		password = (EditText) findViewById(R.id.logPass);
		handler = new Handler();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		php.setOnClickListener(this);
		map.setOnClickListener(this);
		login.setOnClickListener(this);
		register.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
			return;
		}
		lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
			case R.id.logButton:
				break;
			case R.id.toRegButt:
				intent = new Intent(this, RegisterActivity.class);
				intent.putExtra("location", lastLoc);
				startActivity(intent);
				break;
		}
	}
}
